package com.handybook.handybook.library.util;

public class MathUtils {

    // TODO: is there a function in the java standard lib that does the same thing?
    // TODOANSWER :) ^ Yes there is, use java.math.BigDecimal.setScale()
    public static double roundToDecimalPlaces(double num, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(num * multiplier) / (multiplier);
    }
}
