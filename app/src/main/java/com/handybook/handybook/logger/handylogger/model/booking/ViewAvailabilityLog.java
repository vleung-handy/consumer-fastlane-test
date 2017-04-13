package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import static com.handybook.handybook.logger.handylogger.model.booking.EventType.VIEW_AVAILABILITY_SELECTED;

public class ViewAvailabilityLog extends EventLog {

    @SerializedName("booking_id")
    private String mBookingId;
    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("message")
    private String mMessage;

    public ViewAvailabilityLog(
            final String eventContext,
            final String bookingId,
            final String userId,
            final String providerId,
            final String message
    ) {
        super(VIEW_AVAILABILITY_SELECTED, eventContext);
        mBookingId = bookingId;
        mUserId = userId;
        mProviderId = providerId;
        mMessage = message;
    }
}
