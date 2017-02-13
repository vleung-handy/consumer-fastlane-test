package com.handybook.handybook.proteam.model;

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
    PREFERRED(Constants.STRING_VALUE_PREFERRED),
    @SerializedName(Constants.STRING_VALUE_FAVORITE)
    FAVORITE(Constants.STRING_VALUE_FAVORITE);

    private final String mValue;

    ProviderMatchPreference(String value)
    {
        mValue = value;
    }

    @NonNull
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

    /**
     * String constants that represent/can be converted to/from the ProviderMatchPreference
     */
    public static class Constants
    {
        static final String STRING_VALUE_NEVER = "never";
        static final String STRING_VALUE_PREFERRED = "preferred";
        static final String STRING_VALUE_INDIFFERENT = "indifferent";
        static final String STRING_VALUE_FAVORITE = "favorite";
    }
}
