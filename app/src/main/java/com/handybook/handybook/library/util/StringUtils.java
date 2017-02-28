package com.handybook.handybook.library.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.handybook.handybook.R;

public class StringUtils {

    /**
     * @param s
     * @return s with all lowercase chars, or null if s is null
     */
    @Nullable
    public static String toLowerCase(@Nullable String s) {
        return s == null ? null : s.toLowerCase();
    }

    public static String capitalizeFirstCharacter(String s) {
        if (ValidationUtils.isNullOrEmpty(s)) { return s; }
        String returnValue = Character.toString(Character.toUpperCase(s.charAt(0)));
        if (s.length() > 1) {
            returnValue += s.substring(1);
        }
        return returnValue;
    }

    public static String replaceWithEmptyIfNull(String input) {
        return input == null ? "" : input;
    }

    public static String getFrequencyText(Context context, int frequency) {
        switch (frequency) {
            case 1:
                return context.getString(R.string.every_week);
            case 2:
                return context.getString(R.string.every_two_weeks);
            case 4:
                return context.getString(R.string.every_four_weeks);
            default:
                return "";
        }
    }
}
