package com.handybook.handybook.booking.proteam;

import com.google.gson.annotations.SerializedName;

public enum ProviderMatchPreference
{
    @SerializedName("never")
    NEVER,
    @SerializedName("preferred")
    PREFERRED,
    @SerializedName("indifferent")
    INDIFFERENT;
}
