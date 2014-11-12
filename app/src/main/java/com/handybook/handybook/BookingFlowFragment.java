package com.handybook.handybook;

import android.content.Intent;

import javax.inject.Inject;

public class BookingFlowFragment extends InjectedFragment {

    @Inject BookingManager bookingManager;

    protected void showBookingAddress(final BookingQuote quote) {
        bookingManager.setCurrentQuote(quote);

        final BookingTransaction transaction = new BookingTransaction();
        transaction.setHours(quote.getHours());
        transaction.setStartDate(quote.getStartDate());
        bookingManager.setCurrentTransaction(transaction);

        final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
        startActivity(intent);
    }
}
