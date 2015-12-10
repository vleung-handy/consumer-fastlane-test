package com.handybook.handybook.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class User extends Observable {
    @SerializedName("auth_token")
    private String authToken;
    @SerializedName("id")
    private String id;
    @SerializedName("credits")
    private float credits;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone_country_prefix")
    private String phonePrefix;
    @SerializedName("phone")
    private String phone;
    @SerializedName("currency_char")
    private String currencyChar;
    @SerializedName("currency_suffix")
    private String currencySuffix;
    @SerializedName("password")
    private String password;
    @SerializedName("current_password")
    private String currentPassword;
    @SerializedName("password_confirmation")
    private String passwordConfirmation;
    @SerializedName("first_address")
    private Address address;
    @SerializedName("card_info")
    private CreditCard creditCard;
    @SerializedName("analytics")
    private Analytics analytics;
    @SerializedName("booking_to_rate_id")
    private int bookingRateId;
    @SerializedName("booking_to_rate_pro_name")
    private String bookingRatePro;
    @SerializedName("schedule_laundry_booking_id")
    private int laundryBookingId;
    @SerializedName("add_laundry_booking_id")
    private int addLaundryBookingId;
    @SerializedName("default_tip_amounts")
    private ArrayList<LocalizedMonetaryAmount> defaultTipAmounts;
    @SerializedName("enable_recurring_cancellations")
    private boolean mRecurringCancellationsEnabled;
    @SerializedName("enable_recurring_cancellations_email_flow")
    private boolean mRecurringCancellationsEmailFlowEnabled;

    public boolean isRecurringCancellationsEnabled()
    {
        return mRecurringCancellationsEnabled;
    }

    public boolean isRecurringCancellationsEmailFlowEnabled()
    {
        return mRecurringCancellationsEmailFlowEnabled;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
        triggerObservers();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
        triggerObservers();
    }

    public float getCredits() {
        return credits;
    }

    final void setCredits(final float credits) {
        this.credits = credits;
        triggerObservers();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
        triggerObservers();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
        triggerObservers();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
        triggerObservers();
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    final void setPhonePrefix(final String phonePrefix) {
        this.phonePrefix = phonePrefix;
        triggerObservers();
    }

    public String getPhone() {
        final int phoneLen = phone != null ? phone.length() : 0;
        if (phone != null & phoneLen > 10) return phone.substring(phoneLen - 10);
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
        triggerObservers();
    }

    public String getCurrencyChar() {
        return currencyChar;
    }

    final void setCurrencyChar(final String currencyChar) {
        this.currencyChar = currencyChar;
        triggerObservers();
    }

    final String getCurrencySuffix() {
        return currencySuffix;
    }

    final void setCurrencySuffix(final String currencySuffix) {
        this.currencySuffix = currencySuffix;
        triggerObservers();
    }

    final String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    final String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(final String currentPassword) {
        this.currentPassword = currentPassword;
    }

    final String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(final String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public Address getAddress() {
        return address;
    }

    final void setAddress(final Address address) {
        this.address = address;
        triggerObservers();
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    final void setCreditCard(final CreditCard creditCard) {
        this.creditCard = creditCard;
        triggerObservers();
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    final void setAnalytics(final Analytics analytics) {
        this.analytics = analytics;
        triggerObservers();
    }

    public int getBookingRateId() {
        return bookingRateId;
    }

    public String getBookingRatePro() {
        return bookingRatePro;
    }

    public ArrayList<LocalizedMonetaryAmount> getDefaultTipAmounts() {
        return defaultTipAmounts;
    }

    public int getLaundryBookingId() {
        return laundryBookingId;
    }

    public int getAddLaundryBookingId() {
        return addLaundryBookingId;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(User.class, new UserSerializer()).create();

        return gson.toJson(this);
    }

    public static User fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, User.class);
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

    static final class UserSerializer implements JsonSerializer<User> {
        @Override
        public final JsonElement serialize(final User value, final Type type,
                                     final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("id", context.serialize(value.getId()));
            jsonObj.add("credits", context.serialize(value.getCredits()));
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("phone_country_prefix", context.serialize(value.getPhonePrefix()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            jsonObj.add("first_address", context.serialize(value.getAddress()));
            jsonObj.add("card_info", context.serialize(value.getCreditCard()));
            jsonObj.add("analytics", context.serialize(value.getAnalytics()));
            jsonObj.add("password", context.serialize(value.getPassword()));
            jsonObj.add("current_password", context.serialize(value.getCurrentPassword()));
            jsonObj.add("password_confirmation", context.serialize(value.getPasswordConfirmation()));
            jsonObj.add("booking_to_rate_id", context.serialize(value.getBookingRateId()));
            jsonObj.add("booking_to_rate_pro_name", context.serialize(value.getBookingRatePro()));
            jsonObj.add("schedule_laundry_booking_id", context.serialize(value.getLaundryBookingId()));
            jsonObj.add("add_laundry_booking_id", context.serialize(value.getAddLaundryBookingId()));

            return jsonObj;
        }
    }

    public static final class Address {
        @SerializedName("zipcode") private String zip;
        @SerializedName("address1") private String address1;
        @SerializedName("address2") private String address2;

        public final String getZip() {
            return zip;
        }

        public final String getAddress1() {
            return address1;
        }

        public final String getAddress2() {
            return address2;
        }
    }

    public static class CreditCard {
        @SerializedName("last4") private String last4;
        @SerializedName("brand") private String brand;

        public String getLast4() {
            return last4;
        }

        public String getBrand() {
            return brand;
        }
    }

    public static final class Analytics {
        @SerializedName("last_booking_end") private Date lastBookingEnd;
        @SerializedName("partner") private String partner;
        @SerializedName("bookings") private int bookings;
        @SerializedName("total_bookings_count") private int totalBookings;
        @SerializedName("past_bookings_count") private int pastBookings;
        @SerializedName("upcoming_bookings_count") private int upcomingBookings;
        @SerializedName("recurring_bookings_count") private int recurringBookings;
        @SerializedName("provider") private boolean isProvider;
        @SerializedName("vip") private boolean isVip;
        @SerializedName("facebook_login") private boolean isFacebookLogin;

        public final Date getLastBookingEnd() {
            return lastBookingEnd;
        }

        public final String getPartner() {
            return partner;
        }

        public final int getBookings() {
            return bookings;
        }

        public final int getTotalBookings() {
            return totalBookings;
        }

        public final int getPastBookings() {
            return pastBookings;
        }

        public final int getUpcomingBookings() {
            return upcomingBookings;
        }

        public final int getRecurringBookings() {
            return recurringBookings;
        }

        public final boolean isProvider() {
            return isProvider;
        }

        public final boolean isVip() {
            return isVip;
        }

        public final boolean isFacebookLogin() {
            return isFacebookLogin;
        }
    }
}
