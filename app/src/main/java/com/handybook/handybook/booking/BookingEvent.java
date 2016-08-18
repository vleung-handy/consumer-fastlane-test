package com.handybook.handybook.booking;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

import java.util.List;

public abstract class BookingEvent
{
    public abstract static class RequestBookingActionEvent extends HandyEvent.RequestEvent
    {
        public String bookingId;
    }


    public abstract static class ReceiveBookingSuccessEvent extends HandyEvent.ReceiveSuccessEvent
    {
        public Booking booking;
    }


    public static class RequestBookingDetails extends HandyEvent.RequestEvent
    {
        public String bookingId;

        public RequestBookingDetails(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveBookingDetailsSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveBookingDetailsSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    public static class ReceiveBookingDetailsError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveBookingDetailsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }


    public static class RequestBookings extends HandyEvent.RequestEvent
    {
        @NonNull
        @Booking.List.OnlyBookingValues
        final String mOnlyBookingsValue;

        public RequestBookings(
                @NonNull @Booking.List.OnlyBookingValues String onlyBookingsValue
        )
        {
            mOnlyBookingsValue = onlyBookingsValue;
        }

        @NonNull
        @Booking.List.OnlyBookingValues
        public String getOnlyBookingsValue()
        {
            return mOnlyBookingsValue;
        }
    }


    public static class ReceiveBookingsSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final UserBookingsWrapper mBookingWrapper;

        @NonNull
        @Booking.List.OnlyBookingValues
        private final String mOnlyBookingsValue;

        public ReceiveBookingsSuccess(
                final UserBookingsWrapper bookingWrapper,
                @NonNull @Booking.List.OnlyBookingValues String onlyBookingsValue
        )
        {
            mBookingWrapper = bookingWrapper;
            mOnlyBookingsValue = onlyBookingsValue;
        }

        @NonNull
        public String getOnlyBookingsValue()
        {
            return mOnlyBookingsValue;
        }

        public UserBookingsWrapper getBookingWrapper()
        {
            return mBookingWrapper;
        }
    }


    public static class ReceiveBookingsError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class PostLowRatingFeedback extends HandyEvent.RequestEvent
    {
        public RateImprovementFeedback mFeedback;

        public PostLowRatingFeedback(final RateImprovementFeedback feedback)
        {
            mFeedback = feedback;
        }
    }


    public static class PostLowRatingFeedbackSuccess extends HandyEvent.ReceiveSuccessEvent
    {
    }


    public static class PostLowRatingFeedbackError extends HandyEvent.ReceiveErrorEvent
    {
        public PostLowRatingFeedbackError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPrerateProInfo extends HandyEvent.RequestEvent
    {
        public String bookingId;

        public RequestPrerateProInfo(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePrerateProInfoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private PrerateProInfo mPrerateProInfo;

        public ReceivePrerateProInfoSuccess(PrerateProInfo prerateProInfo)
        {
            this.mPrerateProInfo = prerateProInfo;
        }

        public PrerateProInfo getPrerateProInfo()
        {
            return mPrerateProInfo;
        }
    }


    public static class ReceivePrerateProInfoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePrerateProInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestPreRescheduleInfo extends HandyEvent.RequestEvent
    {
        public String bookingId;

        public RequestPreRescheduleInfo(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePreRescheduleInfoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public String notice;

        public ReceivePreRescheduleInfoSuccess(String notice)
        {
            this.notice = notice;
        }

    }


    public static class ReceivePreRescheduleInfoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePreRescheduleInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPreCancelationInfo extends HandyEvent.RequestEvent
    {
        public String bookingId;

        public RequestPreCancelationInfo(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePreCancelationInfoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public Pair<String, List<String>> result;

        public ReceivePreCancelationInfoSuccess(Pair<String, List<String>> result)
        {
            this.result = result;
        }

    }


    public static class ReceivePreCancelationInfoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePreCancelationInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class SetBookingDetailSectionFragmentActionControlsEnabled extends HandyEvent
    {
        public boolean enabled;

        public SetBookingDetailSectionFragmentActionControlsEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }


    public static class RateBookingEvent extends HandyEvent.RequestEvent
    {
        private final int mBookingId;
        private final int mFinalRating;
        private final Integer mTipAmountCents;
        private ProviderMatchPreference mProviderMatchPreference;

        public RateBookingEvent(
                int bookingId,
                int finalRating,
                Integer tipAmountCents,
                ProviderMatchPreference providerMatchPreference
        )
        {
            mBookingId = bookingId;
            mFinalRating = finalRating;
            mTipAmountCents = tipAmountCents;
            mProviderMatchPreference = providerMatchPreference;
        }

        public int getBookingId()
        {
            return mBookingId;
        }

        public int getFinalRating()
        {
            return mFinalRating;
        }

        public Integer getTipAmountCents()
        {
            return mTipAmountCents;
        }

        public ProviderMatchPreference getProviderMatchPreference()
        {
            return mProviderMatchPreference;
        }
    }


    public static class ReceiveRateBookingSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveRateBookingSuccess()
        {
        }
    }


    public static class ReceiveRateBookingError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveRateBookingError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }


    public static class RequestSendCancelRecurringBookingEmail extends HandyEvent.RequestEvent
    {
        public final int bookingRecurringId;

        public RequestSendCancelRecurringBookingEmail(final int bookingRecurringId)
        {
            this.bookingRecurringId = bookingRecurringId;
        }
    }


    public static class ReceiveSendCancelRecurringBookingEmailSuccess extends HandyEvent.ReceiveSuccessEvent
    {
    }


    public static class ReceiveSendCancelRecurringBookingEmailError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveSendCancelRecurringBookingEmailError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestRecurringBookings extends HandyEvent.RequestEvent {}


    public static class ReceiveRecurringBookingsSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final List<RecurringBooking> recurringBookings;

        public ReceiveRecurringBookingsSuccess(final List<RecurringBooking> recurringBookings)
        {
            this.recurringBookings = recurringBookings;
        }
    }


    public static class ReceiveRecurringBookingsError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveRecurringBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestServices {}


    public static class RequestCachedServices {}


    public static class ReceiveServicesSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private List<Service> mServices;

        public ReceiveServicesSuccess(final List<Service> services)
        {
            mServices = services;
        }

        public List<Service> getServices()
        {
            return mServices;
        }
    }


    public static class ReceiveCachedServicesSuccess extends ReceiveServicesSuccess
    {
        public ReceiveCachedServicesSuccess(final List<Service> services)
        {
            super(services);
        }
    }


    public static class ReceiveServicesError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveServicesError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPreBookingPromo extends HandyEvent.RequestEvent
    {
        private String mPromoCode;

        public RequestPreBookingPromo(String promoCode)
        {
            mPromoCode = promoCode;
        }

        public String getPromoCode()
        {
            return mPromoCode;
        }
    }


    public static class ReceivePreBookingPromoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private PromoCode mPromoCode;

        public ReceivePreBookingPromoSuccess(final PromoCode promoCode)
        {
            mPromoCode = promoCode;
        }

        public PromoCode getPromoCode()
        {
            return mPromoCode;
        }
    }


    public static class ReceivePreBookingPromoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePreBookingPromoError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestTipPro extends HandyEvent.RequestEvent
    {
        public final Integer tipAmount;
        public final int bookingId;

        public RequestTipPro(final int bookingId, final Integer tipAmount)
        {
            this.bookingId = bookingId;
            this.tipAmount = tipAmount;
        }
    }


    public static class ReceiveTipProSuccess extends HandyEvent.ReceiveSuccessEvent
    {
    }


    public static class ReceiveTipProError extends HandyEvent.ReceiveErrorEvent
    {
    }


    public static class RequestFinalizeBooking extends HandyEvent.RequestEvent
    {
        private int mBookingId;
        private FinalizeBookingRequestPayload mPayload;

        private RequestFinalizeBooking()
        {
        }

        public RequestFinalizeBooking(final int bookingId, final FinalizeBookingRequestPayload payload)
        {
            mBookingId = bookingId;
            mPayload = payload;
        }

        public int getBookingId()
        {
            return mBookingId;
        }

        public FinalizeBookingRequestPayload getPayload()
        {
            return mPayload;
        }

    }


    public static class FinalizeBookingSuccess extends HandyEvent.ReceiveSuccessEvent {}


    public static class FinalizeBookingError extends HandyEvent.ReceiveErrorEvent {}

}
