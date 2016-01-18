package com.handybook.handybook.analytics;

import android.support.annotation.NonNull;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;

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
        public static final String APP_TRACK_ADD_BOOKING_FAB_CLICKED = "add booking fab clicked";
        public static final String APP_TRACK_ADD_BOOKING_FAB_SERVICE_SELECTED = "add booking fab service selected";
        public static final String APP_TRACK_ADD_BOOKING_FAB_MENU_SHOWN = "add booking fab menu shown";
        public static final String APP_TRACK_ADD_BOOKING_FAB_MENU_DISMISSED = "add booking fab menu dismissed";
        public static final String APP_TRACK_SPLASH_PROMO_SHOW = "app splash promo show";
        public static final String APP_TRACK_SPLASH_PROMO_ACTION = "app splash promo action";
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


    /**
     * denotes the flow where the tip option is shown
     */
    public enum TipParentFlow //TODO: give this a better name?
    {
        RATING_FLOW("rating flow tip"),
        BOOKING_DETAILS_FLOW("booking details tip");

        private final String mStringValue;

        TipParentFlow(String stringValue)
        {
            mStringValue = stringValue;
        }

        @Override
        public String toString()
        {
            return mStringValue;
        }
    }

    /**
     * tracks when the splash promo is shown
     */
    @Track(EventKey.APP_TRACK_SPLASH_PROMO_SHOW)
    public static class TrackSplashPromoShow extends MixpanelEvent
    {
        @TrackField("uniq_code_id")
        public final String promoId;

        public TrackSplashPromoShow(final String promoId)
        {
            this.promoId = promoId;
        }
    }

    /**
     * tracks when the splash promo action button is pressed
     */
    @Track(EventKey.APP_TRACK_SPLASH_PROMO_ACTION)
    public static class TrackSplashPromoAction extends MixpanelEvent
    {
        @TrackField("uniq_code_id")
        public final String promoId;

        public TrackSplashPromoAction(final String promoId)
        {
            this.promoId = promoId;
        }
    }

    /**
     * tracks when the rating dialog is shown
     */
    @Track(EventKey.APP_TRACK_SHOW_RATING_PROMPT)
    public static class TrackShowRatingPrompt extends MixpanelEvent
    {
    }


    /**
     * tracks when the tip layout is shown
     */
    @Track(EventKey.APP_TRACK_SHOW_TIP_PROMPT)
    public static class TrackShowTipPrompt extends MixpanelEvent
    {
        @TrackField("flow")
        public final String tipParentFlow;

        public TrackShowTipPrompt(@NonNull final TipParentFlow tipParentFlow)
        {
            this.tipParentFlow = tipParentFlow.toString();
        }
    }


    /**
     * tracks when a tip is submitted
     */
    @Track(EventKey.APP_TRACK_SUBMIT_TIP)
    public static class TrackSubmitTip extends MixpanelEvent
    {
        @TrackField("amount")
        public final int tipAmountCents;
        @TrackField("flow")
        public final String tipParentFlow;

        public TrackSubmitTip(final int tipAmountCents, @NonNull final TipParentFlow tipParentFlow)
        {
            this.tipAmountCents = tipAmountCents;
            this.tipParentFlow = tipParentFlow.toString();

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

    @Track(EventKey.APP_TRACK_ADD_BOOKING_FAB_CLICKED)
    public static class TrackAddBookingFabClicked {}

    @Track(EventKey.APP_TRACK_ADD_BOOKING_FAB_SERVICE_SELECTED)
    public static class TrackAddBookingFabServiceSelected
    {
        @TrackField("service id")
        private final int mId;
        @TrackField("service uniq")
        private final String mUniq;

        public TrackAddBookingFabServiceSelected(final int id, final String uniq)
        {
            mId = id;
            mUniq = uniq;
        }
    }

    @Track(EventKey.APP_TRACK_ADD_BOOKING_FAB_MENU_SHOWN)
    public static class TrackAddBookingFabMenuShown {}


    @Track(EventKey.APP_TRACK_ADD_BOOKING_FAB_MENU_DISMISSED)
    public static class TrackAddBookingFabMenuDismissed {}
}
