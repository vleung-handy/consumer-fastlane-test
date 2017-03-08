package com.handybook.handybook.core;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class User extends Observable {

    public static final String PAYMENT_METHOD_ANDROID_PAY = "android_pay";

    @SerializedName("auth_token")
    private String mAuthToken;
    @SerializedName("id")
    private String mId;
    @SerializedName("categorized_credits")
    private CategorizedCredits mCategorizedCredits;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone_country_prefix")
    private String mPhonePrefix;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("currency_char")
    private String mCurrencyChar;
    @SerializedName("currency_suffix")
    private String mCurrencySuffix;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("current_password")
    private String mCurrentPassword;
    @SerializedName("password_confirmation")
    private String mPasswordConfirmation;
    @SerializedName("first_address")
    private Address mAddress;
    @SerializedName("card_info")
    private CreditCard mCreditCard;
    @SerializedName("stripe_key")
    private String mStripeKey;
    @SerializedName("payment_method")
    private String mPaymentMethod;
    @SerializedName("analytics")
    private Analytics mAnalytics;
    @SerializedName("booking_to_rate_id")
    private int mBookingRateId;
    @SerializedName("booking_to_rate_pro_name")
    private String mBookingRatePro;
    @SerializedName("schedule_laundry_booking_id")
    private int mLaundryBookingId;
    @SerializedName("add_laundry_booking_id")
    private int mAddLaundryBookingId;
    @SerializedName("default_tip_amounts")
    private ArrayList<LocalizedMonetaryAmount> mDefaultTipAmounts;
    @SerializedName("enable_recurring_cancellations")
    private boolean mRecurringCancellationsEnabled;
    @SerializedName("enable_recurring_cancellations_email_flow")
    private boolean mRecurringCancellationsEmailFlowEnabled;
    @SerializedName("has_active_subscription")
    private boolean mHasActiveSubscription;

    public User() {}

    public boolean isRecurringCancellationsEnabled() {
        return mRecurringCancellationsEnabled;
    }

    public boolean isRecurringCancellationsEmailFlowEnabled() {
        return mRecurringCancellationsEmailFlowEnabled;
    }

    public final String getAuthToken() {
        return mAuthToken;
    }

    public final void setAuthToken(final String authToken) {
        mAuthToken = authToken;
        triggerObservers();
    }

    public final String getId() {
        return mId;
    }

    public final void setId(final String id) {
        mId = id;
        triggerObservers();
    }

    public final String getFirstName() {
        return mFirstName;
    }

    public final void setFirstName(final String firstName) {
        mFirstName = firstName;
        triggerObservers();
    }

    public final String getLastName() {
        return mLastName;
    }

    public final void setLastName(final String lastName) {
        mLastName = lastName;
        triggerObservers();
    }

    public final String getFullName() {
        return mFirstName + " " + mLastName;
    }

    public final String getEmail() {
        return mEmail;
    }

    public final void setEmail(final String email) {
        mEmail = email;
        triggerObservers();
    }

    public final String getPhonePrefix() {
        return mPhonePrefix;
    }

    final void setPhonePrefix(final String phonePrefix) {
        mPhonePrefix = phonePrefix;
        triggerObservers();
    }

    public final String getPhone() {
        final int phoneLen = mPhone != null ? mPhone.length() : 0;
        if (mPhone != null & phoneLen > 10) { return mPhone.substring(phoneLen - 10); }
        return mPhone;
    }

    public final void setPhone(final String phone) {
        mPhone = phone;
        triggerObservers();
    }

    public String getCurrencyChar() {
        return mCurrencyChar;
    }

    final void setCurrencyChar(final String currencyChar) {
        mCurrencyChar = currencyChar;
        triggerObservers();
    }

    final String getCurrencySuffix() {
        return mCurrencySuffix;
    }

    final void setCurrencySuffix(final String currencySuffix) {
        mCurrencySuffix = currencySuffix;
        triggerObservers();
    }

    final String getPassword() {
        return mPassword;
    }

    public final void setPassword(final String password) {
        mPassword = password;
    }

    final String getCurrentPassword() {
        return mCurrentPassword;
    }

    public final void setCurrentPassword(final String currentPassword) {
        mCurrentPassword = currentPassword;
    }

    final String getPasswordConfirmation() {
        return mPasswordConfirmation;
    }

    public final void setPasswordConfirmation(final String passwordConfirmation) {
        mPasswordConfirmation = passwordConfirmation;
    }

    public final Address getAddress() {
        return mAddress;
    }

    final void setAddress(final Address address) {
        mAddress = address;
        triggerObservers();
    }

    public CreditCard getCreditCard() {
        return mCreditCard;
    }

    public boolean isUsingAndroidPay() {
        return mPaymentMethod != null &&
               mPaymentMethod.equalsIgnoreCase(PAYMENT_METHOD_ANDROID_PAY);
    }

    final void setCreditCard(final CreditCard creditCard) {
        mCreditCard = creditCard;
        triggerObservers();
    }

    public Analytics getAnalytics() {
        return mAnalytics;
    }

    final void setAnalytics(final Analytics analytics) {
        mAnalytics = analytics;
        triggerObservers();
    }

    public final int getBookingRateId() {
        return mBookingRateId;
    }

    public final String getBookingRatePro() {
        return mBookingRatePro;
    }

    public ArrayList<LocalizedMonetaryAmount> getDefaultTipAmounts() {
        return mDefaultTipAmounts;
    }

    public final int getLaundryBookingId() {
        return mLaundryBookingId;
    }

    public final int getAddLaundryBookingId() {
        return mAddLaundryBookingId;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                                           .setExclusionStrategies(getExclusionStrategy())
                                           .registerTypeAdapter(User.class, new UserSerializer())
                                           .create();

        return gson.toJson(this);
    }

    public static User fromJson(final String json) {
        try {
            return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                                    .fromJson(json, User.class);
        }
        catch (Exception e) {
            Crashlytics.logException(
                    new RuntimeException("Unable to deserialize:" + json + ":" + e.getMessage(), e)
            );
            return null;
        }
    }

    static ExclusionStrategy getExclusionStrategy() {
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

    public String getStripeKey() {
        return mStripeKey;
    }

    @NonNull
    public CategorizedCredits getCategorizedCredits() {
        if (mCategorizedCredits == null) {
            mCategorizedCredits = new CategorizedCredits();
        }
        return mCategorizedCredits;
    }

    /**
     * Returns true even if subscription credits are ZERO, this checks for absence check, not value
     */
    public boolean hasSubscriptionCreditsValue() {
        return getCategorizedCredits().getSubscriptionCredits() > 0;
    }

    public boolean hasActiveSubscription() {
        return mHasActiveSubscription;
    }

    static final class UserSerializer implements JsonSerializer<User> {

        @Override
        public final JsonElement serialize(
                final User value, final Type type,
                final JsonSerializationContext context
        ) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("id", context.serialize(value.getId()));
            jsonObj.add("categorized_credits", context.serialize(value.getCategorizedCredits()));
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("phone_country_prefix", context.serialize(value.getPhonePrefix()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            jsonObj.add("first_address", context.serialize(value.getAddress()));
            jsonObj.add("card_info", context.serialize(value.getCreditCard()));
            jsonObj.add("stripe_key", context.serialize(value.getStripeKey()));
            jsonObj.add("analytics", context.serialize(value.getAnalytics()));
            jsonObj.add("password", context.serialize(value.getPassword()));
            jsonObj.add("current_password", context.serialize(value.getCurrentPassword()));
            jsonObj.add(
                    "password_confirmation",
                    context.serialize(value.getPasswordConfirmation())
            );
            jsonObj.add("booking_to_rate_id", context.serialize(value.getBookingRateId()));
            jsonObj.add("booking_to_rate_pro_name", context.serialize(value.getBookingRatePro()));
            jsonObj.add(
                    "schedule_laundry_booking_id",
                    context.serialize(value.getLaundryBookingId())
            );
            jsonObj.add(
                    "add_laundry_booking_id",
                    context.serialize(value.getAddLaundryBookingId())
            );

            return jsonObj;
        }
    }


    public static final class Address {

        @SerializedName("zipcode")
        private String mZip;
        @SerializedName("address1")
        private String mAddress1;
        @SerializedName("address2")
        private String mAddress2;

        public final String getZip() {
            return mZip;
        }

        public final String getAddress1() {
            return mAddress1;
        }

        public final String getAddress2() {
            return mAddress2;
        }
    }


    public static class CreditCard {

        @SerializedName("last4")
        private String mLast4;
        @SerializedName("brand")
        private String mBrand;

        public String getLast4() {
            return mLast4;
        }

        public String getBrand() {
            return mBrand;
        }
    }


    /**
     * If this class marks as final, then we won't be able to mock this for unit testing
     */
    public static class Analytics {

        @SerializedName("last_booking_end")
        private Date mLastBookingEnd;
        @SerializedName("partner")
        private String mPartner;
        @SerializedName("bookings")
        private int mBookings;
        @SerializedName("total_bookings_count")
        private int mTotalBookings;
        @SerializedName("past_bookings_count")
        private int mPastBookings;
        @SerializedName("upcoming_bookings_count")
        private int mUpcomingBookings;
        @SerializedName("recurring_bookings_count")
        private int mRecurringBookings;
        @SerializedName("provider")
        private boolean mIsProvider;
        @SerializedName("vip")
        private boolean mIsVip;
        @SerializedName("facebook_login")
        private boolean mIsFacebookLogin;

        public final Date getLastBookingEnd() {
            return mLastBookingEnd;
        }

        public final String getPartner() {
            return mPartner;
        }

        public final int getBookings() {
            return mBookings;
        }

        public final int getTotalBookings() {
            return mTotalBookings;
        }

        public void setTotalBookings(int totalBookings) {
            mTotalBookings = totalBookings;
        }

        public final int getPastBookings() {
            return mPastBookings;
        }

        public int getUpcomingBookings() {
            return mUpcomingBookings;
        }

        public final int getRecurringBookings() {
            return mRecurringBookings;
        }

        public final boolean isProvider() {
            return mIsProvider;
        }

        public final boolean isVip() {
            return mIsVip;
        }

        public final boolean isFacebookLogin() {
            return mIsFacebookLogin;
        }
    }


    public static class CategorizedCredits implements Serializable {

        @SerializedName("general")
        private int mGeneralCredits;
        @SerializedName("subscription")
        private int mSubscriptionCredits;

        public int getGeneralCredits() {
            return mGeneralCredits;
        }

        public int getSubscriptionCredits() {
            return mSubscriptionCredits;
        }
    }
}
