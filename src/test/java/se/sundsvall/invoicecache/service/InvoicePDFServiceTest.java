package se.sundsvall.invoicecache.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.hibernate.engine.jdbc.BlobProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.invoicecache.integration.db.PdfRepository;
import se.sundsvall.invoicecache.integration.db.entity.PdfEntity;


@ExtendWith(MockitoExtension.class)
class InvoicePDFServiceTest {

    @Mock
    PdfRepository mockRepository;

    @InjectMocks
    InvoicePDFService pdfService;

    @Test
    void getInvoicePdf() {

        when(mockRepository.findByFilename(any(String.class))).thenReturn(generatePDFEntity());

     var response =   pdfService.getInvoicePdf("someFileName");

     assertEquals(response.getContentType(), "application/pdf");
     assertEquals(response.getName(), "someFileName");
     assertEquals(response.getContent(), Base64.getEncoder().encodeToString("blobMe".getBytes()));


    }


    @Test
    void getInvoicePdf_throwsException(){
        when(mockRepository.findByFilename(any(String.class))).thenThrow(new RuntimeException());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> pdfService.getInvoicePdf("someFileName"));
    }

  PdfEntity generatePDFEntity(){
        return PdfEntity.builder()
                .withFilename("someFileName")
                .withDocument(BlobProxy.generateProxy("blobMe".getBytes()))
                .withId(1)
                .build();
  }
}