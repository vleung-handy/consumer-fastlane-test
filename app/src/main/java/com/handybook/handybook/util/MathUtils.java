package com.handybook.handybook.util;

public class MathUtils
{
    //TODO: is there a function in the java standard lib that does the same thing?
    public static float roundToDecimalPlaces(float num, int decimalPlaces)
    {
        float multiplier = (float) Math.pow(10, decimalPlaces);
        int i = (int) ((num * multiplier) + 0.5f);
        return i/(multiplier);
    }
}
