package se.sundsvall.invoicecache.api.model;


import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Model for the InvoicePDF")
public class InvoicePDF {


    @Schema(description = "The file content as a BASE64-encoded string", example = "aGVsbG8gd29ybGQK", required = true)
    private String content;


    @Schema(description = "The filename", example = "test.pdf", required = true)
    private String name;

    @Schema(description = "The content type", example = "text/plain")
    private String contentType;
}
