package com.handybook.handybook.booking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.util.List;

public abstract class BookingEvent {

    public abstract static class RequestBookingActionEvent extends HandyEvent.RequestEvent {

        public String bookingId;
    }


    public abstract static class ReceiveBookingSuccessEvent extends HandyEvent.ReceiveSuccessEvent {

        public Booking booking;
    }


    public static class RequestBookingDetails extends HandyEvent.RequestEvent {

        public String bookingId;

        public RequestBookingDetails(String bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveBookingDetailsSuccess extends ReceiveBookingSuccessEvent {

        public ReceiveBookingDetailsSuccess(Booking booking) {
            this.booking = booking;
        }
    }


    public static class ReceiveBookingDetailsError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveBookingDetailsError(DataManager.DataManagerError error) {
            this.error = error;
        }

    }

    public static class PostLowRatingFeedback extends HandyEvent.RequestEvent {

        public RateImprovementFeedback mFeedback;

        public PostLowRatingFeedback(final RateImprovementFeedback feedback) {
            mFeedback = feedback;
        }
    }


    public static class PostLowRatingFeedbackSuccess extends HandyEvent.ReceiveSuccessEvent {
    }


    public static class PostLowRatingFeedbackError extends HandyEvent.ReceiveErrorEvent {

        public PostLowRatingFeedbackError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestPrerateProInfo extends HandyEvent.RequestEvent {

        public String bookingId;

        public RequestPrerateProInfo(String bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePrerateProInfoSuccess extends HandyEvent.ReceiveSuccessEvent {

        private PrerateProInfo mPrerateProInfo;

        public ReceivePrerateProInfoSuccess(PrerateProInfo prerateProInfo) {
            this.mPrerateProInfo = prerateProInfo;
        }

        public PrerateProInfo getPrerateProInfo() {
            return mPrerateProInfo;
        }
    }


    public static class ReceivePrerateProInfoError extends HandyEvent.ReceiveErrorEvent {

        public ReceivePrerateProInfoError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestPreRescheduleInfo extends HandyEvent.RequestEvent {

        public String bookingId;

        public RequestPreRescheduleInfo(String bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePreRescheduleInfoSuccess extends HandyEvent.ReceiveSuccessEvent {

        public String notice;

        public ReceivePreRescheduleInfoSuccess(String notice) {
            this.notice = notice;
        }

    }


    public static class ReceivePreRescheduleInfoError extends HandyEvent.ReceiveErrorEvent {

        public ReceivePreRescheduleInfoError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestBookingCancellationData extends HandyEvent.RequestEvent {

        public String bookingId;

        public RequestBookingCancellationData(String bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveBookingCancellationDataSuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public BookingCancellationData result;

        public ReceiveBookingCancellationDataSuccess(final BookingCancellationData result) {
            this.result = result;
        }

    }


    public static class ReceiveBookingCancellationDataError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveBookingCancellationDataError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class SetBookingDetailSectionFragmentActionControlsEnabled extends HandyEvent {

        public boolean enabled;

        public SetBookingDetailSectionFragmentActionControlsEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }


    public static class RateBookingEvent extends HandyEvent.RequestEvent {

        private final int mBookingId;
        private final int mFinalRating;
        private final Integer mTipAmountCents;
        private ProviderMatchPreference mProviderMatchPreference;

        public RateBookingEvent(
                int bookingId,
                int finalRating,
                Integer tipAmountCents,
                ProviderMatchPreference providerMatchPreference
        ) {
            mBookingId = bookingId;
            mFinalRating = finalRating;
            mTipAmountCents = tipAmountCents;
            mProviderMatchPreference = providerMatchPreference;
        }

        public int getBookingId() {
            return mBookingId;
        }

        public int getFinalRating() {
            return mFinalRating;
        }

        public Integer getTipAmountCents() {
            return mTipAmountCents;
        }

        public ProviderMatchPreference getProviderMatchPreference() {
            return mProviderMatchPreference;
        }
    }


    public static class ReceiveRateBookingSuccess extends ReceiveBookingSuccessEvent {

        public ReceiveRateBookingSuccess() {
        }
    }


    public static class ReceiveRateBookingError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveRateBookingError(DataManager.DataManagerError error) {
            this.error = error;
        }

    }


    public static class RequestSendCancelRecurringBookingEmail extends HandyEvent.RequestEvent {

        public final int bookingRecurringId;

        public RequestSendCancelRecurringBookingEmail(final int bookingRecurringId) {
            this.bookingRecurringId = bookingRecurringId;
        }
    }


    public static class ReceiveSendCancelRecurringBookingEmailSuccess
            extends HandyEvent.ReceiveSuccessEvent {
    }


    public static class ReceiveSendCancelRecurringBookingEmailError
            extends HandyEvent.ReceiveErrorEvent {

        public ReceiveSendCancelRecurringBookingEmailError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestRecurringBookings extends HandyEvent.RequestEvent {}


    public static class ReceiveRecurringBookingsSuccess extends HandyEvent.ReceiveSuccessEvent {

        public final List<RecurringBooking> recurringBookings;

        public ReceiveRecurringBookingsSuccess(final List<RecurringBooking> recurringBookings) {
            this.recurringBookings = recurringBookings;
        }
    }


    public static class ReceiveRecurringBookingsError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveRecurringBookingsError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestServices {

        private String mZip;

        public RequestServices() {}

        public RequestServices(@NonNull final String zip) {
            mZip = zip;
        }

        @Nullable
        public String getZip() {
            return mZip;
        }
    }


    public static class RequestCachedServices {}


    public static class ReceiveServicesSuccess extends HandyEvent.ReceiveSuccessEvent {

        private List<Service> mServices;
        private String mZip;

        public ReceiveServicesSuccess(final List<Service> services, @Nullable final String zip) {
            this(services);
            mZip = zip;
        }

        public ReceiveServicesSuccess(final List<Service> services) {
            mServices = services;
        }

        public List<Service> getServices() {
            return mServices;
        }

        @Nullable
        public String getZip() {
            return mZip;
        }
    }


    public static class ReceiveCachedServicesSuccess extends ReceiveServicesSuccess {

        public ReceiveCachedServicesSuccess(final List<Service> services) {
            super(services);
        }
    }


    public static class ReceiveServicesError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveServicesError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestPreBookingPromo extends HandyEvent.RequestEvent {

        private String mPromoCode;

        public RequestPreBookingPromo(String promoCode) {
            mPromoCode = promoCode;
        }

        public String getPromoCode() {
            return mPromoCode;
        }
    }


    public static class ReceivePreBookingPromoSuccess extends HandyEvent.ReceiveSuccessEvent {

        private PromoCode mPromoCode;

        public ReceivePreBookingPromoSuccess(final PromoCode promoCode) {
            mPromoCode = promoCode;
        }

        public PromoCode getPromoCode() {
            return mPromoCode;
        }
    }


    public static class ReceivePreBookingPromoError extends HandyEvent.ReceiveErrorEvent {

        public ReceivePreBookingPromoError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestTipPro extends HandyEvent.RequestEvent {

        public final Integer tipAmount;
        public final int bookingId;

        public RequestTipPro(final int bookingId, final Integer tipAmount) {
            this.bookingId = bookingId;
            this.tipAmount = tipAmount;
        }
    }


    public static class ReceiveTipProSuccess extends HandyEvent.ReceiveSuccessEvent {
    }


    public static class ReceiveTipProError extends HandyEvent.ReceiveErrorEvent {
    }


    public static class RequestFinalizeBooking extends HandyEvent.RequestEvent {

        private int mBookingId;
        private FinalizeBookingRequestPayload mPayload;

        private RequestFinalizeBooking() {
        }

        public RequestFinalizeBooking(
                final int bookingId,
                final FinalizeBookingRequestPayload payload
        ) {
            mBookingId = bookingId;
            mPayload = payload;
        }

        public int getBookingId() {
            return mBookingId;
        }

        public FinalizeBookingRequestPayload getPayload() {
            return mPayload;
        }

    }


    public static class FinalizeBookingSuccess extends HandyEvent.ReceiveSuccessEvent {}


    public static class FinalizeBookingError extends HandyEvent.ReceiveErrorEvent {}


    public static class RescheduleBookingWithProAvailabilitySuccess
            extends HandyEvent.ReceiveSuccessEvent {

        private Booking mBooking;
        private ProAvailabilityResponse mProAvailability;
        private String mNotice;

        public RescheduleBookingWithProAvailabilitySuccess(
                final Booking booking,
                final ProAvailabilityResponse proAvailability,
                final String notice
        ) {
            mBooking = booking;
            mProAvailability = proAvailability;
            mNotice = notice;
        }

        public ProAvailabilityResponse getProAvailability() {
            return mProAvailability;
        }

        public String getNotice() {
            return mNotice;
        }

        public Booking getBooking() {
            return mBooking;
        }
    }


    public static class RescheduleBookingWithProAvailabilityError
            extends HandyEvent.ReceiveErrorEvent {

        public RescheduleBookingWithProAvailabilityError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }
}
