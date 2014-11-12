package com.handybook.handybook;

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
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public final class BookingQuote extends Observable {
    @SerializedName("id") private int bookingId;
    @SerializedName("hrs") private float hours;
    @SerializedName("date_start") private Date startDate;
    @SerializedName("currency_char") private String currencyChar;
    @SerializedName("currency_suffix") private String currencySuffix;
    @SerializedName("price_table") private ArrayList<PriceInfo> priceTable;

    private HashMap<Float, PriceInfo> priceTableMap;

    final int getBookingId() {
        return bookingId;
    }

    final void setBookingId(final int bookingId) {
        this.bookingId = bookingId;
        triggerObservers();
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
        triggerObservers();
    }

    final Date getStartDate() {
        return startDate;
    }

    final void setStartDate(final Date startDate) {
        this.startDate = startDate;
        triggerObservers();
    }

    final String getCurrencyChar() {
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

    final ArrayList<PriceInfo> getPriceTable() {
        return priceTable;
    }

    final void setPriceTable(final ArrayList<PriceInfo> priceTable) {
        this.priceTable = priceTable;
        buildPriceMap();
    }

    public HashMap<Float, PriceInfo> getPriceTableMap() {
        if (priceTableMap == null || priceTable.isEmpty()) buildPriceMap();
        return priceTableMap;
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    private void buildPriceMap() {
        priceTableMap = new HashMap<>();
        for (final PriceInfo info : this.priceTable) priceTableMap.put(info.getHours(), info);
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingQuote.class, new BookingQuoteSerializer()).create();

        return gson.toJson(this);
    }

    static BookingQuote fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingQuote.class);
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

    static final class BookingQuoteSerializer implements JsonSerializer<BookingQuote> {
        @Override
        public final JsonElement serialize(final BookingQuote value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getBookingId()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("date_start", context.serialize(value.getStartDate()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            jsonObj.add("price_table", context.serialize(value.getPriceTable()));
            return jsonObj;
        }
    }

    static final class PriceInfo {
        @SerializedName("hours") private float hours;
        @SerializedName("price") private float price;

        final float getHours() {
            return hours;
        }

        final float getPrice() {
            return price;
        }
    }
}
