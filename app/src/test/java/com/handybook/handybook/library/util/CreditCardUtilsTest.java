package com.handybook.handybook.library.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CreditCardUtilsTest {

    @Test
    public void creditCardFormatterShouldBeautify() {
        assertEquals(
                "4242 4242",
                CreditCardUtils.formatCreditCardNumber("4242 4242 ")
        );
        assertEquals(
                "4242 4242 4",
                CreditCardUtils.formatCreditCardNumber("4242 42424")
        );
        assertEquals(
                "4242 4242 4242 4242",
                CreditCardUtils.formatCreditCardNumber("4242424242424242")
        );
        assertEquals(
                "3442 424242 42424",
                CreditCardUtils.formatCreditCardNumber("344242424242424")
        );
    }

    @Test
    public void creditCardDateFormatterShouldBeautify() {
        assertEquals("0", CreditCardUtils.formatCreditCardExpDate("0"));
        assertEquals("11", CreditCardUtils.formatCreditCardExpDate("11"));
        assertEquals("10/1", CreditCardUtils.formatCreditCardExpDate("101"));
        assertEquals("09/18", CreditCardUtils.formatCreditCardExpDate("0918"));
    }
}
