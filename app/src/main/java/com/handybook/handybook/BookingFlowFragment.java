package com.handybook.handybook;

import android.content.Intent;

import java.util.ArrayList;

public class BookingFlowFragment extends InjectedFragment {

    protected void continueBookingFlow() {
        if (BookingFlowFragment.this instanceof PeakPricingTableFragment) {
            disableInputs();
            progressDialog.show();

            final BookingQuote quote = bookingManager.getCurrentQuote();
            dataManager.updateBookingDate(quote.getBookingId(), quote.getStartDate(),
                    bookingQuoteCallback);
            return;
        }

        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();

        if (user != null) {
            request.setUserId(user.getId());
            request.setEmail(user.getEmail());
        }
        else if (request.getEmail() == null) {
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_IS_FOR_BOOKING, true);
            startActivity(intent);
            return;
        }

        disableInputs();
        progressDialog.show();
        dataManager.getBookingQuote(request, bookingQuoteCallback);
    }

    private DataManager.Callback<BookingQuote> bookingQuoteCallback
            = new DataManager.Callback<BookingQuote>() {
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

            if (user != null) {
                transaction.setEmail(user.getEmail());
                transaction.setAuthToken(user.getAuthToken());
            }
            else transaction.setEmail(bookingManager.getCurrentRequest().getEmail());

            bookingManager.setCurrentTransaction(transaction);

            if (quote.hasRecurring()) {
                final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
                startActivity(intent);
                enableInputs();
                progressDialog.dismiss();
                return;
            }

            final ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakTable
                    = quote.getPeakPriceTable();

            if (!(BookingFlowFragment.this instanceof PeakPricingFragment) &&
                    !(BookingFlowFragment.this instanceof PeakPricingTableFragment) && peakTable != null
                    && !peakTable.isEmpty()) {
                final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
                startActivity(intent);
                enableInputs();
                progressDialog.dismiss();
                return;
            }

            final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
            startActivity(intent);

            if (user != null && BookingFlowFragment.this instanceof LoginFragment)
                BookingFlowFragment.this.getActivity().finish();

            enableInputs();
            progressDialog.dismiss();
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;

            enableInputs();
            progressDialog.dismiss();
            dataManagerErrorHandler.handleError(getActivity(), error);

            if (BookingFlowFragment.this instanceof LoginFragment)
                BookingFlowFragment.this.getActivity().finish();
        }
    };
}
