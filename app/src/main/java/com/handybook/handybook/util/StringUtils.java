package com.handybook.handybook.util;

public class StringUtils
{
    public static String capitalizeFirstCharacter(String s)
    {
        if (ValidationUtils.isNullOrEmpty(s)) { return s; }
        String returnValue = Character.toString(Character.toUpperCase(s.charAt(0)));
        if (s.length() > 1)
        {
            returnValue += s.substring(1);
        }
        return returnValue;
    }

    public static String replaceWithEmptyIfNull(String input)
    {
        return input == null ? "" : input;
    }
}
