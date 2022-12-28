package se.sundsvall.invoicecache.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.invoicecache.api.model.InvoiceRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class RequestValidatorTest {
    
    private final List<String> invoiceNumbers = List.of("1234", "2345");
    private final List<String> legalIds = List.of("5591621234", "5591621235");

    @Test
    void testCheckInvoiceDates_fromIsAfterTo() {
        InvoiceRequest request = new InvoiceRequest();
        request.setInvoiceDateFrom(LocalDate.now().plusDays(1L));
        request.setInvoiceDateTo(LocalDate.now());
        request.setInvoiceNumbers(invoiceNumbers);
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> RequestValidator.validateRequest(request))
                .withMessage("To-date is before From-date.");
    }
    
    @Test
    void testOrganizationNumberFieldIsSet() {
        InvoiceRequest request = new InvoiceRequest();
        request.setPartyIds(legalIds);
        RequestValidator.validateRequest(request);
    }
    
    @Test
    void testInvoiceNumberFieldIsSet() {
        InvoiceRequest request = new InvoiceRequest();
        request.setInvoiceNumbers(invoiceNumbers);
        RequestValidator.validateRequest(request);
    }
    
    @Test
    void testOcrNumberFieldIsSet() {
        InvoiceRequest request = new InvoiceRequest();
        request.setOcrNumber("testString");
        RequestValidator.validateRequest(request);
    }
    
    @Test
    void testAllFieldsAreSet(){
        InvoiceRequest request = new InvoiceRequest();
        request.setOcrNumber("testString");
        request.setInvoiceNumbers(invoiceNumbers);
        request.setPartyIds(legalIds);
        RequestValidator.validateRequest(request);
    }
    
    @Test
    void testNoFieldIsSet() {
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> RequestValidator.validateRequest(new InvoiceRequest()))
                .withMessage("One of legalIds, invoiceNumbers or ocrNumber needs to be set.");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testOnlyOrganizationNumberHasFaultyValues(final String testString) {
        InvoiceRequest request = new InvoiceRequest();
        request.setPartyIds(List.of(testString));
    
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> RequestValidator.validateRequest(request))
                .withMessage("One of legalIds, invoiceNumbers or ocrNumber needs to be set.");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testOnlyInvoiceNumberHasFaultyValues(final String testString) {
        InvoiceRequest request = new InvoiceRequest();
        request.setInvoiceNumbers(List.of(testString));
        
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> RequestValidator.validateRequest(request))
                .withMessage("One of legalIds, invoiceNumbers or ocrNumber needs to be set.");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testOnlyOcrNumberHasFaultyValues(final String testString) {
        InvoiceRequest request = new InvoiceRequest();
        request.setOcrNumber(testString);
        
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> RequestValidator.validateRequest(request))
                .withMessage("One of legalIds, invoiceNumbers or ocrNumber needs to be set.");
    }
}