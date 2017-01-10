package com.handybook.handybook.booking.model;

import android.support.annotation.Nullable;

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
import com.handybook.handybook.booking.model.subscription.CommitmentType;
import com.handybook.handybook.booking.model.subscription.Price;
import com.handybook.handybook.core.model.bill.Bill;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BookingQuote extends Observable
{
    public static final String KEY_ID = "id";
    public static final String KEY_SERVICE_ID = "service_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_HRS = "hrs";
    public static final String KEY_DATE_START = "date_start";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CURRENCY_CHAR = "currency_char";
    public static final String KEY_CURRENCY_SUFFIX = "currency_suffix";
    public static final String KEY_HOURLY_AMOUNT = "hourly_amount";
    public static final String KEY_PRICE_TABLE = "price_table";
    public static final String KEY_DYNAMIC_OPTIONS = "dynamic_options";
    public static final String KEY_STRIPE_KEY = "stripe_key";
    public static final String KEY_PHONE_COUNTRY_PREFIX = "phone_country_prefix";
    public static final String KEY_SPECIAL_EXTRAS_OPTIONS = "special_extras_options";
    public static final String KEY_IS_ANDROID_PAY_ENABLED = "is_android_pay_enabled";
    public static final String KEY_ANDROID_PAY_COUPON = "android_pay_coupon";
    public static final String KEY_ANDROID_PAY_COUPON_VALUE_FORMATTED = "android_pay_coupon_value_formatted";
    public static final String KEY_COUPON = "coupon";
    public static final String KEY_RECURRENCE_OPTIONS = "recurrence_options";
    public static final String KEY_QUOTE_CONFIG = "quote_config";
    public static final String KEY_BILL = "bill";
    public static final String KEY_COMMITMENT_PRICES = "commitment_prices";
    public static final int ONCE = 0;
    public static final int WEEKLY_PRICE = 1;
    public static final int BI_WEEKLY_PRICE = 2;
    public static final int MONTHLY_PRICE = 4;

    @SerializedName(KEY_ID)
    private int mBookingId;
    @SerializedName(KEY_SERVICE_ID)
    private int mServiceId;
    @SerializedName(KEY_USER_ID)
    private String mUserId;
    @SerializedName(KEY_HRS)
    private float mHours;
    @SerializedName(KEY_DATE_START)
    private Date mStartDate;
    @SerializedName(KEY_ADDRESS)
    private Address mAddress;
    @SerializedName(KEY_CURRENCY_CHAR)
    private String mCurrencyChar;
    @SerializedName(KEY_CURRENCY_SUFFIX)
    private String mCurrencySuffix;
    @SerializedName(KEY_HOURLY_AMOUNT)
    private float mHourlyAmount;

    @SerializedName(KEY_PRICE_TABLE)
    private ArrayList<BookingPriceInfo> mPriceTable;

    /**
     * This is just a generic json holder at the moment. If there are months returned from the
     * commitment prices, then we'll use {@link CommitmentType}, else we'll use the {@link CommitmentPricesMap}
     * fallback. If mCommitmentPrices is null, we'll fallback to the old prices table.
     *
     */
    @SerializedName(KEY_COMMITMENT_PRICES)
    private JsonObject mCommitmentPrices;
    private CommitmentType mCommitmentType;
    private CommitmentPricesMap mCommitmentPricesMap;

    @SerializedName(KEY_DYNAMIC_OPTIONS)
    private ArrayList<PeakPriceInfo> mSurgePriceTable;
    @SerializedName(KEY_STRIPE_KEY)
    private String mStripeKey;
    @SerializedName(KEY_PHONE_COUNTRY_PREFIX)
    private String mPhonePrefix;
    @SerializedName(KEY_SPECIAL_EXTRAS_OPTIONS)
    private BookingOption mBookingOption;
    @SerializedName(KEY_IS_ANDROID_PAY_ENABLED)
    private boolean mIsAndroidPayEnabled;
    @SerializedName(KEY_ANDROID_PAY_COUPON)
    private String mAndroidPayCouponCode;
    @SerializedName(KEY_ANDROID_PAY_COUPON_VALUE_FORMATTED)
    private String mAndroidPayCouponValueFormatted;
    @SerializedName(KEY_COUPON)
    private QuoteCoupon mCoupon;
    @SerializedName(KEY_RECURRENCE_OPTIONS)
    private int[] mRecurrenceOptions;
    @SerializedName(KEY_QUOTE_CONFIG)
    private QuoteConfig mQuoteConfig;
    @SerializedName(KEY_BILL)
    private Bill mBill;

    private HashMap<Float, BookingPriceInfo> mPriceTableMap;
    private ArrayList<ArrayList<PeakPriceInfo>> mPeakPriceTable;


    public static class QuoteConfig implements Serializable
    {
        @SerializedName("disclaimer_text")
        private String mDisclaimerText;
        @SerializedName("recurrence_options")
        private List<RecurrenceOption> mRecurrenceOptions;

        public String getDisclaimerText()
        {
            return mDisclaimerText;
        }

        public List<RecurrenceOption> getRecurrenceOptions()
        {
            return mRecurrenceOptions;
        }
    }

    /**
     * Now replaced by using the price in {@link CommitmentType}
     */
    @Deprecated()
    public QuoteConfig getQuoteConfig()
    {
        return mQuoteConfig;
    }

    /**
     * each item returned in this array should be a constant in BookingRecurrence
     *
     * @return
     */
    public int[] getRecurrenceOptions()
    {
        return mRecurrenceOptions;
    }

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

    public void setCoupon(final QuoteCoupon coupon)
    {
        mCoupon = coupon;
    }

    public QuoteCoupon getCoupon()
    {
        return mCoupon;
    }

    public int getBookingId()
    {
        return mBookingId;
    }

    void setBookingId(final int bookingId)
    {
        mBookingId = bookingId;
        triggerObservers();
    }

    public int getServiceId()
    {
        return mServiceId;
    }

    void setServiceId(final int serviceId)
    {
        mServiceId = serviceId;
        triggerObservers();
    }

    public String getUserId()
    {
        return mUserId;
    }

    void setUserId(final String userId)
    {
        mUserId = userId;
        triggerObservers();
    }

    public float getHours()
    {
        return mHours;
    }

    public void setHours(float hours)
    {
        mHours = hours;
        triggerObservers();
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public void setStartDate(final Date startDate)
    {
        mStartDate = startDate;
        triggerObservers();
    }

    public Address getAddress()
    {
        return mAddress;
    }

    void setAddress(final Address address)
    {
        mAddress = address;
        triggerObservers();
    }

    public String getCurrencyChar()
    {
        return mCurrencyChar;
    }

    void setCurrencyChar(final String currencyChar)
    {
        mCurrencyChar = currencyChar;
        triggerObservers();
    }

    public String getCurrencySuffix()
    {
        return mCurrencySuffix;
    }

    void setCurrencySuffix(final String currencySuffix)
    {
        mCurrencySuffix = currencySuffix;
        triggerObservers();
    }

    public float getHourlyAmount()
    {
        return mHourlyAmount;
    }

    void setHourlyAmount(final float hourlyAmount)
    {
        mHourlyAmount = hourlyAmount;
    }

    /**
     * Now replaced by the prices in {@link CommitmentType}
     */
    @Deprecated
    public ArrayList<BookingPriceInfo> getPriceTable()
    {
        return mPriceTable;
    }

    public void setPriceTable(final ArrayList<BookingPriceInfo> priceTable)
    {
        mPriceTable = priceTable;
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
        mSurgePriceTable = surgePriceTable;
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
        final BookingPriceInfo info = mPriceTable.get(0);
        return !(info.getBiMonthlyprice() <= 0 && info.getMonthlyPrice() <= 0
                && info.getWeeklyPrice() <= 0);
    }

    /**
     * Returns the price for the selected length & frequency option.
     * Full price at index 0, and discounted/amount due at index 1;
     *
     * This is for use in the new commitment model. Will default back to the old pricing model if
     * there is no {@link CommitmentType} present.
     * @return
     */
    @Nullable
    public float[] getPricing(final float hours, final int freq, final int lengths)
    {
        if (getCommitmentType() != null)
        {
            //this means to use the new commitment model
            Price price = getCommitmentType()
                    .getPrice(
                            String.valueOf(lengths),
                            String.valueOf(freq),
                            String.valueOf(hours)
                    );

            return new float[]{price.getFullPrice(), price.getAmountDue()};
        }
        else
        {
            return getPricing(hours, freq);
        }
    }

    @Nullable
    public float[] getPricing(final float hours, final int freq)
    {
        final BookingPriceInfo info = getPriceTableMap().get(hours);
        if (info == null)
        {
            try
            {
                Crashlytics.log(toJson());
            }
            catch (Exception e)
            {
                Crashlytics.log("BookingQuote toJSON failed.");
            }
            Crashlytics.logException(new NullPointerException("BookingPriceInfo is null"));
            return new float[]{};
        }

        if (info == null)
        {
            return null;
        }
        switch (freq)
        {
            case WEEKLY_PRICE:
                return new float[]{info.getWeeklyPrice(), info.getDiscountWeeklyPrice()};
            case BI_WEEKLY_PRICE:
                return new float[]{info.getBiMonthlyprice(), info.getDiscountBiMonthlyprice()};
            case MONTHLY_PRICE:
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
        mPhonePrefix = phonePrefix;
    }

    public String getStripeKey()
    {
        return mStripeKey;
    }

    void setStripeKey(final String stripeKey)
    {
        mStripeKey = stripeKey;
        triggerObservers();
    }

    public BookingOption getBookingOption()
    {
        return mBookingOption;
    }

    public void setBookingOption(final BookingOption bookingOption)
    {
        mBookingOption = bookingOption;
        triggerObservers();
    }

    @Nullable
    public Bill getBill()
    {
        return mBill;
    }

    public CommitmentType getCommitmentType()
    {
        return mCommitmentType;
    }

    public CommitmentPricesMap getCommitmentPricesMap()
    {
        return mCommitmentPricesMap;
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    private void buildPriceMap()
    {
        mPriceTableMap = new HashMap<>();

        if (mPriceTable == null) { return; }

        for (final BookingPriceInfo info : mPriceTable)
        { mPriceTableMap.put(info.getHours(), info); }
    }

    private void buildPeakPriceTable()
    {
        if (mSurgePriceTable == null) { return; }

        final HashMap<Date, ArrayList<PeakPriceInfo>> peakPriceMap = new HashMap<>();

        for (final PeakPriceInfo info : mSurgePriceTable)
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
                                           .registerTypeAdapter(
                                                   BookingQuote.class,
                                                   new BookingQuoteSerializer()
                                           ).create();

        return gson.toJson(this);
    }

    public JsonObject getCommitmentPrices()
    {
        return mCommitmentPrices;
    }

    public static BookingQuote fromJson(final String json)
    {
        final BookingQuote bookingQuote = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                                                           .create()
                                                           .fromJson(json, BookingQuote.class);

        if (bookingQuote != null && bookingQuote.getCommitmentPrices() != null)
        {

            //TODO: JIA: check for the flag that tells you what commitment to use.
            // If none, fall back is to use no_commitment

            if (bookingQuote.getCommitmentPrices().has("months"))
            {
                bookingQuote.setCommitmentType(new Gson().fromJson(
                        bookingQuote.getCommitmentPrices(),
                        CommitmentType.class
                ));
                bookingQuote.getCommitmentType().transform();
            }
            else
            {
                bookingQuote.setCommitmentPricesMap(new Gson().fromJson(
                        bookingQuote.getCommitmentPrices(),
                        CommitmentPricesMap.class
                ));
                bookingQuote.mPriceTable = bookingQuote.getCommitmentPricesMap().toPriceTable();
            }
        }

        return bookingQuote;
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

    public void setCommitmentPricesMap(final CommitmentPricesMap commitmentPricesMap)
    {
        mCommitmentPricesMap = commitmentPricesMap;
    }

    public void setCommitmentType(final CommitmentType commitmentType)
    {
        mCommitmentType = commitmentType;
    }

    public boolean hasCouponWarning()
    {
        return getCoupon() != null && getCoupon().getWarning() != null;
    }

    public static class BookingQuoteSerializer implements JsonSerializer<BookingQuote>
    {
        @Override
        public JsonElement serialize(
                final BookingQuote value, final Type type,
                final JsonSerializationContext context
        )
        {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add(KEY_ID, context.serialize(value.getBookingId()));
            jsonObj.add(KEY_SERVICE_ID, context.serialize(value.getServiceId()));
            jsonObj.add(KEY_USER_ID, context.serialize(value.getUserId()));
            jsonObj.add(KEY_HRS, context.serialize(value.getHours()));
            jsonObj.add(KEY_DATE_START, context.serialize(value.getStartDate()));
            jsonObj.add(KEY_ADDRESS, context.serialize(value.getAddress()));
            jsonObj.add(KEY_CURRENCY_CHAR, context.serialize(value.getCurrencyChar()));
            jsonObj.add(KEY_CURRENCY_SUFFIX, context.serialize(value.getCurrencySuffix()));
            jsonObj.add(KEY_PHONE_COUNTRY_PREFIX, context.serialize(value.getPhonePrefix()));
            jsonObj.add(KEY_HOURLY_AMOUNT, context.serialize(value.getHourlyAmount()));
            jsonObj.add(KEY_PRICE_TABLE, context.serialize(value.getPriceTable()));
            jsonObj.add(KEY_DYNAMIC_OPTIONS, context.serialize(value.getSurgePriceTable()));
            jsonObj.add(KEY_STRIPE_KEY, context.serialize(value.getStripeKey()));
            jsonObj.add(KEY_SPECIAL_EXTRAS_OPTIONS, context.serialize(value.getBookingOption()));
            jsonObj.add(KEY_IS_ANDROID_PAY_ENABLED, context.serialize(value.isAndroidPayEnabled()));
            jsonObj.add(KEY_ANDROID_PAY_COUPON, context.serialize(value.getAndroidPayCouponCode()));
            jsonObj.add(
                    KEY_ANDROID_PAY_COUPON_VALUE_FORMATTED,
                    context.serialize(value.getAndroidPayCouponValueFormatted())
            );
            jsonObj.add(KEY_COUPON, context.serialize(value.getCoupon()));
            jsonObj.add(KEY_RECURRENCE_OPTIONS, context.serialize(value.getRecurrenceOptions()));
            jsonObj.add(KEY_QUOTE_CONFIG, context.serialize(value.getQuoteConfig()));
            jsonObj.add(KEY_BILL, context.serialize(value.getBill()));
            jsonObj.add(KEY_COMMITMENT_PRICES, context.serialize(value.getCommitmentType()));
            return jsonObj;
        }
    }


    public static class Address implements Serializable
    {
        @SerializedName("zipcode")
        private String zip;

        public String getZip()
        {
            return zip;
        }
    }


    public static class QuoteCoupon implements Serializable
    {

        @SerializedName("code")
        private String mCode;
        @SerializedName("warning")
        private String mWarning;

        public String getCode()
        {
            return mCode;
        }

        public String getWarning()
        {
            return mWarning;
        }
    }
}
