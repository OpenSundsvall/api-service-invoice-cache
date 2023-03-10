package se.sundsvall.invoicecache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.invoicecache.api.model.Invoice;
import se.sundsvall.invoicecache.api.model.InvoiceMapper;
import se.sundsvall.invoicecache.api.model.InvoiceRequest;
import se.sundsvall.invoicecache.api.model.InvoicesResponse;
import se.sundsvall.invoicecache.api.model.MetaData;
import se.sundsvall.invoicecache.integration.db.InvoiceEntityRepository;
import se.sundsvall.invoicecache.integration.db.entity.InvoiceEntity;
import se.sundsvall.invoicecache.integration.db.specifications.InvoiceSpecifications;
import se.sundsvall.invoicecache.integration.party.PartyClient;

@ExtendWith(MockitoExtension.class)
class InvoiceCacheServiceTest {
    
    @Mock
    private InvoiceEntityRepository mockRepository;
    @Mock
    private InvoiceMapper mockMapper;
    @Mock
    private InvoiceSpecifications mockInvoiceSpecifications;
    @Mock
    private Specification<InvoiceEntity> mockSpecification;
    @Mock
    private Scheduler mockScheduler;
    @Mock
    private PartyClient mockPartyClient;
    
    @InjectMocks
    private InvoiceCacheService service;

    private final Page<InvoiceEntity> invoicePage = new PageImpl<>(Arrays.asList(generateMinimalInvoiceEntity("7001011234"), generateMinimalInvoiceEntity("7001011235")));
    
    @Test
    void testGetInvoices() {
        when(mockPartyClient.getLegalIdsFromParty(eq("ab123"))).thenReturn("197001011234");
        when(mockPartyClient.getLegalIdsFromParty(eq("cde345"))).thenReturn("197001011235");
        when(mockInvoiceSpecifications.createInvoicesSpecification(any(InvoiceRequest.class))).thenReturn(mockSpecification);
        when(mockRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(invoicePage);
        when(mockMapper.entityToInvoice(any(InvoiceEntity.class))).thenReturn(new Invoice());
    
        final InvoicesResponse response = service.getInvoices(generateMinimalInvoiceRequest());

        assertNotNull(response);
        assertEquals(2, response.getInvoices().size());
        verify(mockPartyClient, times(2)).getLegalIdsFromParty(anyString());
        verify(mockInvoiceSpecifications, times(1)).createInvoicesSpecification(any(InvoiceRequest.class));
        verify(mockRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(mockMapper, times(2)).entityToInvoice(any(InvoiceEntity.class));
    }

    @Test
    void testCreateMetadata() {
        MetaData metadata = service.createMetaData(generateMinimalInvoiceRequest(), invoicePage);
        assertEquals(1, metadata.getPage());
        assertEquals(100, metadata.getLimit());
        assertEquals(2, metadata.getTotalRecords());
        assertEquals(2, metadata.getCount());
        assertEquals(1, metadata.getTotalPages());
    }

    @Test
    void testForceFetchInvoices() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        when(mockScheduler.fetchInvoices()).thenReturn(new JobExecution(1L));
        service.forceFetchInvoices();
        verify(mockScheduler, times(1)).fetchInvoices();
    }

    @Test
    void testForceRunBackup() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        doNothing().when(mockScheduler).runBackup();
        service.forceCreateBackup();
        verify(mockScheduler, times(1)).runBackup();
    }

    @Test
    void testForceRestoreBackup() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        doNothing().when(mockScheduler).restoreBackup();
        service.forceRestoreBackup();
        verify(mockScheduler, times(1)).restoreBackup();
    }

    private InvoiceRequest generateMinimalInvoiceRequest() {
        InvoiceRequest request = new InvoiceRequest();
        request.setPartyIds(List.of("ab123", "cde345"));

        return request;
    }

    private InvoiceEntity generateMinimalInvoiceEntity(String orgNumber) {
        return InvoiceEntity.builder()
                .withOrganizationNumber(orgNumber)
                .build();
    }
}