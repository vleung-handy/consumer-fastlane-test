package com.handybook.handybook.booking.bookingedit;

import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;

public abstract class BookingEditEvent {

    //Update the note to pro for a booking
    public static class RequestUpdateBookingNoteToPro extends HandyEvent.RequestEvent {

        public int bookingId;
        public BookingUpdateNoteToProTransaction descriptionTransaction;

        public RequestUpdateBookingNoteToPro(
                int bookingId,
                BookingUpdateNoteToProTransaction descriptionTransaction
        ) {
            this.bookingId = bookingId;
            this.descriptionTransaction = descriptionTransaction;
        }
    }


    //
    public static class ReceiveUpdateBookingNoteToProSuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public ReceiveUpdateBookingNoteToProSuccess() {

        }
    }


    //
    public static class ReceiveUpdateBookingNoteToProError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveUpdateBookingNoteToProError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class ReceiveEditBookingFrequencySuccess extends HandyEvent.ReceiveSuccessEvent {

        public ReceiveEditBookingFrequencySuccess() {
        }
    }


    public static class ReceiveEditBookingFrequencyError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditBookingFrequencyError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class ReceiveUpdateRecurringFrequencySuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public ReceiveUpdateRecurringFrequencySuccess() {
        }
    }


    public static class ReceiveUpdateRecurringFrequencyError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveUpdateRecurringFrequencyError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class ReceiveGetEditFrequencyViewModelSuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public final BookingEditFrequencyViewModel bookingEditFrequencyViewModel;

        public ReceiveGetEditFrequencyViewModelSuccess(BookingEditFrequencyViewModel bookingEditFrequencyViewModel) {
            this.bookingEditFrequencyViewModel = bookingEditFrequencyViewModel;
        }
    }


    public static class ReceiveGetEditFrequencyViewModelError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveGetEditFrequencyViewModelError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Get the booking prices for each booking frequency
    public static class RequestRecurringFrequencyViewModel extends HandyEvent.RequestEvent {

        public final String recurringId;

        public RequestRecurringFrequencyViewModel(String recurringId) {
            this.recurringId = recurringId;
        }
    }


    public static class ReceiveRecurringFrequencyViewModelSuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public final BookingEditFrequencyViewModel bookingEditFrequencyViewModel;

        public ReceiveRecurringFrequencyViewModelSuccess(BookingEditFrequencyViewModel bookingEditFrequencyViewModel) {
            this.bookingEditFrequencyViewModel = bookingEditFrequencyViewModel;
        }
    }


    public static class ReceiveRecurringFrequencyViewModelError
            extends HandyEvent.ReceiveErrorEvent {

        public ReceiveRecurringFrequencyViewModelError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Get the service extras options
    public static class RequestEditBookingExtrasViewModel extends HandyEvent.RequestEvent {

        public final int bookingId;

        public RequestEditBookingExtrasViewModel(int bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveEditBookingExtrasViewModelSuccess
            extends HandyEvent.ReceiveSuccessEvent {

        public final BookingEditExtrasViewModel mBookingEditExtrasViewModel;

        public ReceiveEditBookingExtrasViewModelSuccess(
                BookingEditExtrasViewModel bookingEditExtrasViewModel
        ) {
            this.mBookingEditExtrasViewModel = bookingEditExtrasViewModel;
        }
    }


    public static class ReceiveEditBookingExtrasViewModelError
            extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditBookingExtrasViewModelError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Update the service extras options
    public static class RequestEditBookingExtras extends HandyEvent.RequestEvent {

        public final int bookingId;
        public final BookingEditExtrasRequest bookingEditExtrasRequest;

        public RequestEditBookingExtras(
                int bookingId,
                BookingEditExtrasRequest bookingEditExtrasRequest
        ) {
            this.bookingId = bookingId;
            this.bookingEditExtrasRequest = bookingEditExtrasRequest;
        }
    }


    public static class ReceiveEditExtrasSuccess extends HandyEvent.ReceiveSuccessEvent {

        public final SuccessWrapper successWrapper;

        public ReceiveEditExtrasSuccess(SuccessWrapper successWrapper) {
            this.successWrapper = successWrapper;
        }
    }


    public static class ReceiveEditExtrasError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditExtrasError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestEditHoursInfoViewModel extends HandyEvent.RequestEvent {

        public final int bookingId;

        public RequestEditHoursInfoViewModel(final int bookingId) {
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveEditHoursInfoViewModelSuccess
            extends BookingEvent.ReceiveBookingSuccessEvent {

        public final BookingEditHoursViewModel editHoursInfoViewModel;

        public ReceiveEditHoursInfoViewModelSuccess(
                final BookingEditHoursViewModel editHoursInfoViewModel
        ) {
            this.editHoursInfoViewModel = editHoursInfoViewModel;
        }
    }


    public static class ReceiveEditHoursInfoViewModelError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditHoursInfoViewModelError(DataManager.DataManagerError error) {
            this.error = error;
        }

    }


    public static class RequestEditHours extends HandyEvent.RequestEvent {

        public final int bookingId;
        public final BookingEditHoursRequest bookingEditHoursRequest;

        public RequestEditHours(
                final int bookingId,
                final BookingEditHoursRequest bookingEditHoursRequest
        ) {
            this.bookingId = bookingId;
            this.bookingEditHoursRequest = bookingEditHoursRequest;
        }
    }


    public static class ReceiveEditHoursSuccess extends BookingEvent.ReceiveBookingSuccessEvent {

        public final SuccessWrapper successWrapper;

        public ReceiveEditHoursSuccess(final SuccessWrapper successWrapper) {
            this.successWrapper = successWrapper;
        }
    }


    public static class ReceiveEditHoursError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditHoursError(DataManager.DataManagerError error) {
            this.error = error;
        }

    }


    public static class RequestEditBookingAddress extends HandyEvent.RequestEvent {

        public final int bookingId;
        public final EditAddressRequest mEditAddressRequest;

        public RequestEditBookingAddress(
                final int bookingId,
                final EditAddressRequest editAddressRequest
        ) {
            this.bookingId = bookingId;
            this.mEditAddressRequest = editAddressRequest;
        }
    }


    public static class ReceiveEditBookingAddressSuccess extends HandyEvent.ReceiveSuccessEvent {
    }


    public static class ReceiveEditBookingAddressError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveEditBookingAddressError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestEditPreferences extends HandyEvent.RequestEvent {

        private int mBookingId;
        private FinalizeBookingRequestPayload mPayload;

        private RequestEditPreferences() {
        }

        public RequestEditPreferences(
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


    public static class ReceiveEditPreferencesSuccess extends HandyEvent.ReceiveSuccessEvent {}


    public static class ReceiveEditPreferencesError extends HandyEvent.ReceiveErrorEvent {}


}
