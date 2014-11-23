package com.handybook.handybook;

import android.content.Intent;

import java.util.ArrayList;

public class BookingFlowFragment extends InjectedFragment {

    protected void continueBookingFlow() {
        /*
          don't reload quote after recurrence selection, after extras selection,
          or if user skipped peak pricing
        */
        if (BookingFlowFragment.this instanceof BookingRecurrenceFragment
                || BookingFlowFragment.this instanceof PeakPricingFragment
                || BookingFlowFragment.this instanceof BookingExtrasFragment) {
            continueFlow();
            return;
        }

        // if user skipped, don't reload quote
        if (BookingFlowFragment.this instanceof PeakPricingFragment) {
            continueFlow();
            return;
        }

        // user selected new time, reload quote
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
        else if (!(BookingFlowFragment.this instanceof LoginFragment)) {
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_FIND_USER, true);
            startActivity(intent);
            return;
        }

        disableInputs();
        progressDialog.show();
        dataManager.getBookingQuote(request, bookingQuoteCallback);
    }

    private void continueFlow() {
        final BookingRequest request = bookingManager.getCurrentRequest();
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final BookingTransaction oldTransaction = bookingManager.getCurrentTransaction();
        final User user = userManager.getCurrentUser();
        final BookingTransaction transaction = new BookingTransaction();

        transaction.setBookingId(quote.getBookingId());
        transaction.setHours(quote.getHours());
        transaction.setStartDate(quote.getStartDate());
        transaction.setZipCode(quote.getZipCode());
        transaction.setUserId(quote.getUserId());
        transaction.setServiceId(quote.getServiceId());

        // persist additional transaction fields not contained in quote
        if (oldTransaction != null) {
            transaction.setRecurringFrequency(oldTransaction.getRecurringFrequency());
            transaction.setExtraHours(oldTransaction.getExtraHours());
            transaction.setExtraCleaningText(oldTransaction.getExtraCleaningText());
        }

        if (user != null) {
            transaction.setEmail(user.getEmail());
            transaction.setAuthToken(user.getAuthToken());
        }
        else transaction.setEmail(request.getEmail());

        bookingManager.setCurrentTransaction(transaction);

        final ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakTable
                = quote.getPeakPriceTable();

        // show recurrence options if available
        if (!(BookingFlowFragment.this instanceof BookingRecurrenceFragment)
                && !(BookingFlowFragment.this instanceof PeakPricingFragment)
                && !(BookingFlowFragment.this instanceof PeakPricingTableFragment)
                && !(BookingFlowFragment.this instanceof BookingExtrasFragment)
                && quote.hasRecurring()) {
            final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
            startActivity(intent);
        }

        // show surge pricing options if necessary
        else if (!(BookingFlowFragment.this instanceof PeakPricingFragment) &&
                !(BookingFlowFragment.this instanceof PeakPricingTableFragment)
                && !(BookingFlowFragment.this instanceof BookingExtrasFragment)
                && peakTable != null && !peakTable.isEmpty()) {
            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
            startActivity(intent);
        }

        // show extras for home cleaning
        else if (!(BookingFlowFragment.this instanceof BookingExtrasFragment)
                && request.getUniq().equals("home_cleaning")) {
            final Intent intent = new Intent(getActivity(), BookingExtrasActivity.class);
            startActivity(intent);
        }

        // show address info
        else {
            final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
            startActivity(intent);
        }

        // if user logged in, hide login view on back
        if (user != null && BookingFlowFragment.this instanceof LoginFragment) {
            getActivity().setResult(LoginActivity.RESULT_FINISH);
            getActivity().finish();
        }

        enableInputs();
        progressDialog.dismiss();
    }

    private DataManager.Callback<BookingQuote> bookingQuoteCallback
            = new DataManager.Callback<BookingQuote>() {
        @Override
        public void onSuccess(final BookingQuote quote) {
            if (!allowCallbacks) return;

            // persist extras since api may not return them on subsequent calls
            final BookingQuote oldQuote = bookingManager.getCurrentQuote();
            if (oldQuote != null) quote.setExtrasOptions(oldQuote.getExtrasOptions());

            bookingManager.setCurrentQuote(quote);
            continueFlow();
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;

            enableInputs();
            progressDialog.dismiss();
            dataManagerErrorHandler.handleError(getActivity(), error);

            if (BookingFlowFragment.this instanceof LoginFragment)
                getActivity().setResult(LoginActivity.RESULT_FINISH);
                getActivity().finish();
        }
    };
}
