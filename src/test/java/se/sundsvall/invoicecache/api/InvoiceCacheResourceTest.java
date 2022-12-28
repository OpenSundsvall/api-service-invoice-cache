package se.sundsvall.invoicecache.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.invoicecache.api.model.Invoice;
import se.sundsvall.invoicecache.api.model.InvoicePDF;
import se.sundsvall.invoicecache.api.model.InvoiceRequest;
import se.sundsvall.invoicecache.api.model.InvoicesResponse;
import se.sundsvall.invoicecache.service.InvoiceCacheService;
import se.sundsvall.invoicecache.service.InvoicePDFService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class InvoiceCacheResourceTest {
    
    @Mock
    private InvoiceCacheService mockService;

    @Mock
    private InvoicePDFService mockPdfService;
    
    @InjectMocks
    private InvoiceCacheResource resource;
    
    @Test
    void testGetInvoicesSuccessfulRequest_shouldReturnResponse() {
        final InvoicesResponse invoicesResponse = new InvoicesResponse();
        invoicesResponse.addInvoice(new Invoice()); //Fake that we got an invoice
        
        when(mockService.getInvoices(any(InvoiceRequest.class))).thenReturn(invoicesResponse);
    
        final ResponseEntity<InvoicesResponse> response = resource.getInvoices(generateRequest());
        verify(mockService, times(1)).getInvoices(any(InvoiceRequest.class));
        verifyNoInteractions(mockPdfService);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void testGetInvoicesFailedRequestValidation_shouldThrowException() {
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> resource.getInvoices(new InvoiceRequest()))
                .withMessage("One of legalIds, invoiceNumbers or ocrNumber needs to be set.");
        verify(mockService, times(0)).getInvoices(any(InvoiceRequest.class));
    }
    
    private InvoiceRequest generateRequest() {
        InvoiceRequest request = new InvoiceRequest();
        request.setPartyIds(List.of("5533221234"));
        return request;
    }

    @Test
    void testGetPdfSuccessfulRequest_shouldReturnResponse(){
        when(mockPdfService.getInvoicePdf(any(String.class))).thenReturn(generateInvoicePdfResponse());


        final ResponseEntity<InvoicePDF> response = resource.getInvoicePDF("fileName");
        verify(mockPdfService, times(1)).getInvoicePdf(any(String.class));
        verifyNoInteractions(mockService);
        verifyNoMoreInteractions(mockPdfService);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        var responsebody = response.getBody();
        assertEquals(responsebody.getName(), "someName");
        assertEquals(responsebody.getContent(),"someContent");
        assertEquals(responsebody.getContentType(), "application/pdf");
    }

    private InvoicePDF generateInvoicePdfResponse(){
        return InvoicePDF.builder()
                .withContent("someContent")
                .withName("someName")
                .withContentType("application/pdf")
                .build();
    }
}
