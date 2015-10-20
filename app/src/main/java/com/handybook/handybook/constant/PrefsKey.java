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
    APP_FIRST_RUN("APP_FIRST_RUN"),
    ENVIRONMENT_PREFIX("ENVIRONMENT_PREFIX"),;

    private String mKey;

    PrefsKey(String key)
    {
        mKey = key;
    }

    public String getKey()
    {
        return this.mKey;
    }

    @Override
    public String toString()
    {
        return this.getKey();
    }
}
