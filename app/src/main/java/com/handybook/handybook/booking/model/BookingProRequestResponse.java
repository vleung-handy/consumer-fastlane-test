package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public final class BookingProRequestResponse {

    @SerializedName("success")
    private boolean success;
    @SerializedName("booking")
    private Booking booking;
    @SerializedName("analytics")
    private BookingProRequestResponseAnalytics analytics;
    @SerializedName("available")
    private boolean available;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public Booking getBooking() {
        return booking;
    }

    public BookingProRequestResponseAnalytics getAnalytics() {
        return analytics;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getMessage() {
        return message;
    }

    public static final class BookingProRequestResponseAnalytics {

        @SerializedName("hours_before_job")
        private float hoursBeforeJob;
    }

}

