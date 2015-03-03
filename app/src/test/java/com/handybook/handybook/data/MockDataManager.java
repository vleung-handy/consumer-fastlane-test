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
import com.squareup.otto.Bus;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jwilliams on 2/24/15.
 */
public class MockDataManager extends DataManager {

    @Inject
    public MockDataManager(final HandyRetrofitService service, final HandyEndpoint endpoint, final Bus bus) {
        super(bus);
    }

    @Override
    public void getServices(CacheResponse<List<Service>> cache, Callback<List<Service>> cb) {

    }

    @Override
    public void validateBookingZip(int serviceId, String zipCode, String userId, String authToken, String promoCode, Callback<Void> cb) {

    }

    @Override
    public void getBookings(User user, Callback<List<Booking>> cb) {

    }

    @Override
    public void getBookingOptions(int serviceId, String userId, Callback<List<BookingOption>> cb) {

    }

    @Override
    public void getBookingQuote(BookingRequest bookingRequest, Callback<BookingQuote> cb) {

    }

    @Override
    public void updateBookingDate(int bookingId, Date date, Callback<BookingQuote> cb) {

    }

    @Override
    public void applyPromo(String promoCode, int bookingId, String userId, String email, String authToken, Callback<BookingCoupon> cb) {

    }

    @Override
    public void removePromo(int bookingId, Callback<BookingCoupon> cb) {

    }

    @Override
    public void getPreBookingPromo(String promoCode, Callback<PromoCode> cb) {

    }

    @Override
    public void completeBooking(BookingTransaction bookingTransaction, Callback<BookingCompleteTransaction> cb) {

    }

    @Override
    public void getPreRescheduleInfo(String bookingId, Callback<String> cb) {

    }

    @Override
    public void rescheduleBooking(String bookingId, String date, boolean rescheduleAll, String userId, String authToken, Callback<Pair<String, BookingQuote>> cb) {

    }

    @Override
    public void getPreCancelationInfo(String bookingId, Callback<Pair<String, List<String>>> cb) {

    }

    @Override
    public void cancelBooking(String bookingId, int reasonCode, String userId, String authToken, Callback<String> cb) {

    }

    @Override
    public void addBookingPostInfo(int bookingId, BookingPostInfo postInfo, Callback<Void> cb) {

    }

    @Override
    public void authUser(String email, String password, Callback<User> cb) {

    }

    @Override
    public void getUser(String userId, String authToken, Callback<User> cb) {

    }

    @Override
    public void getUser(String email, Callback<String> cb) {

    }

    @Override
    public void updateUser(User user, Callback<User> cb) {

    }

    @Override
    public void authFBUser(String fbid, String accessToken, String email, String firstName, String lastName, Callback<User> cb) {

    }

    @Override
    public void requestPasswordReset(String email, Callback<String> cb) {

    }
}
