package com.handybook.handybook;

import android.content.Intent;

import javax.inject.Inject;

public class BookingFlowFragment extends InjectedFragment {

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;

    protected void showBookingAddress(final BookingQuote quote) {
        bookingManager.setCurrentQuote(quote);

        final User user = userManager.getCurrentUser();
        final BookingTransaction transaction = new BookingTransaction();

        transaction.setBookingId(quote.getBookingId());
        transaction.setHours(quote.getHours());
        transaction.setStartDate(quote.getStartDate());
        transaction.setZipCode(quote.getZipCode());
        transaction.setUserId(quote.getUserId());
        transaction.setServiceId(quote.getServiceId());
        transaction.setEmail(user.getEmail());
        transaction.setAuthToken(user.getAuthToken());
        bookingManager.setCurrentTransaction(transaction);

        final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
        startActivity(intent);
    }
}
