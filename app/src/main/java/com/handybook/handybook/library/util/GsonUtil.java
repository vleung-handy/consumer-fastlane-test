package com.handybook.handybook.library.util;

import com.google.gson.JsonElement;

public class GsonUtil
{

    public static String safeGetAsString(JsonElement element)
    {
        if (element != null)
        {
            return element.getAsString();
        }

        return null;
    }

    /**
     * Note: Defaults null values to false
     * @param element
     * @return
     */
    public static boolean safeGetAsBoolean(JsonElement element)
    {
        if (element != null)
        {
            return element.getAsBoolean();
        }

        return false;
    }
}
