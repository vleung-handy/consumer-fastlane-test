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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class BookingQuote extends Observable {
    @SerializedName("id")
    private int mBookingId;
    @SerializedName("service_id")
    private int mServiceId;
    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("hrs")
    private float mHours;
    @SerializedName("date_start")
    private Date mStartDate;
    @SerializedName("address")
    private Address mAddress;
    @SerializedName("currency_char")
    private String mCurrencyChar;
    @SerializedName("currency_suffix")
    private String mCurrencySuffix;
    @SerializedName("hourly_amount")
    private float mHourlyAmount;
    @SerializedName("price_table")
    private ArrayList<BookingPriceInfo> mPriceTable;
    @SerializedName("dynamic_options")
    private ArrayList<PeakPriceInfo> mSurgePriceTable;
    @SerializedName("stripe_key")
    private String mStripeKey;
    @SerializedName("phone_country_prefix")
    private String mPhonePrefix;
    @SerializedName("special_extras_options")
    private BookingOption mBookingOption;
    @SerializedName("is_android_pay_enabled") private boolean mIsAndroidPayEnabled;
    @SerializedName("android_pay_coupon") private String mAndroidPayCouponCode;
    @SerializedName("android_pay_coupon_value_formatted") private String mAndroidPayCouponValueFormatted;

    private HashMap<Float, BookingPriceInfo> mPriceTableMap;
    private ArrayList<ArrayList<PeakPriceInfo>> mPeakPriceTable;

    public String getAndroidPayCouponValueFormatted()
    {
        return mAndroidPayCouponValueFormatted;
    }

    public boolean isAndroidPayEnabled()
    {
        return mIsAndroidPayEnabled;
    }

    public String getAndroidPayCouponCode()
    {
        return mAndroidPayCouponCode;
    }

    public int getBookingId()
    {
        return mBookingId;
    }

    void setBookingId(final int bookingId)
    {
        this.mBookingId = bookingId;
        triggerObservers();
    }

    public int getServiceId()
    {
        return mServiceId;
    }

    void setServiceId(final int serviceId)
    {
        this.mServiceId = serviceId;
        triggerObservers();
    }

    public String getUserId()
    {
        return mUserId;
    }

    void setUserId(final String userId)
    {
        this.mUserId = userId;
        triggerObservers();
    }

    public float getHours()
    {
        return mHours;
    }

    public void setHours(float hours)
    {
        this.mHours = hours;
        triggerObservers();
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public void setStartDate(final Date startDate)
    {
        this.mStartDate = startDate;
        triggerObservers();
    }

    public Address getAddress()
    {
        return mAddress;
    }

    void setAddress(final Address address)
    {
        this.mAddress = address;
        triggerObservers();
    }

    public String getCurrencyChar()
    {
        return mCurrencyChar;
    }

    void setCurrencyChar(final String currencyChar)
    {
        this.mCurrencyChar = currencyChar;
        triggerObservers();
    }

    public String getCurrencySuffix()
    {
        return mCurrencySuffix;
    }

    void setCurrencySuffix(final String currencySuffix)
    {
        this.mCurrencySuffix = currencySuffix;
        triggerObservers();
    }

    public float getHourlyAmount()
    {
        return mHourlyAmount;
    }

    void setHourlyAmount(final float hourlyAmount)
    {
        this.mHourlyAmount = hourlyAmount;
    }

    public ArrayList<BookingPriceInfo> getPriceTable()
    {
        return mPriceTable;
    }

    public void setPriceTable(final ArrayList<BookingPriceInfo> priceTable)
    {
        this.mPriceTable = priceTable;
        buildPriceMap();
        triggerObservers();
    }

    HashMap<Float, BookingPriceInfo> getPriceTableMap()
    {
        if (mPriceTableMap == null || mPriceTable.isEmpty()) { buildPriceMap(); }
        return mPriceTableMap;
    }

    public ArrayList<PeakPriceInfo> getSurgePriceTable()
    {
        return mSurgePriceTable;
    }

    public void setSurgePriceTable(final ArrayList<PeakPriceInfo> surgePriceTable)
    {
        this.mSurgePriceTable = surgePriceTable;
        buildPeakPriceTable();
        triggerObservers();
    }

    public ArrayList<ArrayList<PeakPriceInfo>> getPeakPriceTable()
    {
        if (mPeakPriceTable == null || mPeakPriceTable.isEmpty())
        {
            buildPeakPriceTable();
        }
        return mPeakPriceTable;
    }

    boolean hasRecurring()
    {
        final BookingPriceInfo info = this.mPriceTable.get(0);
        return !(info.getBiMonthlyprice() <= 0 && info.getMonthlyPrice() <= 0
                && info.getWeeklyPrice() <= 0);
    }

    public float[] getPricing(final float hours, final int freq)
    {
        final BookingPriceInfo info = this.getPriceTableMap().get(hours);

        switch (freq)
        {
            case 1:
                return new float[]{info.getWeeklyPrice(), info.getDiscountWeeklyPrice()};

            case 2:
                return new float[]{info.getBiMonthlyprice(), info.getDiscountBiMonthlyprice()};

            case 4:
                return new float[]{info.getMonthlyPrice(), info.getDiscountMonthlyPrice()};

            default:
                return new float[]{info.getPrice(), info.getDiscountPrice()};
        }
    }

    public String getPhonePrefix()
    {
        return mPhonePrefix;
    }

    void setPhonePrefix(final String phonePrefix)
    {
        this.mPhonePrefix = phonePrefix;
    }

    public String getStripeKey()
    {
        return mStripeKey;
    }

    void setStripeKey(final String stripeKey)
    {
        this.mStripeKey = stripeKey;
        triggerObservers();
    }

    public BookingOption getBookingOption()
    {
        return mBookingOption;
    }

    public void setBookingOption(final BookingOption bookingOption)
    {
        this.mBookingOption = bookingOption;
        triggerObservers();
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    private void buildPriceMap()
    {
        mPriceTableMap = new HashMap<>();

        if (this.mPriceTable == null) { return; }

        for (final BookingPriceInfo info : this.mPriceTable)
        { mPriceTableMap.put(info.getHours(), info); }
    }

    private void buildPeakPriceTable()
    {
        if (this.mSurgePriceTable == null) { return; }

        final HashMap<Date, ArrayList<PeakPriceInfo>> peakPriceMap = new HashMap<>();

        for (final PeakPriceInfo info : this.mSurgePriceTable)
        {
            final Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(info.getDate());

            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
            cal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            final Date date = cal.getTime();
            ArrayList<PeakPriceInfo> priceList;

            if ((priceList = peakPriceMap.get(date)) != null)
            {
                priceList.add(info);
            }
            else
            {
                priceList = new ArrayList<>();
                priceList.add(info);
                peakPriceMap.put(date, priceList);
            }
        }

        final ArrayList<ArrayList<PeakPriceInfo>> table = new ArrayList<>();
        final ArrayList<Date> keys = new ArrayList<>(peakPriceMap.keySet());

        Collections.sort(keys, new Comparator<Date>()
        {
            @Override
            public int compare(final Date lhs, final Date rhs)
            {
                return (int) (lhs.getTime() - rhs.getTime());
            }
        });

        for (final Date d : keys)
        {
            final ArrayList<PeakPriceInfo> list = peakPriceMap.get(d);
            table.add(list);
        }
        mPeakPriceTable = table;
    }

    public String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingQuote.class, new BookingQuoteSerializer()).create();

        return gson.toJson(this);
    }

    public static BookingQuote fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingQuote.class);
    }

    public static ExclusionStrategy getExclusionStrategy()
    {
        return new ExclusionStrategy()
        {
            @Override
            public boolean shouldSkipField(final FieldAttributes f)
            {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz)
            {
                return clazz.equals(Observer.class);
            }
        };
    }

    public static class BookingQuoteSerializer implements JsonSerializer<BookingQuote>
    {
        @Override
        public JsonElement serialize(final BookingQuote value, final Type type,
                                     final JsonSerializationContext context)
        {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getBookingId()));
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("user_id", context.serialize(value.getUserId()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("date_start", context.serialize(value.getStartDate()));
            jsonObj.add("address", context.serialize(value.getAddress()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            jsonObj.add("phone_country_prefix", context.serialize(value.getPhonePrefix()));
            jsonObj.add("hourly_amount", context.serialize(value.getHourlyAmount()));
            jsonObj.add("price_table", context.serialize(value.getPriceTable()));
            jsonObj.add("dynamic_options", context.serialize(value.getSurgePriceTable()));
            jsonObj.add("stripe_key", context.serialize(value.getStripeKey()));
            jsonObj.add("special_extras_options", context.serialize(value.getBookingOption()));
            return jsonObj;
        }
    }


    public static class Address
    {
        @SerializedName("zipcode")
        private String zip;

        public String getZip()
        {
            return zip;
        }
    }
}
