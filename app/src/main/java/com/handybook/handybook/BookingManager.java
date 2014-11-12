package com.handybook.handybook;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class BookingManager implements Observer {
    private BookingRequest request;
    private BookingQuote quote;
    private BookingTransaction transaction;
    private final SecurePreferences securePrefs;

    @Inject
    BookingManager(final SecurePreferences prefs) {
        this.securePrefs = prefs;
    }

    final BookingRequest getCurrentRequest() {
        if (request != null) return request;
        else {
            if ((request = BookingRequest.fromJson(securePrefs.getString("BOOKING_REQ"))) != null)
                request.addObserver(this);
            return request;
        }
    }

    final void setCurrentRequest(final BookingRequest newRequest) {
        if (request != null) request.deleteObserver(this);

        if (newRequest == null) {
            request = null;
            securePrefs.put("BOOKING_REQ", null);
            return;
        }

        request = newRequest;
        request.addObserver(this);
        securePrefs.put("BOOKING_REQ", request.toJson());

        setCurrentQuote(null);
    }

    final BookingQuote getCurrentQuote() {
        if (quote != null) return quote;
        else {
            if ((quote = BookingQuote.fromJson(securePrefs.getString("BOOKING_QUOTE"))) != null)
                quote.addObserver(this);
            return quote;
        }
    }

    final void setCurrentQuote(final BookingQuote newQuote) {
        if (quote != null) quote.deleteObserver(this);

        if (newQuote == null) {
            quote = null;
            securePrefs.put("BOOKING_QUOTE", null);
            return;
        }

        quote = newQuote;
        quote.addObserver(this);
        securePrefs.put("BOOKING_QUOTE", quote.toJson());

        setCurrentTransaction(null);
    }

    final BookingTransaction getCurrentTransaction() {
        if (transaction != null) return transaction;
        else {
            if ((transaction = BookingTransaction
                    .fromJson(securePrefs.getString("BOOKING_TRANS"))) != null)
                transaction.addObserver(this);
            return transaction;
        }
    }

    final void setCurrentTransaction(final BookingTransaction newTransaction) {
        if (transaction != null) transaction.deleteObserver(this);

        if (newTransaction == null) {
            transaction = null;
            securePrefs.put("BOOKING_TRANS", null);
            return;
        }

        transaction = newTransaction;
        transaction.addObserver(this);
        securePrefs.put("BOOKING_TRANS", transaction.toJson());
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof BookingRequest) setCurrentRequest((BookingRequest)observable);
        if (observable instanceof BookingQuote) setCurrentQuote((BookingQuote)observable);
    }
}
