package com.handybook.handybook;

public final class BookingRequest {
    private int serviceId;
    private String zipCode;

    final int getServiceId() {
        return serviceId;
    }

    final void setServiceId(final int serviceId) {
        this.serviceId = serviceId;
    }

    final String getZipCode() {
        return zipCode;
    }

    final void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }
}
