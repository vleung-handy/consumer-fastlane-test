package com.handybook.handybook.data;

import android.support.v4.util.Pair;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingCoupon;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.PromoCode;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.squareup.otto.Bus;

import java.util.Date;
import java.util.List;

public abstract class DataManager {
    public static enum Environment {P, S, Q1, Q2, Q3, Q4}
    private Environment env = Environment.S;
    private final Bus bus;

    DataManager(final Bus bus) {
        this.bus = bus;
    }

    public Environment getEnvironment() {
        return env;
    }

    public void setEnvironment(final Environment env, final boolean notify) {
        if (notify) bus.post(new EnvironmentUpdatedEvent(env, this.env));
        this.env = env;
    }

    public abstract void getServices(CacheResponse<List<Service>> cache, Callback<List<Service>> cb);

    public abstract void validateBookingZip(int serviceId, String zipCode, String userId, String authToken,
                                            String promoCode, Callback<Void> cb);

    public abstract void getBookings(User user, Callback<List<Booking>> cb);

    public abstract void getBookingOptions(int serviceId, String userId, Callback<List<BookingOption>> cb);

    public abstract void getBookingQuote(BookingRequest bookingRequest, Callback<BookingQuote> cb);

    public abstract void updateBookingDate(int bookingId, Date date,
                                           Callback<BookingQuote> cb);

    public abstract void applyPromo(String promoCode, int bookingId, String userId, String email,
                                    String authToken, Callback<BookingCoupon> cb);

    public abstract void removePromo(int bookingId, Callback<BookingCoupon> cb);

    public abstract void getPreBookingPromo(String promoCode, Callback<PromoCode> cb);

    public abstract void completeBooking(BookingTransaction bookingTransaction,
                                         Callback<BookingCompleteTransaction> cb);

    public abstract void getPreRescheduleInfo(String bookingId, Callback<String> cb);

    public abstract void rescheduleBooking(String bookingId, String date, boolean rescheduleAll,
                                           String userId, String authToken,
                                           Callback<Pair<String, BookingQuote>> cb);

    public abstract void getPreCancelationInfo(final String bookingId,
                                               final Callback<Pair<String, List>> cb);

    public abstract void addBookingPostInfo(int bookingId, BookingPostInfo postInfo, Callback<Void> cb);

    public abstract void authUser(String email, String password, Callback<User> cb);

    public abstract void getUser(String userId, String authToken, Callback<User> cb);

    public abstract void getUser(String email, Callback<String> cb);

    public abstract void updateUser(User user, Callback<User> cb);

    public abstract void authFBUser(String fbid, String accessToken, String email, String firstName,
                                    String lastName, Callback<User> cb);

    public abstract void requestPasswordReset(String email, Callback<String> cb);

    public static interface Callback<T> {
        void onSuccess(T response);
        void onError(DataManagerError error);
    }

    public static interface CacheResponse<T> {
        void onResponse(T response);
    }

    static enum Type {OTHER, SERVER, CLIENT, NETWORK}

    public static final class DataManagerError {
        private final Type type;
        private final String message;
        private String[] invalidInputs;

        DataManagerError(final Type type) {
            this.type = type;
            this.message = null;
        }

        DataManagerError(final Type type, final String message) {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs() {
            return invalidInputs;
        }

        final void setInvalidInputs(final String[] inputs) {
            this.invalidInputs = inputs;
        }

        final String getMessage() {
            return message;
        }

        final Type getType() {
            return type;
        }
    }
}
