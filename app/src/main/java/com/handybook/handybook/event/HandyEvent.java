package com.handybook.handybook.event;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.core.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.helpcenter.model.HelpNode;
import com.handybook.handybook.model.request.BookingEditAddressRequest;
import com.handybook.handybook.model.request.BookingEditExtrasRequest;
import com.handybook.handybook.model.request.BookingEditFrequencyRequest;
import com.handybook.handybook.model.request.BookingEditHoursRequest;
import com.handybook.handybook.model.response.RecurringBooking;
import com.handybook.handybook.viewmodel.BookingCardViewModel;
import com.handybook.handybook.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.viewmodel.BookingEditHoursViewModel;
import com.stripe.android.model.Token;

import java.util.List;

import retrofit.mime.TypedInput;

public abstract class HandyEvent
{
    public abstract static class RequestEvent extends HandyEvent
    {
        public static class BookingCardViewModelsEvent extends RequestEvent
        {
            private User mUser;
            @Booking.List.OnlyBookingValues
            private String mOnlyBookingValue;

            public BookingCardViewModelsEvent(
                    @NonNull final User user,
                    @NonNull @Booking.List.OnlyBookingValues final String onlyBookingsValue)
            {
                mUser = user;
                mOnlyBookingValue = onlyBookingsValue;
            }

            public BookingCardViewModelsEvent(@NonNull final User user)
            {
                mUser = user;
            }

            public User getUser()
            {
                return mUser;
            }

            @Booking.List.OnlyBookingValues
            public String getOnlyBookingValue()
            {
                return mOnlyBookingValue;
            }
        }
    }


    public abstract static class ResponseEvent<T> extends HandyEvent
    {
        private T mPayload;

        public ResponseEvent(T payload)
        {
            this.mPayload = payload;
        }

        public T getPayload()
        {
            return mPayload;
        }

        public static class BookingCardViewModels extends ResponseEvent<BookingCardViewModel.List>
        {

            public BookingCardViewModels(BookingCardViewModel.List payload)
            {
                super(payload);
            }
        }


        public static class BookingCardViewModelsError extends ResponseEvent<DataManager.DataManagerError>
        {
            public BookingCardViewModelsError(DataManager.DataManagerError payload)
            {
                super(payload);
            }
        }

    }


    public abstract static class RequestBookingActionEvent extends RequestEvent
    {
        public String bookingId;
    }


    public abstract static class ReceiveSuccessEvent extends HandyEvent
    {
    }


    public abstract static class ReceiveBookingSuccessEvent extends ReceiveSuccessEvent
    {
        public Booking booking;
    }


    public abstract static class ReceiveErrorEvent extends HandyEvent
    {
        public DataManager.DataManagerError error;
    }


    //Bookings


    public static class RequestBookingsForUser extends RequestEvent
    {
        public User user;

        public RequestBookingsForUser(User user)
        {
            this.user = user;
        }
    }


    public static class ReceiveBookingsSuccess extends ReceiveSuccessEvent
    {
        public List<Booking> bookings;

        public ReceiveBookingsSuccess(List<Booking> bookings)
        {
            this.bookings = bookings;
        }

    }


    public static class ReceiveBookingsError extends ReceiveErrorEvent
    {
        public ReceiveBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


//Booking Details


    public static class RequestBookingDetails extends RequestEvent
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


    public static class ReceiveBookingDetailsError extends ReceiveErrorEvent
    {
        public ReceiveBookingDetailsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }


    public static class RequestPreRescheduleInfo extends RequestEvent
    {
        public String bookingId;

        public RequestPreRescheduleInfo(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePreRescheduleInfoSuccess extends ReceiveSuccessEvent
    {
        public String notice;

        public ReceivePreRescheduleInfoSuccess(String notice)
        {
            this.notice = notice;
        }

    }


    public static class ReceivePreRescheduleInfoError extends ReceiveErrorEvent
    {
        public ReceivePreRescheduleInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPreCancelationInfo extends RequestEvent
    {
        public String bookingId;

        public RequestPreCancelationInfo(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


    public static class ReceivePreCancelationInfoSuccess extends ReceiveSuccessEvent
    {
        public Pair<String, List<String>> result;

        public ReceivePreCancelationInfoSuccess(Pair<String, List<String>> result)
        {
            this.result = result;
        }

    }


    public static class ReceivePreCancelationInfoError extends ReceiveErrorEvent
    {
        public ReceivePreCancelationInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


//Edit Booking Events


    //Update the note to pro for a booking
    public static class RequestUpdateBookingNoteToPro extends RequestEvent
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
    public static class ReceiveUpdateBookingNoteToProSuccess extends ReceiveSuccessEvent
    {
        public ReceiveUpdateBookingNoteToProSuccess()
        {

        }
    }


    //
    public static class ReceiveUpdateBookingNoteToProError extends ReceiveErrorEvent
    {
        public ReceiveUpdateBookingNoteToProError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Update the entry information for a booking
    public static class RequestUpdateBookingEntryInformation extends RequestEvent
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
    public static class ReceiveUpdateBookingEntryInformationSuccess extends ReceiveSuccessEvent
    {
        public ReceiveUpdateBookingEntryInformationSuccess()
        {

        }
    }


    //
    public static class ReceiveUpdateBookingEntryInformationError extends ReceiveErrorEvent
    {
        public ReceiveUpdateBookingEntryInformationError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Update the frequency of a booking
    public static class RequestEditBookingFrequency extends RequestEvent
    {
        public final int bookingId;
        public final BookingEditFrequencyRequest bookingEditFrequencyRequest;

        public RequestEditBookingFrequency(int bookingId, BookingEditFrequencyRequest bookingEditFrequencyRequest)
        {
            this.bookingId = bookingId;
            this.bookingEditFrequencyRequest = bookingEditFrequencyRequest;
        }
    }

    public static class ReceiveEditBookingFrequencySuccess extends ReceiveSuccessEvent
    {
        public ReceiveEditBookingFrequencySuccess()
        {
        }
    }

    public static class ReceiveEditBookingFrequencyError extends ReceiveErrorEvent
    {
        public ReceiveEditBookingFrequencyError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Get the booking prices for each booking frequency
    public static class RequestGetEditFrequencyViewModel extends RequestEvent
    {
        public final int bookingId;

        public RequestGetEditFrequencyViewModel(int bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveGetEditFrequencyViewModelSuccess extends ReceiveSuccessEvent
    {
        public final BookingEditFrequencyViewModel bookingEditFrequencyViewModel;

        public ReceiveGetEditFrequencyViewModelSuccess(BookingEditFrequencyViewModel bookingEditFrequencyViewModel)
        {
            this.bookingEditFrequencyViewModel = bookingEditFrequencyViewModel;
        }
    }

    public static class ReceiveGetEditFrequencyViewModelError extends ReceiveErrorEvent
    {
        public ReceiveGetEditFrequencyViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Get the service extras options
    public static class RequestEditBookingExtrasViewModel extends RequestEvent
    {
        public final int bookingId;

        public RequestEditBookingExtrasViewModel(int bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveEditBookingExtrasViewModelSuccess extends ReceiveSuccessEvent
    {
        public final BookingEditExtrasViewModel mBookingEditExtrasViewModel;

        public ReceiveEditBookingExtrasViewModelSuccess(
                BookingEditExtrasViewModel bookingEditExtrasViewModel)
        {
            this.mBookingEditExtrasViewModel = bookingEditExtrasViewModel;
        }
    }

    public static class ReceiveEditBookingExtrasViewModelError extends ReceiveErrorEvent
    {
        public ReceiveEditBookingExtrasViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Update the service extras options
    public static class RequestEditBookingExtras extends RequestEvent
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

    public static class ReceiveEditExtrasSuccess extends ReceiveSuccessEvent
    {
        public final SuccessWrapper successWrapper;

        public ReceiveEditExtrasSuccess(SuccessWrapper successWrapper)
        {
            this.successWrapper = successWrapper;
        }
    }

    public static class ReceiveEditExtrasError extends ReceiveErrorEvent
    {
        public ReceiveEditExtrasError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
    //UI


    public static class SetBookingDetailSectionFragmentActionControlsEnabled extends HandyEvent
    {
        public boolean enabled;

        public SetBookingDetailSectionFragmentActionControlsEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }


    //Help Self Service Center
    public static class RequestHelpNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Booking Node - help node associated with a particular booking
    public static class RequestHelpBookingNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpBookingNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpBookingNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpBookingNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpBookingNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpBookingNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Contact Message
    @Track("pro help contact form submitted")
    public static class RequestNotifyHelpContact extends HandyEvent
    {
        public TypedInput body;

        public RequestNotifyHelpContact(TypedInput body)
        {
            this.body = body;
        }
    }


    public static class ReceiveNotifyHelpContactSuccess extends ReceiveSuccessEvent
    {
        public ReceiveNotifyHelpContactSuccess()
        {
        }
    }


    public static class ReceiveNotifyHelpContactError extends ReceiveErrorEvent
    {
        public ReceiveNotifyHelpContactError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    @Track("consumer app blocking screen displayed")
    public static class BlockingScreenDisplayed extends HandyEvent
    {
    }


    @Track("consumer app blocking screen button clicked")
    public static class BlockingScreenButtonPressed extends HandyEvent
    {
    }


    public static class StartBlockingAppEvent extends HandyEvent
    {

    }


    public static class StopBlockingAppEvent extends HandyEvent
    {

    }

//Rating and Tip

    public static class RateBookingEvent extends RequestEvent
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

    public static class ReceiveRateBookingError extends ReceiveErrorEvent
    {
        public ReceiveRateBookingError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }

    public static class RequestEditHoursInfoViewModel extends RequestEvent
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

    public static class ReceiveEditHoursInfoViewModelError extends ReceiveErrorEvent
    {
        public ReceiveEditHoursInfoViewModelError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }

    public static class RequestEditHours extends RequestEvent
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

    public static class ReceiveEditHoursError extends ReceiveErrorEvent
    {
        public ReceiveEditHoursError(DataManager.DataManagerError error)
        {
            this.error = error;
        }

    }

    public static class RequestTipPro extends RequestEvent
    {
        public final Integer tipAmount;
        public final int bookingId;

        public RequestTipPro(final int bookingId, final Integer tipAmount)
        {
            this.bookingId = bookingId;
            this.tipAmount = tipAmount;
        }
    }

    public static class ReceiveTipProSuccess extends ReceiveSuccessEvent
    {
    }

    public static class ReceiveTipProError extends ReceiveErrorEvent
    {
    }

    public static class RequestSendCancelRecurringBookingEmail extends RequestEvent
    {
        public final int bookingRecurringId;

        public RequestSendCancelRecurringBookingEmail(final int bookingRecurringId)
        {
            this.bookingRecurringId = bookingRecurringId;
        }
    }

    public static class ReceiveSendCancelRecurringBookingEmailSuccess extends ReceiveSuccessEvent
    {
    }

    public static class ReceiveSendCancelRecurringBookingEmailError extends ReceiveErrorEvent
    {
        public ReceiveSendCancelRecurringBookingEmailError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestRecurringBookingsForUser extends RequestEvent
    {
        public final User user;

        public RequestRecurringBookingsForUser(final User user)
        {
            this.user = user;
        }
    }

    public static class ReceiveRecurringBookingsSuccess extends ReceiveSuccessEvent
    {
        public final List<RecurringBooking> recurringBookings;

        public ReceiveRecurringBookingsSuccess(final List<RecurringBooking> recurringBookings)
        {
            this.recurringBookings = recurringBookings;
        }
    }

    public static class ReceiveRecurringBookingsError extends ReceiveErrorEvent
    {
        public ReceiveRecurringBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestEditBookingAddress extends RequestEvent
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

    public static class ReceiveEditBookingAddressSuccess extends ReceiveSuccessEvent
    {
    }

    public static class ReceiveEditBookingAddressError extends ReceiveErrorEvent
    {
        public ReceiveEditBookingAddressError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestUpdatePayment extends RequestEvent
    {
        public final Token token;

        public RequestUpdatePayment(final Token token)
        {
            this.token = token;
        }
    }

    public static class ReceiveUpdatePaymentSuccess extends ReceiveSuccessEvent
    {
    }

    public static class ReceiveUpdatePaymentError extends ReceiveErrorEvent
    {
        public ReceiveUpdatePaymentError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class ApplicationCreated
    {
        private Application mApplication;

        public ApplicationCreated(final Application application)
        {
            mApplication = application;
        }

        public Application getApplication()
        {
            return mApplication;
        }
    }
}
