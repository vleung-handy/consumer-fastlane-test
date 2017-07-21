package com.handybook.handybook.library.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
