package com.handybook.handybook.core.util;

import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.library.util.TextUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jwilliams on 2/16/15.
 */
public class TextUtilsTest {

    @Test
    public void priceFormatterShouldBeautify() {
        assertEquals("$4.99", TextUtils.formatPrice(4.99f, "$", ""));
        assertEquals(
                "Should use default of dollar sign when null",
                "$15.87",
                TextUtils.formatPrice(15.87f, null, null)
        );
    }

    @Test
    public void creditCardFormatterShouldBeautify() {
        assertEquals(
                "4242 4242",
                TextUtils.formatCreditCardNumber(CreditCard.Type.VISA, "4242 4242 ")
        );
        assertEquals(
                "4242 4242 4",
                TextUtils.formatCreditCardNumber(CreditCard.Type.DISCOVER, "4242 42424")
        );
        assertEquals(
                "4242 4242 4242 4242",
                TextUtils.formatCreditCardNumber(
                        CreditCard.Type.MASTERCARD,
                        "4242424242424242"
                )
        );
        assertEquals(
                "4242 424242 42424",
                TextUtils.formatCreditCardNumber(CreditCard.Type.AMEX, "424242424242424")
        );
    }

    @Test
    public void creditCardDateFormatterShouldBeautify() {
        assertEquals("0", TextUtils.formatCreditCardExpDate("0"));
        assertEquals("11", TextUtils.formatCreditCardExpDate("11"));
        assertEquals("10/1", TextUtils.formatCreditCardExpDate("101"));
        assertEquals("09/18", TextUtils.formatCreditCardExpDate("0918"));
    }

}
