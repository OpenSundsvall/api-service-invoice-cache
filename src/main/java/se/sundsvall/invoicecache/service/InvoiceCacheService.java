package se.sundsvall.invoicecache.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import se.sundsvall.invoicecache.api.model.Invoice;
import se.sundsvall.invoicecache.api.model.InvoiceMapper;
import se.sundsvall.invoicecache.api.model.InvoicePDF;
import se.sundsvall.invoicecache.api.model.InvoiceRequest;
import se.sundsvall.invoicecache.api.model.InvoicesResponse;
import se.sundsvall.invoicecache.api.model.MetaData;
import se.sundsvall.invoicecache.integration.db.InvoiceEntityRepository;
import se.sundsvall.invoicecache.integration.db.entity.InvoiceEntity;
import se.sundsvall.invoicecache.integration.db.specifications.InvoiceSpecifications;
import se.sundsvall.invoicecache.integration.party.PartyClient;

@Service
public class InvoiceCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceCacheService.class);
    
    private final InvoiceEntityRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceSpecifications invoiceSpecifications;
    private final Scheduler scheduler;
    private final PartyClient partyClient;

    public InvoiceCacheService(final InvoiceEntityRepository repository, final InvoiceMapper mapper,
            final InvoiceSpecifications invoiceSpecifications, Scheduler scheduler, PartyClient partyClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.invoiceSpecifications = invoiceSpecifications;
        this.scheduler = scheduler;
        this.partyClient = partyClient;
    }
    
    public InvoicesResponse getInvoices(InvoiceRequest request) {
        final InvoicesResponse response = new InvoicesResponse();

        //Key: partyId, Value: legalId. Used later for mapping back which invoice belongs to which partyId
        Map<String, String> legalIdPartyIdMap = new HashMap<>();

        //Fetch legalIds (if any) from party service and set them in the request.
        if(!CollectionUtils.isEmpty(request.getPartyIds())) {
            request.getPartyIds().forEach(partyId -> {
                String legalId = partyClient.getLegalIdsFromParty(partyId);

                //Store each legalId in a hashmap
                legalIdPartyIdMap.put(legalId, partyId);
            });

            //Set the fetched legalIds in the request.
            request.setLegalIds(legalIdPartyIdMap.keySet().stream().toList());
            LOG.info("legalIds to look for: {}", request.getLegalIds());
        }

        //Find all invoices matching the request, map them and add them to the response.
        Page<InvoiceEntity> invoicePage = repository.findAll(invoiceSpecifications.createInvoicesSpecification(request), getPagingParameters(request));

        LOG.info("Got {} invoices from the DB.", invoicePage.getTotalElements());
        invoicePage.forEach(entity -> {
                    Invoice invoice = mapper.entityToInvoice(entity);
                    invoice.setPartyId(legalIdPartyIdMap.getOrDefault(invoice.getLegalId(), null));
                    response.addInvoice(invoice);
                });

        //Set pagination data
        response.setMetaData(createMetaData(request, invoicePage));
        return response;
    }

    MetaData createMetaData(InvoiceRequest request, Page<InvoiceEntity> page) {
        return MetaData.builder()
                .withPage(request.getPage())
                .withLimit(request.getLimit())
                .withTotalRecords(page.getTotalElements())
                .withCount(page.getNumberOfElements())
                .withTotalPages(page.getTotalPages())
                .build();
    }

    /**
     * To mimic the api for Invoices, a page starts with "1" and that is also the minimum value in the api.
     * The PageRequest originally has a 0-based page index, subtract 1 for which page to request.
     * @param request
     * @return+
     */
    private Pageable getPagingParameters(InvoiceRequest request) {
        return PageRequest.of(request.getPage() - 1, request.getLimit());
    }

    //The following methods are only to be used when there's a need to manipulate invoices via actuators in se.sundsvall.invoicecache.api.batchactuator
    public void forceFetchInvoices() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        scheduler.fetchInvoices();
    }

    //Only to be used by actuators
    public void forceCreateBackup() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        scheduler.runBackup();
    }

    //Only to be used by actuators
    public void forceRestoreBackup() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        scheduler.restoreBackup();
    }
}
