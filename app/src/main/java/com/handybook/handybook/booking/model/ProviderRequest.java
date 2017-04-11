package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderRequest implements Serializable {

    @SerializedName("provider")
    private Provider mProvider;
    @SerializedName("response")
    private Response mResponse;

    public ProviderRequest(
            final Provider provider,
            final Response response
    ) {
        mProvider = provider;
        mResponse = response;
    }

    public Provider getProvider() {
        return mProvider;
    }

    public Response getResponse() {
        return mResponse;
    }

    public static class Response implements Serializable {

        @SerializedName("text")
        private String mText;

        public Response(final String text) {
            mText = text;
        }

        public String getText() {
            return mText;
        }
    }
}
