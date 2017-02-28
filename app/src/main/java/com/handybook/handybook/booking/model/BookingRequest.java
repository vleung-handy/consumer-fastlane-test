package com.handybook.handybook.booking.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.TextUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class BookingRequest extends Observable {

    public static final String KEY_UNIQ = "uniq";
    public static final String KEY_SERVICE_ID = "service_id";
    public static final String KEY_PROVIDER_ID = "provider_id";
    public static final String KEY_ZIPCODE = "zipcode";
    public static final String KEY_ZIP_AREA = "zip_area";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SERVICE_ATTRIBUTES = "service_attributes";
    public static final String KEY_DATE_START = "date_start";
    public static final String KEY_ENTERED_CODE = "entered_code";
    public static final String KEY_ANDROID_PROMO_TYPE = "_android_promo_type";
    public static final String KEY_COUPON = "coupon";

    @SerializedName(KEY_SERVICE_ID)
    private int mServiceId;
    @SerializedName(KEY_PROVIDER_ID)
    private String mProviderId;
    @SerializedName(KEY_UNIQ)
    private String mUniq;

    //zipCode is needed for POSTing to the server
    @SerializedName(KEY_ZIPCODE)
    private String mZipCode;

    //zip area is a convenience method for holding information relevant to the current booking request
    @SerializedName(KEY_ZIP_AREA)
    private ZipValidationResponse.ZipArea mZipArea;

    @SerializedName(KEY_EMAIL)
    private String mEmail;
    @SerializedName(KEY_USER_ID)
    private String mUserId;
    @SerializedName(KEY_SERVICE_ATTRIBUTES)
    private HashMap<String, String> mOptions;
    @SerializedName(KEY_DATE_START)
    private Date mStartDate;
    @SerializedName(KEY_ENTERED_CODE)
    private String mPromoCode;
    @SerializedName(KEY_ANDROID_PROMO_TYPE)
    private PromoCode.Type mPromoType;
    @SerializedName(KEY_COUPON)
    private String mCoupon;

    transient private String mTimeZone;

    public int getServiceId() {
        return mServiceId;
    }

    public void setServiceId(final int serviceId) {
        mServiceId = serviceId;
        triggerObservers();
    }

    public String getUniq() {
        return mUniq;
    }

    public void setUniq(final String uniq) {
        mUniq = uniq;
        triggerObservers();
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(final String zipCode) {
        mZipCode = zipCode;
        triggerObservers();
    }

    public ZipValidationResponse.ZipArea getZipArea() {
        return mZipArea;
    }

    public void setZipArea(final ZipValidationResponse.ZipArea zipArea) {
        mZipArea = zipArea;
        triggerObservers();
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(final String email) {
        mEmail = email;
        triggerObservers();
    }

    final String getUserId() {
        return mUserId;
    }

    public void setUserId(final String userId) {
        mUserId = userId;
        triggerObservers();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(final Date startDate) {
        mStartDate = startDate;
        triggerObservers();
    }

    public HashMap<String, String> getOptions() {
        if (mOptions == null) { mOptions = new HashMap<>(); }
        return mOptions;
    }

    public void setOptions(final HashMap<String, String> options) {
        mOptions = options;
        triggerObservers();
    }

    public String getPromoCode() {
        return mPromoCode;
    }

    public void setPromoCode(final String promoCode) {
        mPromoCode = promoCode;
        triggerObservers();
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(final String timeZone) {
        mTimeZone = timeZone;
        triggerObservers();
    }

    public PromoCode.Type getPromoType() {
        return mPromoType;
    }

    public void setPromoType(final PromoCode.Type promoType) {
        mPromoType = promoType;
        triggerObservers();
    }

    public String getCoupon() {
        return mCoupon;
    }

    public void setCoupon(final String coupon) {
        mCoupon = coupon;
        triggerObservers();
    }

    public String getProviderId() {
        return mProviderId;
    }

    public void setProviderId(final String providerId) {
        mProviderId = providerId;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    public final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                                           .setExclusionStrategies(getExclusionStrategy())
                                           .registerTypeAdapter(
                                                   BookingRequest.class,
                                                   new BookingRequestSerializer()
                                           ).create();

        return gson.toJson(this);
    }

    public static BookingRequest fromJson(final String json) {
        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                                .fromJson(json, BookingRequest.class);
    }

    public static ExclusionStrategy getExclusionStrategy() {
        return new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        };
    }

    public static final class BookingRequestSerializer implements JsonSerializer<BookingRequest> {

        @Override
        public JsonElement serialize(
                final BookingRequest value, final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add(KEY_SERVICE_ID, context.serialize(value.getServiceId()));
            jsonObj.add(KEY_PROVIDER_ID, context.serialize(value.getProviderId()));
            jsonObj.add(KEY_UNIQ, context.serialize(value.getUniq()));
            jsonObj.add(KEY_ZIPCODE, context.serialize(value.getZipCode()));
            jsonObj.add(KEY_ZIP_AREA, context.serialize(value.getZipArea()));
            jsonObj.add(KEY_EMAIL, context.serialize(value.getEmail()));
            jsonObj.add(KEY_USER_ID, context.serialize(value.getUserId()));
            jsonObj.add(KEY_SERVICE_ATTRIBUTES, context.serialize(value.getOptions()));
            jsonObj.add(KEY_DATE_START, context.serialize(
                    TextUtils.formatDate(
                            value.getStartDate(),
                            DateTimeUtils.UNIVERSAL_DATE_FORMAT
                    )
            ));
            jsonObj.add(KEY_ENTERED_CODE, context.serialize(value.getPromoCode()));
            jsonObj.add(KEY_ANDROID_PROMO_TYPE, context.serialize(value.getPromoType()));
            jsonObj.add(KEY_COUPON, context.serialize(value.getCoupon()));
            // jsonObj.add("mobile", context.serialize(1)); //If this doesn't break things delete it.
            // Doesn't seem to be used fo anything useful on the backend and it is defaulted to true
            return jsonObj;
        }
    }
}
