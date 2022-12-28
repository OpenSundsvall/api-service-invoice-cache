package se.sundsvall.invoicecache.api.model;

import java.math.BigDecimal;

public enum InvoiceType {
    NORMAL("Faktura"),
    CREDIT("Kreditfaktura");

    InvoiceType(final String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }

    public static InvoiceType fromInvoiceAmount(BigDecimal amount) {
        if(amount.signum() == 1) {
            return InvoiceType.NORMAL;
        } else {
            return InvoiceType.CREDIT;
        }
    }
}
