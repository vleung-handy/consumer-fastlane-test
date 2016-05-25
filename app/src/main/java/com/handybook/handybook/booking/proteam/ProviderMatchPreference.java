package com.handybook.handybook.booking.proteam;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public enum ProviderMatchPreference
{
    @SerializedName(Constants.STRING_VALUE_NEVER)
    NEVER(Constants.STRING_VALUE_NEVER),
    @SerializedName(Constants.STRING_VALUE_INDIFFERENT)
    INDIFFERENT(Constants.STRING_VALUE_INDIFFERENT),
    @SerializedName(Constants.STRING_VALUE_PREFERRED)
    PREFERRED(Constants.STRING_VALUE_PREFERRED);

    private final String mValue;

    ProviderMatchPreference(String value)
    {
        mValue = value;
    }

    @Nullable
    @Override
    public String toString()
    {
        return mValue;
    }

    /**
     * @param preference ProviderMatchPrefference to be converted to it's String represenation
     * @return String representation of provided provider match preferrence or a
     * representation of INDIFFERENT type ('indifferent')
     */
    @NonNull
    public static String asString(@Nullable ProviderMatchPreference preference)
    {
        return preference == null ? INDIFFERENT.toString() : preference.toString();
    }


    private static class Constants
    {
        static final String STRING_VALUE_NEVER = "never";
        static final String STRING_VALUE_PREFERRED = "preferred";
        static final String STRING_VALUE_INDIFFERENT = "indifferent";
    }
}
