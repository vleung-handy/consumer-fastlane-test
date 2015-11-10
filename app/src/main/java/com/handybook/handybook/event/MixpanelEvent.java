package com.handybook.handybook.event;

import android.support.annotation.NonNull;

import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.annotation.TrackField;

public abstract class MixpanelEvent
{
    private static class EventKey
    {
        public static final String APP_TRACK_INSTALL = "App Track Install";
        public static final String APP_TRACK_LOCATION = "App Track Location";
        public static final String APP_TRACK_DETAILS = "App Track Details";
        public static final String APP_TRACK_TIME = "App Track Time";
        public static final String APP_TRACK_CONTACT = "App Track Contact";
        public static final String APP_TRACK_LOG_IN = "App Track Log In";
        public static final String APP_TRACK_REQUEST_PRO = "App Track Request Pro";
        public static final String APP_TRACK_COMMENTS = "App Track Comments";
        public static final String APP_TRACK_FREQUENCY = "App Track Frequency";
        public static final String APP_TRACK_EXTRAS = "App Track Extras";
        public static final String APP_TRACK_ADDRESS = "App Track Address";
        public static final String APP_TRACK_PAYMENT = "App Track Payment";
        public static final String APP_TRACK_CONFIRMATION = "App Track Confirmation";
        public static final String APP_TRACK_BOOKING_MADE = "booking made";
        public static final String APP_TRACK_PAYMENT_TOKEN = "App Track Payment Token";
    }

    public enum PaymentMethod
    {
        ANDROID_PAY("Android Pay");
        //more methods to come

        private final String mStringValue;
        PaymentMethod(String stringValue)
        {
            mStringValue = stringValue;
        }

        @Override
        public String toString()
        {
            return mStringValue;
        }
    }

    @Track(EventKey.APP_TRACK_PAYMENT) //TODO: this already exists in Mixpanel.java, need to move it out of there
    public static class TrackPaymentMethodShownEvent extends MixpanelEvent
    {
        @TrackField("Payment method shown")
        public final String paymentMethodShown;

        public TrackPaymentMethodShownEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethodShown = paymentMethod.toString();
        }
    }

    @Track(EventKey.APP_TRACK_BOOKING_MADE) //TODO: this already exists in Mixpanel.java, need to move it out of there
    public static class TrackBookingMadeWithPaymentMethodEvent extends MixpanelEvent
    {
        @TrackField("Payment method")
        public final String paymentMethod;

        public TrackBookingMadeWithPaymentMethodEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethod = paymentMethod.toString();
        }
    }

    @Track(EventKey.APP_TRACK_PAYMENT_TOKEN) //TODO
    public static class TrackReceivedPaymentTokenEvent extends MixpanelEvent
    {
        @TrackField("Payment method")
        public final String paymentMethod;

        public TrackReceivedPaymentTokenEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethod = paymentMethod.toString();
        }
    }
}
