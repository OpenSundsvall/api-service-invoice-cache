package se.sundsvall.invoicecache.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvoiceTypeTest {

    @Test
    void testValuesHaveNotChanged() {
        assertEquals("Faktura", InvoiceType.NORMAL.getType());
        assertEquals("Kreditfaktura", InvoiceType.CREDIT.getType());
        assertEquals(2, InvoiceType.values().length);
    }
}
