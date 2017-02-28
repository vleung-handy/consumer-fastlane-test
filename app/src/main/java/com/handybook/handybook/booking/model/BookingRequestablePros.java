package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class BookingRequestablePros {

    @SerializedName("requestable_jobs")
    private List<RequestableProvider> requestableProviders;

    public List<RequestableProvider> getRequestableProviders() {
        return requestableProviders;
    }

    public static final class RequestableProvider {

        @SerializedName("name")
        private String fullName;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("id")
        private String providerId;
    }
}

