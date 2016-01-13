package com.handybook.handybook.booking;

import android.support.v4.util.Pair;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.booking.bookingedit.model.BookingEditAddressRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;

import java.util.List;

public class BookingEvent
{
    //TODO: further subdivide
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


    //Update the note to pro for a booking
    public static class RequestUpdateBookingNoteToPro extends HandyEvent.RequestEvent
    {
        public int bookingId;
        public BookingUpdateNoteToProTransaction descriptionTransaction;

        public RequestUpdateBookingNoteToPro(int bookingId, BookingUpdateNoteToProTransaction descriptionTransaction)
        {
            this.bookingId = bookingId;
            this.descriptionTransaction = descriptionTransaction;
        }
    }


    //
    public static class ReceiveUpdateBookingNoteToProSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public ReceiveUpdateBookingNoteToProSuccess()
        {

        }
    }


    //
    public static class ReceiveUpdateBookingNoteToProError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveUpdateBookingNoteToProError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Update the entry information for a booking
    public static class RequestUpdateBookingEntryInformation extends HandyEvent.RequestEvent
    {
        public int bookingId;
        public BookingUpdateEntryInformationTransaction entryInformationTransaction;

        public RequestUpdateBookingEntryInformation(int bookingId, BookingUpdateEntryInformationTransaction entryInformationTransaction)
        {
            this.bookingId = bookingId;
            this.entryInformationTransaction = entryInformationTransaction;
        }
    }


    //
    public static class ReceiveUpdateBookingEntryInformationSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public ReceiveUpdateBookingEntryInformationSuccess()
        {

        }
    }


    //
    public static class ReceiveUpdateBookingEntryInformationError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveUpdateBookingEntryInformationError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Update the frequency of a booking
    public static class RequestEditBookingFrequency extends HandyEvent.RequestEvent
    {
        public final int bookingId;
        public final BookingEditFrequencyRequest bookingEditFrequencyRequest;

        public RequestEditBookingFrequency(int bookingId, BookingEditFrequencyRequest bookingEditFrequencyRequest)
        {
            this.bookingId = bookingId;
            this.bookingEditFrequencyRequest = bookingEditFrequencyRequest;
        }
    }


    public static class ReceiveEditBookingFrequencySuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public ReceiveEditBookingFrequencySuccess()
        {
        }
    }


    public static class ReceiveEditBookingFrequencyError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditBookingFrequencyError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Get the booking prices for each booking frequency
    public static class RequestGetEditFrequencyViewModel extends HandyEvent.RequestEvent
    {
        public final int bookingId;

        public RequestGetEditFrequencyViewModel(int bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveGetEditFrequencyViewModelSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final BookingEditFrequencyViewModel bookingEditFrequencyViewModel;

        public ReceiveGetEditFrequencyViewModelSuccess(BookingEditFrequencyViewModel bookingEditFrequencyViewModel)
        {
            this.bookingEditFrequencyViewModel = bookingEditFrequencyViewModel;
        }
    }


    public static class ReceiveGetEditFrequencyViewModelError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveGetEditFrequencyViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Get the service extras options
    public static class RequestEditBookingExtrasViewModel extends HandyEvent.RequestEvent
    {
        public final int bookingId;

        public RequestEditBookingExtrasViewModel(int bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveEditBookingExtrasViewModelSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final BookingEditExtrasViewModel mBookingEditExtrasViewModel;

        public ReceiveEditBookingExtrasViewModelSuccess(
                BookingEditExtrasViewModel bookingEditExtrasViewModel)
        {
            this.mBookingEditExtrasViewModel = bookingEditExtrasViewModel;
        }
    }


    public static class ReceiveEditBookingExtrasViewModelError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditBookingExtrasViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Update the service extras options
    public static class RequestEditBookingExtras extends HandyEvent.RequestEvent
    {
        public final int bookingId;
        public final BookingEditExtrasRequest bookingEditExtrasRequest;

        public RequestEditBookingExtras(
                int bookingId,
                BookingEditExtrasRequest bookingEditExtrasRequest
        )
        {
            this.bookingId = bookingId;
            this.bookingEditExtrasRequest = bookingEditExtrasRequest;
        }
    }


    public static class ReceiveEditExtrasSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final SuccessWrapper successWrapper;

        public ReceiveEditExtrasSuccess(SuccessWrapper successWrapper)
        {
            this.successWrapper = successWrapper;
        }
    }


    public static class ReceiveEditExtrasError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditExtrasError(DataManager.DataManagerError error)
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

        public RateBookingEvent(int bookingId, int finalRating, Integer tipAmountCents)
        {
            mBookingId = bookingId;
            mFinalRating = finalRating;
            mTipAmountCents = tipAmountCents;
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


    public static class RequestEditHoursInfoViewModel extends HandyEvent.RequestEvent
    {
        public final int bookingId;
        public RequestEditHoursInfoViewModel(final int bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveEditHoursInfoViewModelSuccess extends ReceiveBookingSuccessEvent
    {
        public final BookingEditHoursViewModel editHoursInfoViewModel;
        public ReceiveEditHoursInfoViewModelSuccess(
                final BookingEditHoursViewModel editHoursInfoViewModel)
        {
            this.editHoursInfoViewModel = editHoursInfoViewModel;
        }
    }


    public static class ReceiveEditHoursInfoViewModelError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditHoursInfoViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }


    public static class RequestEditHours extends HandyEvent.RequestEvent
    {
        public final int bookingId;
        public final BookingEditHoursRequest bookingEditHoursRequest;
        public RequestEditHours(final int bookingId,
                                final BookingEditHoursRequest bookingEditHoursRequest) {
            this.bookingId = bookingId;
            this.bookingEditHoursRequest = bookingEditHoursRequest;
        }
    }


    public static class ReceiveEditHoursSuccess extends ReceiveBookingSuccessEvent
    {
        public final SuccessWrapper successWrapper;
        public ReceiveEditHoursSuccess(final SuccessWrapper successWrapper)
        {
            this.successWrapper = successWrapper;
        }
    }


    public static class ReceiveEditHoursError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditHoursError(DataManager.DataManagerError error)
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


    public static class RequestRecurringBookingsForUser extends HandyEvent.RequestEvent
    {
        public final User user;

        public RequestRecurringBookingsForUser(final User user)
        {
            this.user = user;
        }
    }


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


    public static class RequestEditBookingAddress extends HandyEvent.RequestEvent
    {
        public final int bookingId;
        public final BookingEditAddressRequest bookingEditAddressRequest;
        public RequestEditBookingAddress(final int bookingId,
                                         final BookingEditAddressRequest bookingEditAddressRequest)
        {
            this.bookingId = bookingId;
            this.bookingEditAddressRequest = bookingEditAddressRequest;
        }
    }


    public static class ReceiveEditBookingAddressSuccess extends HandyEvent.ReceiveSuccessEvent
    {
    }


    public static class ReceiveEditBookingAddressError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveEditBookingAddressError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestServices {}


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


    @Track("add booking fab clicked")
    public static class AddBookingButtonClicked {}


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
}
