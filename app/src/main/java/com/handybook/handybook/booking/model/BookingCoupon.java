package com.handybook.handybook.booking.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public final class BookingCoupon {

    @SerializedName("coupon_id")
    private int id;
    @SerializedName("price_table")
    private ArrayList<BookingPriceInfo> priceTable;
    @SerializedName("commitment_prices")
    private JsonObject mCommitmentPrices;

    final int getId() {
        return id;
    }

    public final ArrayList<BookingPriceInfo> getPriceTable() {
        return priceTable;
    }

    public final JsonObject getCommitmentPrices() {
        return mCommitmentPrices;
    }

    public static BookingCoupon fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                                .fromJson(json, BookingCoupon.class);
    }
}
