package se.sundsvall.invoicecache.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import se.sundsvall.invoicecache.api.model.InvoicePDF;
import se.sundsvall.invoicecache.api.model.InvoiceRequest;
import se.sundsvall.invoicecache.api.model.InvoicesResponse;
import se.sundsvall.invoicecache.service.InvoiceCacheService;
import se.sundsvall.invoicecache.service.InvoicePDFService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/")
@Tag(name = "Invoice Cache")
public class InvoiceCacheResource {

    private final InvoiceCacheService invoiceCacheService;
    private final InvoicePDFService invoicePDFService;

    public InvoiceCacheResource(final InvoiceCacheService invoiceCacheService, InvoicePDFService invoicePDFService) {
        this.invoiceCacheService = invoiceCacheService;
        this.invoicePDFService = invoicePDFService;
    }

    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = InvoicesResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Problem.class)))
    @GetMapping(value = "/getInvoices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoicesResponse> getInvoices(@Valid InvoiceRequest request) {
        RequestValidator.validateRequest(request);
        final InvoicesResponse response = invoiceCacheService.getInvoices(request);
        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = InvoicePDF.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Problem.class)))
    @GetMapping("/getInvoice/{filename}")
    public ResponseEntity<InvoicePDF> getInvoicePDF(@PathVariable @NotBlank String filename) {
        return ResponseEntity.ok(invoicePDFService.getInvoicePdf(filename));
    }
}
