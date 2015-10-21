package com.handybook.handybook.event;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingPricesForFrequenciesResponse;
import com.handybook.handybook.core.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.core.BookingUpdateFrequencyTransaction;
import com.handybook.handybook.core.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.model.BookingCardViewModel;

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
    public static class RequestUpdateBookingFrequency extends Request
    {
        public final int bookingId;
        public final BookingUpdateFrequencyTransaction bookingUpdateFrequencyTransaction;

        public RequestUpdateBookingFrequency(int bookingId, BookingUpdateFrequencyTransaction bookingUpdateFrequencyTransaction)
        {
            this.bookingId = bookingId;
            this.bookingUpdateFrequencyTransaction = bookingUpdateFrequencyTransaction;
        }
    }

    public static class ReceiveUpdateBookingFrequencySuccess extends ReceiveSuccessEvent
    {
        public ReceiveUpdateBookingFrequencySuccess()
        {
        }
    }

    public static class ReceiveUpdateBookingFrequencyError extends ReceiveErrorEvent
    {
        public ReceiveUpdateBookingFrequencyError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Get the booking prices for each booking frequency
    public static class RequestGetBookingPricesForFrequencies extends Request
    {
        public final int bookingId;

        public RequestGetBookingPricesForFrequencies(int bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveGetBookingPricesForFrequenciesSuccess extends ReceiveSuccessEvent
    {
        public final BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse;
        public ReceiveGetBookingPricesForFrequenciesSuccess(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
        {
            this.bookingPricesForFrequenciesResponse = bookingPricesForFrequenciesResponse;
        }
    }

    public static class ReceiveGetBookingPricesForFrequenciesError extends ReceiveErrorEvent
    {
        public ReceiveGetBookingPricesForFrequenciesError(DataManager.DataManagerError error)
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

}
