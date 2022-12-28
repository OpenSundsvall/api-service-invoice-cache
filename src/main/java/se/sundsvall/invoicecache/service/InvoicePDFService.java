package se.sundsvall.invoicecache.service;

import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import se.sundsvall.invoicecache.api.model.InvoicePDF;
import se.sundsvall.invoicecache.integration.db.PdfRepository;
import se.sundsvall.invoicecache.integration.db.entity.PdfEntity;
import se.sundsvall.invoicecache.integration.smb.SMBIntegration;

@Service
public class InvoicePDFService {

    private final PdfRepository pdfRepository;
    private final SMBIntegration smbIntegration;

    public InvoicePDFService(PdfRepository pdfRepository, SMBIntegration smbIntegration) {
        this.pdfRepository = pdfRepository;
        this.smbIntegration = smbIntegration;
    }


    public InvoicePDF getInvoicePdf(String filename) {
        try {
            return Optional.ofNullable(pdfRepository
                            .findByFilename(filename))
                    .map(this::toResponse)
                    .orElseGet(() -> toResponse(smbIntegration.findPDF(filename)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InvoicePDF toResponse(PdfEntity entity) {
        byte[] blobBytes;
        try {
            blobBytes = entity.getDocument().getBytes(1, (int) entity.getDocument().length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return InvoicePDF.builder()
                .withName(entity.getFilename())
                .withContent(Base64.getEncoder().encodeToString(blobBytes))
                .withContentType("application/pdf")
                .build();
    }
}
