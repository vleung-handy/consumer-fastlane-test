package com.handybook.handybook;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class BookingRequestManager implements Observer {
    private BookingRequest request;
    private final SecurePreferences securePrefs;

    @Inject
    BookingRequestManager(final SecurePreferences prefs) {
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
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof BookingRequest) setCurrentRequest((BookingRequest)observable);
    }
}
