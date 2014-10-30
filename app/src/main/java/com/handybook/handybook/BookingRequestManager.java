package com.handybook.handybook;

public final class BookingRequestManager {
    private BookingRequest request;

    final BookingRequest getCurrentRequest() {
        return request;
    }

    final void setCurrentRequest(final BookingRequest request) {
        this.request = request;
    }
}
