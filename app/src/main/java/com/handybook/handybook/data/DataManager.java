package com.handybook.handybook.data;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingCoupon;
import com.handybook.handybook.core.BookingOptionsWrapper;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.model.response.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.core.BookingProRequestResponse;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingRequestablePros;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.model.request.BookingEditExtrasRequest;
import com.handybook.handybook.model.request.BookingEditFrequencyRequest;
import com.handybook.handybook.core.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.model.response.BookingEditExtrasInfoResponse;
import com.handybook.handybook.core.HelpNodeWrapper;
import com.handybook.handybook.core.LaundryDropInfo;
import com.handybook.handybook.core.PromoCode;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserBookingsWrapper;
import com.handybook.handybook.model.request.BookingEditHoursRequest;
import com.handybook.handybook.model.response.BookingEditHoursInfoResponse;

import java.util.Date;
import java.util.List;

import retrofit.mime.TypedInput;

//TODO: Don't need to manually pass auth tokens for any endpoint, auth token is now auto added as
// part of the intercept

public abstract class DataManager
{
    public abstract void getServices(CacheResponse<List<Service>> cache,
                                     Callback<List<Service>> cb);

    public abstract void getServiceExtras(int bookingId,
                                     Callback<BookingEditExtrasInfoResponse> cb);

    public abstract void editServiceExtras(int bookingId,
                                           BookingEditExtrasRequest bookingEditExtrasRequest,
                                          Callback<SuccessWrapper> cb);

    public abstract void getEditHoursInfo(int bookingId,
                                           Callback<BookingEditHoursInfoResponse> cb);

    public abstract void editBookingHours(int bookingId,
                                           BookingEditHoursRequest bookingEditHoursRequest,
                                           Callback<SuccessWrapper> cb);
    /**
     * Requests a ShouldBlockObject defining if the app is recent enough to be used
     * @param versionCode Android version code (Not version name!)
     * @param shouldBlockObjectCacheResponse ..
     * @param shouldBlockObjectCallback ..
     */
    public abstract void getBlockedWrapper(
            final int versionCode,
            final CacheResponse<BlockedWrapper> shouldBlockObjectCacheResponse,
            final Callback<BlockedWrapper> shouldBlockObjectCallback
    );

    public abstract void getQuoteOptions(int serviceId,
                                         String userId,
                                         Callback<BookingOptionsWrapper> cb);

    public abstract void createQuote(BookingRequest bookingRequest,
                                     Callback<BookingQuote> cb);

    public abstract void updateQuoteDate(int quoteId,
                                         Date date,
                                         Callback<BookingQuote> cb);

    public abstract void applyPromo(String promoCode,
                                    int quoteId,
                                    String userId,
                                    String email,
                                    String authToken,
                                    Callback<BookingCoupon> cb);

    public abstract void removePromo(int quoteId,
                                     Callback<BookingCoupon> cb);

    public abstract void createBooking(BookingTransaction bookingTransaction,
                                       Callback<BookingCompleteTransaction> cb);

    public abstract void validateBookingZip(int serviceId,
                                            String zipCode,
                                            String userId,
                                            String authToken,
                                            String promoCode,
                                            Callback<Void> cb);

    public abstract void getBookings(User user,
            Callback<UserBookingsWrapper> cb);

    public abstract void getBookings(
            @NonNull final User user,
            @NonNull @Booking.List.OnlyBookingValues String onlyBookingValues,
            @NonNull Callback<UserBookingsWrapper> cb);

    public abstract void getBooking(String bookingId,
                                    Callback<Booking> cb);

    public abstract void getPreBookingPromo(String promoCode,
                                            Callback<PromoCode> cb);

    public abstract void getPreRescheduleInfo(String bookingId,
                                              Callback<String> cb);

    public abstract void rescheduleBooking(String bookingId,
                                           String date,
                                           boolean rescheduleAll,
                                           String userId,
                                           String authToken,
                                           Callback<Pair<String, BookingQuote>> cb);

    public abstract void getPreCancelationInfo(String bookingId,
                                               Callback<Pair<String, List<String>>> cb);

    public abstract void cancelBooking(String bookingId,
                                       int reasonCode,
                                       String userId,
                                       String authToken,
                                       Callback<String> cb);

    public abstract void getLaundryScheduleInfo(int bookingId,
                                                String authToken,
                                                Callback<LaundryDropInfo> cb);

    public abstract void setLaundryDropOff(int bookingId,
                                           String authToken,
                                           String date,
                                           int hour,
                                           int minute,
                                           String type,
                                           Callback<Void> cb);

    public abstract void getAddLaundryInfo(int bookingId,
                                           String authToken,
                                           Callback<Booking> cb);

    public abstract void addLaundry(int bookingId,
                                    String authToken,
                                    Callback<Void> cb);

    public abstract void addBookingPostInfo(int bookingId,
                                            BookingPostInfo postInfo,
                                            Callback<Void> cb);

    public abstract void updateBookingNoteToPro(int bookingId,
                                                BookingUpdateNoteToProTransaction descriptionTransaction,
                                                Callback<Void> cb);

    public abstract void updateBookingEntryInformation(int bookingId,
                                                       BookingUpdateEntryInformationTransaction entryInformationTransaction,
                                                       Callback<Void> cb);

    public abstract void updateBookingFrequency(int bookingId,
                                                BookingEditFrequencyRequest bookingEditFrequencyRequest,
                                                Callback<Void> cb);

    public abstract void getBookingPricesForFrequencies(int bookingId,
                                                        Callback<BookingEditFrequencyInfoResponse> cb);

    public abstract void ratePro(int bookingId,
                                 int rating,
                                 Integer tipAmount,
                                 Callback<Void> cb);

    public abstract void tipPro(int bookingId,
                                Integer tipAmount,
                                Callback<Void> cb);

    public abstract void submitProRatingDetails(int bookingId,
                                                final String positiveFeedback,
                                                Callback<Void> cb);

    public abstract void authUser(String email,
                                  String password,
                                  Callback<User> cb);

    public abstract void getUser(String userId,
                                 String authToken,
                                 Callback<User> cb);

    public abstract void getUser(String email,
                                 Callback<String> cb);

    public abstract void updateUser(User user,
                                    Callback<User> cb);

    public abstract void authFBUser(String fbid,
                                    String accessToken,
                                    String email,
                                    String firstName,
                                    String lastName, Callback<User> cb);

    public abstract void requestPasswordReset(String email,
                                              Callback<String> cb);

    public abstract void getRequestProInfo(int bookingId,
                                           Callback<BookingRequestablePros> cb);

    public abstract void requestProForBooking(int bookingId,
                                              int requestedProId,
                                              Callback<BookingProRequestResponse> cb);

    public abstract void getHelpInfo(String nodeId,
                                     String authToken,
                                     String bookingId,
                                     Callback<HelpNodeWrapper> cb);

    public abstract void getHelpBookingsInfo(String nodeId,
                                             String authToken,
                                             String bookingId,
                                             Callback<HelpNodeWrapper> cb);

    public abstract void createHelpCase(TypedInput body,
                                        Callback<Void> cb);

    public abstract String getBaseUrl();

    public interface Callback<T>
    {
        void onSuccess(T response);

        void onError(DataManagerError error);
    }


    public interface CacheResponse<T>
    {
        void onResponse(T response);
    }


    enum Type
    {
        OTHER, SERVER, CLIENT, NETWORK
    }

    public static final class DataManagerError
    {
        private final Type type;
        private final String message;
        private String[] invalidInputs;

        DataManagerError(final Type type)
        {
            this.type = type;
            this.message = null;
        }

        DataManagerError(final Type type, final String message)
        {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs()
        {
            return invalidInputs;
        }

        final void setInvalidInputs(final String[] inputs)
        {
            this.invalidInputs = inputs;
        }

        final String getMessage()
        {
            return message;
        }

        final Type getType()
        {
            return type;
        }
    }
}
