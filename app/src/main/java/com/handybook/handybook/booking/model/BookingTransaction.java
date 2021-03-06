package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;

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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class BookingTransaction extends Observable {

    @SerializedName("booking_id")
    private int mBookingId;
    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("service_id")
    private int mServiceId;
    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("address1")
    private String mAddress1;
    @SerializedName("address2")
    private String mAddress2;
    @SerializedName("city")
    private String mCity;
    @SerializedName("state")
    private String mState;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("zipcode")
    private String mZipCode;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("hrs")
    private float mHours;

    /**
     * This frequency field is used both in the legacy pricing tables, and the new
     * {@link com.handybook.handybook.booking.model.subscription.CommitmentType} pricing
     */
    @SerializedName("updated_recurring_freq")
    private int mRecurringFrequency;

    @SerializedName("extra_cleaning_text")
    private String mExtraCleaningText;
    @SerializedName("extra_hours")
    private float mExtraHours;
    @SerializedName("date_start")
    private Date mStartDate;
    @SerializedName("auth_token")
    private String mAuthToken;
    @SerializedName("stripe_token")
    private String mStripeToken;
    @SerializedName("payment_method")
    private String mPaymentMethod;
    @SerializedName("button_referrer_token")
    private String mReferrerToken;
    @SerializedName("_android_promo_applied")
    private String mPromoCode;

    //this is used for creating additional params to post for the create booking
    private Map<String, Object> mAdditionalQueryParamMap;

    /**
     * TODO for ugly promo code hotfix
     * ideally this shouldn't be sent to server
     * (this entire object is sent to the /quotes/{quote} endpoint)
     * but need this field in this obj for a hotfix
     *
     * this is set to true when we get a promo code that is not set by the user or deeplink
     * and will make the promo text field not show
     */
    @SerializedName("should_promo_code_be_hidden")
    private boolean mShouldPromoCodeBeHidden;

    /**
     * holds values like : {"no_commitment", "months"}
     */
    @SerializedName("commitment_type")
    private String mCommitmentType;

    /**
     * Holds values like : {3, 6, 9}
     */
    @SerializedName("commitment_length")
    private int mCommitmentLength;

    public boolean shouldPromoCodeBeHidden() {
        return mShouldPromoCodeBeHidden;
    }

    public int getBookingId() {
        return mBookingId;
    }

    public void setBookingId(final int bookingId) {
        mBookingId = bookingId;
        triggerObservers();
    }

    final String getUserId() {
        return mUserId;
    }

    public void setUserId(final String userId) {
        mUserId = userId;
        triggerObservers();
    }

    public final int getServiceId() {
        return mServiceId;
    }

    public void setServiceId(final int serviceId) {
        mServiceId = serviceId;
        triggerObservers();
    }

    public String getProviderId() {
        return mProviderId;
    }

    public void setProviderId(final String providerId) {
        mProviderId = providerId;
        triggerObservers();
    }

    final String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(final String firstName) {
        mFirstName = firstName;
        triggerObservers();
    }

    final String getLastName() {
        return mLastName;
    }

    public void setLastName(final String lastName) {
        mLastName = lastName;
        triggerObservers();
    }

    final String getAddress1() {
        return mAddress1;
    }

    public void setAddress1(final String address1) {
        mAddress1 = address1;
        triggerObservers();
    }

    final String getAddress2() {
        return mAddress2;
    }

    public void setAddress2(final String address2) {
        mAddress2 = address2;
        triggerObservers();
    }

    final String getCity() {
        return mCity;
    }

    public void setCity(final String city) {
        mCity = city;
        triggerObservers();
    }

    final String getState() {
        return mState;
    }

    public void setState(final String state) {
        mState = state;
        triggerObservers();
    }

    public String getCommitmentType() {
        return mCommitmentType;
    }

    public void setCommitmentType(final String commitmentType) {
        mCommitmentType = commitmentType;
        triggerObservers();
    }

    public int getCommitmentLength() {
        return mCommitmentLength;
    }

    public void setCommitmentLength(final int commitmentLength) {
        mCommitmentLength = commitmentLength;
        triggerObservers();
    }

    final String getPhone() {
        return mPhone;
    }

    public void setPhone(final String phone) {
        mPhone = phone;
        triggerObservers();
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(final String zipCode) {
        mZipCode = zipCode;
        triggerObservers();
    }

    public final String getEmail() {
        return mEmail;
    }

    public void setEmail(final String email) {
        mEmail = email;
        triggerObservers();
    }

    public float getHours() {
        return mHours;
    }

    public void setHours(final float hours) {
        mHours = hours;
        triggerObservers();
    }

    public int getRecurringFrequency() {
        return mRecurringFrequency;
    }

    public void setRecurringFrequency(final int recurringFrequency) {
        mRecurringFrequency = recurringFrequency;
        triggerObservers();
    }

    public String getExtraCleaningText() {
        return mExtraCleaningText;
    }

    public void setExtraCleaningText(final String extraCleaningText) {
        mExtraCleaningText = extraCleaningText;
        triggerObservers();
    }

    public float getExtraHours() {
        return mExtraHours;
    }

    public void setExtraHours(final float extraHours) {
        mExtraHours = extraHours;
        triggerObservers();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(final Date startDate) {
        mStartDate = startDate;
        triggerObservers();
    }

    final String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(final String authToken) {
        mAuthToken = authToken;
        triggerObservers();
    }

    final String getStripeToken() {
        return mStripeToken;
    }

    public void setStripeToken(final String stripeToken) {
        mStripeToken = stripeToken;
        triggerObservers();
    }

    public String getPromoCode() {
        return mPromoCode;
    }

    /**
     * requiring both the promo code and its hidden state to be passed as arguments
     * to enforce that the promo hidden state gets updated whenever the promo code does
     * @param promoCode
     * @param shouldPromoCodeBeHidden we want the promo code hidden if not from deeplink or user input (temporary solution for hotfix)
     */
    public void setPromoCode(final String promoCode, final boolean shouldPromoCodeBeHidden) {
        mPromoCode = promoCode;
        mShouldPromoCodeBeHidden = shouldPromoCodeBeHidden;
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
                                                   BookingTransaction.class,
                                                   new BookingTransactionSerializer()
                                           ).create();

        return gson.toJson(this);
    }

    public static BookingTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat(
                DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                                .create()
                                .fromJson(json, BookingTransaction.class);
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

    public void setPaymentMethod(String paymentMethod) {
        mPaymentMethod = paymentMethod;
        triggerObservers();
    }

    public String getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setReferrerToken(final String referrerToken) {
        mReferrerToken = referrerToken;
        triggerObservers();
    }

    public Map<String, Object> getAdditionalQueryParamMap() {
        return mAdditionalQueryParamMap;
    }

    /**
     * This is used to set additional query parameters for create a booking post
     * @param key Key name for the query param
     * @param value value for the query param
     */
    public void setAddtionalQueryParam(@NonNull String key, @NonNull Object value) {
        if(mAdditionalQueryParamMap == null)
            mAdditionalQueryParamMap = new HashMap<>();

        mAdditionalQueryParamMap.put(key, value);
    }

    public String getReferrerToken() {
        return mReferrerToken;
    }

    public static final class BookingTransactionSerializer
            implements JsonSerializer<BookingTransaction> {

        @Override
        public final JsonElement serialize(
                final BookingTransaction value, final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("booking_id", context.serialize(value.getBookingId()));
            jsonObj.add("user_id", context.serialize(value.getUserId()));
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("provider_id", context.serialize(value.getProviderId()));
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("address1", context.serialize(value.getAddress1()));
            jsonObj.add("address2", context.serialize(value.getAddress2()));
            jsonObj.add("city", context.serialize(value.getCity()));
            jsonObj.add("state", context.serialize(value.getState()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("zipcode", context.serialize(value.getZipCode()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("date_start", context.serialize(value.getStartDate()));
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("stripe_token", context.serialize(value.getStripeToken()));
            jsonObj.add("payment_method", context.serialize(value.getPaymentMethod()));
            jsonObj.add("extra_cleaning_text", context.serialize(value.getExtraCleaningText()));
            jsonObj.add("mobile", context.serialize(1));
            jsonObj.add("button_referrer_token", context.serialize(value.getReferrerToken()));
            jsonObj.add("_android_promo_applied", context.serialize(value.getPromoCode()));

            Map<String, Object> map = value.getAdditionalQueryParamMap();
            if(map != null) {
                for (String key : map.keySet()) {
                    jsonObj.add(key, context.serialize(map.get(key)));
                }
            }

            jsonObj.add(
                    "should_promo_code_be_hidden",
                    context.serialize(value.shouldPromoCodeBeHidden())
            );

            if (value.getCommitmentType() != null) {
                jsonObj.add("commitment_type", context.serialize(value.getCommitmentType()));
            }

            final int recur = value.getRecurringFrequency();

            if (recur > 0) {
                jsonObj.add(
                        "updated_recurring_freq",
                        context.serialize(Integer.toString(recur))
                );
            }

            final float extraHours = value.getExtraHours();
            if (extraHours > 0) { jsonObj.add("extra_hours", context.serialize(extraHours)); }

            final int comLength = value.getCommitmentLength();
            if (comLength > 0)     //if the server didn't receive this value, it'll assume 0
            {
                jsonObj.add("commitment_length", context.serialize(comLength));
            }

            return jsonObj;
        }
    }
}
