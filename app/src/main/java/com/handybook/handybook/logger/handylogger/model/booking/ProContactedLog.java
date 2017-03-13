package com.handybook.handybook.logger.handylogger.model.booking;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import static com.handybook.handybook.logger.handylogger.model.booking.EventContext.ACTIVE_BOOKING;
import static com.handybook.handybook.logger.handylogger.model.booking.EventContext.BOOKING_DETAILS;
import static com.handybook.handybook.logger.handylogger.model.booking.EventContext.ISSUE_RESOLUTION;

public class ProContactedLog extends EventLog {

    public static final String PHONE = "phone";
    public static final String SMS = "sms";
    public static final String CHAT = "chat";


    @StringDef({PHONE, SMS, CHAT})
    @interface ContactType {}


    @StringDef({ACTIVE_BOOKING, ISSUE_RESOLUTION, BOOKING_DETAILS})
    @interface EventContext {}


    @SerializedName("booking_id")
    private String mBookingId;
    @SerializedName("pro_contact_type")
    private String mContactType;

    public ProContactedLog(
            @EventContext String eventContext,
            String bookingId,
            @ContactType String contactType
    ) {
        super(EventType.PRO_CONTACTED, eventContext);
        mBookingId = bookingId;
        mContactType = contactType;
    }
}
