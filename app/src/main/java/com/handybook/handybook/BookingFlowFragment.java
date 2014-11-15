package com.handybook.handybook;

import android.content.Intent;

public class BookingFlowFragment extends InjectedFragment {

    protected void showBookingAddress() {
        disableInputs();
        progressDialog.show();

        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();

        if (user != null) request.setUserId(userManager.getCurrentUser().getId());

        dataManager.getBookingQuote(request, new DataManager.Callback<BookingQuote>() {
            @Override
            public void onSuccess(final BookingQuote quote) {
                if (!allowCallbacks) return;

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

                enableInputs();
                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                if (!allowCallbacks) return;

                enableInputs();
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }
}
