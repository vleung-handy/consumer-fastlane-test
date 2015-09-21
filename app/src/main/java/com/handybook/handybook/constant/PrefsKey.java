package com.handybook.handybook.constant;

public enum PrefsKey
{
    USER("USER_OBJ"),
    BOOKING_REQUEST("BOOKING_REQ"),
    BOOKING_QUOTE("BOOKING_QUOTE"),
    BOOKING_TRANSACTION("BOOKING_TRANS"),
    BOOKING_POST("BOOKING_POST"),
    BOOKING_PROMO_TAB_COUPON("BOOKING_PROMO_TAB_COUPON"),
    STATE_BOOKING_CLEANING_EXTRAS_SELECTION("STATE_BOOKING_CLEANING_EXTRAS_SEL"),
    CACHED_SERVICES("CACHED_SERVICES"),
    APP_BLOCKED("APP_BLOCKED"),
    APP_BLOCKED_LAST_CHECK("APP_BLOCKED_LAST_CHECK"),
    ;

    private String key;

    PrefsKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    @Override
    public String toString()
    {
        return this.getKey();
    }
}
