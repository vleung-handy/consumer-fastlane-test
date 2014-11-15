package com.handybook.handybook;

import com.squareup.otto.Bus;

import java.util.List;

abstract class DataManager {
    static enum Environment {P, S, Q1, Q2, Q3, Q4}
    private Environment env = Environment.S;
    private final Bus bus;

    DataManager(final Bus bus) {
        this.bus = bus;
    }

    Environment getEnvironment() {
        return env;
    }

    void setEnvironment(Environment env) {
        bus.post(new EnvironmentUpdatedEvent(env, this.env));
        this.env = env;
    }

    abstract void getServices(CacheResponse<List<Service>> cache, Callback<List<Service>> cb);

    abstract void validateBookingZip(int serviceId, String zipCode, Callback<Void> cb);

    abstract void getBookings(User user, Callback<List<Booking>> cb);

    abstract void getBookingOptions(int serviceId, String userId, Callback<List<BookingOption>> cb);

    abstract void getBookingQuote(BookingRequest bookingRequest, Callback<BookingQuote> cb);

    abstract void completeBooking(BookingTransaction bookingTransaction, Callback<String> cb);

    abstract void authUser(String email, String password, Callback<User> cb);

    abstract void getUser(String userId, String authToken, Callback<User> cb);

    abstract void getUser(String email, Callback<String> cb);

    abstract void updateUser(User user, Callback<User> cb);

    abstract void authFBUser(String fbid, String accessToken, String email, String firstName,
                             String lastName, Callback<User> cb);

    abstract void requestPasswordReset(String email, Callback<String> cb);

    static interface Callback<T> {
        void onSuccess(T response);
        void onError(DataManagerError error);
    }

    static interface CacheResponse<T> {
        void onResponse(T response);
    }

    static enum Type {OTHER, SERVER, CLIENT, NETWORK}

    static final class DataManagerError {
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

        final void setInvalidInputs(String[] inputs) {
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
