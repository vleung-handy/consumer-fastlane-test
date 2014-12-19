package com.handybook.handybook;

public final class PromoCode {
    enum Type {COUPON, VOUCHER}

    private Type type;
    private String code, uniq;
    private int serviceId;

    PromoCode(final Type type, final String code) {
        this.type = type;
        this.code = code;
    }

    final Type getType() {
        return type;
    }

    final void setType(final Type type) {
        this.type = type;
    }

    final String getCode() {
        return code;
    }

    final void setCode(final String code) {
        this.code = code;
    }

    final String getUniq() {
        return uniq;
    }

    final void setUniq(final String uniq) {
        this.uniq = uniq;
    }

    final int getServiceId() {
        return serviceId;
    }

    final void setServiceId(final int serviceId) {
        this.serviceId = serviceId;
    }
}
