package com.handybook.handybook.event;

import android.support.annotation.NonNull;

import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.annotation.TrackField;

public abstract class MixpanelEvent
{
    //TODO: move event trackers from Mixpanel.java here
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
        //TODO: ^this event key already exists in Mixpanel.java, need to move it out
        public static final String APP_TRACK_CONFIRMATION = "App Track Confirmation";
        public static final String APP_TRACK_BOOKING_MADE = "booking made";
        //TODO: ^this event key already exists in Mixpanel.java, need to move it out
        public static final String APP_TRACK_PAYMENT_METHOD_PROVIDED = "App Track Payment Method Provided";
        public static final String APP_TRACK_SHOW_RATING_PROMPT = "app rate prompt";
        public static final String APP_TRACK_SHOW_TIP_PROMPT = "present tips";
        public static final String APP_TRACK_SUBMIT_TIP = "submit tips";
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


    public enum TipFlowType
    {
        RATING_FLOW_TIP("rating flow tip"),
        BOOKING_DETAILS_FLOW_TIP("booking details tip");

        private final String mStringValue;

        TipFlowType(String stringValue)
        {
            mStringValue = stringValue;
        }

        @Override
        public String toString()
        {
            return mStringValue;
        }
    }


    @Track(EventKey.APP_TRACK_SHOW_RATING_PROMPT)
    public static class TrackShowRatingPrompt extends MixpanelEvent
    {
    }


    @Track(EventKey.APP_TRACK_SHOW_TIP_PROMPT)
    public static class TrackShowTipPrompt extends MixpanelEvent
    {
        @TrackField("flow")
        public final String flow;

        public TrackShowTipPrompt(@NonNull final TipFlowType tipFlowType)
        {
            this.flow = tipFlowType.toString();
        }
    }


    @Track(EventKey.APP_TRACK_SUBMIT_TIP)
    public static class TrackSubmitTip extends MixpanelEvent
    {
        @TrackField("amount")
        public final int tipAmountCents;
        @TrackField("flow")
        public final String flow;

        public TrackSubmitTip(final int tipAmountCents, @NonNull final TrackShowTipPrompt.TipFlowType tipFlowType)
        {
            this.tipAmountCents = tipAmountCents;
            this.flow = tipFlowType.toString();

        }
    }


    @Track(EventKey.APP_TRACK_PAYMENT)
    public static class TrackPaymentMethodShownEvent extends MixpanelEvent
    {
        @TrackField("Payment method shown")
        public final String paymentMethodShown;

        public TrackPaymentMethodShownEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethodShown = paymentMethod.toString();
        }
    }


    @Track(EventKey.APP_TRACK_BOOKING_MADE)
    public static class TrackBookingCompletedWithPaymentMethodEvent extends MixpanelEvent
    {
        @TrackField("Payment method")
        public final String paymentMethod;

        public TrackBookingCompletedWithPaymentMethodEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethod = paymentMethod.toString();
        }
    }


    /**
     * Given payment method (such as Android Pay) was successfully set up and provided in the
     * booking
     */
    @Track(EventKey.APP_TRACK_PAYMENT_METHOD_PROVIDED)
    public static class TrackPaymentMethodProvidedEvent extends MixpanelEvent
    {
        @TrackField("Payment method")
        public final String paymentMethod;

        public TrackPaymentMethodProvidedEvent(@NonNull PaymentMethod paymentMethod)
        {
            this.paymentMethod = paymentMethod.toString();
        }
    }

}
