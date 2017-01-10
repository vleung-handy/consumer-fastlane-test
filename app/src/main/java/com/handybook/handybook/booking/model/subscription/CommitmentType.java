package com.handybook.handybook.booking.model.subscription;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.GsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In the new pricing / subscription model, there can only be two commitment types :
 * {no_commitment, months}
 */
public class CommitmentType implements Serializable
{
    @SerializedName("no_commitment")
    private JsonObject mNoCommitment;

    @SerializedName("months")
    private JsonObject mMonths;

    /**
     * In the form of <LengthKey, <FrequencyKey, SubscriptionPrice>>
     */
    private Map<String, Map<String, SubscriptionPrices>> mSubscriptionPrices;

    /**
     * For now, we don't have a way to enforce a certain type of ordering
     */
    private List<SubscriptionFrequency> mUniqueFrequencies;

    /**
     * For now, we don't have a way to enforce a certain type of ordering
     */
    private List<SubscriptionLength> mUniqueLengths;

    /**
     * Apply a massive transformation of the commitments into something more understandable and
     * readable.
     *
     * This method will:
     * --generate a unique list of Frequencies
     * --generate a unique list of lengths
     * --generate a map of subscription prices based on the length & frequency combination that
     * the user selects.
     *
     */
    public void transform()
    {
        mSubscriptionPrices = new HashMap<>();
        mUniqueFrequencies = new ArrayList<>();
        mUniqueLengths = new ArrayList<>();

        //TODO: JIA: For now, only use mMonths if mNoCommitment is null;
        try
        {
            if (mNoCommitment != null)
            {
                processLengths(mNoCommitment);
            }
            else if (mMonths != null)
            {
                processLengths(mMonths);
            }
        }
        catch (Exception e)
        {
            //This shouldn't happen, but if it does, just log it
            Crashlytics.logException(new RuntimeException(e.getMessage()));
        }
    }

    /**
     * Process the lengths. This is at the very top level. The prices are at the lowest level
     *
     * @param data
     */
    private void processLengths(JsonObject data) throws Exception
    {
        if (data == null) {
            return;
        }

        //The first level has keys to the subscription Lengths.
        for (final Map.Entry<String, JsonElement> entrySet : data.entrySet())
        {
             String lengthKey = entrySet.getKey();       //in the form of {"0", "3", "6", "9"}
            JsonObject lengthInformation = (JsonObject) entrySet.getValue();

            //if we don't already have this length stored, then store it.
            if (!contains(mUniqueLengths, lengthKey)) {
                SubscriptionLength length = new SubscriptionLength(
                        lengthKey,
                        GsonUtil.safeGetAsString(lengthInformation.get("title"))
                );
                mUniqueLengths.add(length);
            }

            //digging deeper in the length information, we'll find frequency
            JsonObject frequencyData = (JsonObject) lengthInformation.get("frequency");
            processFrequencies(frequencyData, lengthKey);

        }
    }


    /**
     * Process the frequencies. The prices are contained within this
     * @param data
     * @param lengthKey the length this frequency belongs to
     */
    private void processFrequencies(JsonObject data, String lengthKey) throws Exception
    {
        if (data == null) {
            return;
        }

        for (final Map.Entry<String, JsonElement> entrySet : data.entrySet())
        {
            //in the form of {"price", "weekly_recurring_price", "monthly_recurring_price", etc.}
            String freqKey = entrySet.getKey();
            JsonObject freqInformation = (JsonObject) entrySet.getValue();

            //if we don't already have this frequency, then add it;
            if (!contains(mUniqueFrequencies, freqKey)) {
                SubscriptionFrequency frequency = new SubscriptionFrequency(freqKey, GsonUtil.safeGetAsString(freqInformation.get("title")));
                mUniqueFrequencies.add(frequency);
            }

            //within freqInformation, there is the prices ("hours") table, in the form of
            //{"3", "3.5", "4.0"}
            JsonObject hours = (JsonObject) freqInformation.get("hours");
            boolean isEnabled = !GsonUtil.safeGetAsBoolean(freqInformation.get("disabled"));
            boolean isDefault = GsonUtil.safeGetAsBoolean(freqInformation.get("default"));

            processHours(lengthKey, freqKey, hours, isDefault, isEnabled);
        }
    }

    /**
     * Every set of "hours" receive here is unique to a length & frequency combination.
     * @param lengthKey
     * @param frequencyKey
     * @param data
     */
    private void processHours(
            String lengthKey,
            String frequencyKey,
            JsonObject data,
            boolean isDefault,
            boolean isEnabled
    ) throws Exception
    {

        //if this length entry doesn't exist yet, create the length entry
        if (!mSubscriptionPrices.containsKey(lengthKey)) {
            mSubscriptionPrices.put(lengthKey, new HashMap<String, SubscriptionPrices>());
        }

        Map<String, SubscriptionPrices> freqPriceMap = mSubscriptionPrices.get(lengthKey);

        //the freqPriceMap is supposed to contain SubscriptionPrices, so we extract that
        //information out of data and build it here.
        if (!freqPriceMap.containsKey(frequencyKey)) {


            //iterates through the "hours" data. "Hours" is also going to be the key
            Map<String, Price> prices = new HashMap<>();

            for (final Map.Entry<String, JsonElement> entrySet : data.entrySet())
            {
                String hourKey = entrySet.getKey(); //in the form of {"3.0", "3.5", "4.0", etc.}
                JsonObject priceData = (JsonObject) entrySet.getValue();
                if (priceData != null) {
                    prices.put(hourKey, new Price(priceData.get("full_price").getAsInt(), priceData.get("amount_due").getAsInt()));
                } else {
                    prices.put(hourKey, null);
                }
            }

            SubscriptionPrices subscriptionPrices = new SubscriptionPrices(
                    lengthKey,
                    frequencyKey,
                    isDefault,
                    isEnabled,
                    prices
            );

            freqPriceMap.put(frequencyKey, subscriptionPrices);
        }
    }


    private boolean contains(List<? extends SubscriptionType> subscriptionTypes, String key) {
        if (subscriptionTypes != null) {
            for (SubscriptionType sub : subscriptionTypes) {
                if (sub.getKey().equals(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setNoCommitment(final JsonObject noCommitment)
    {
        mNoCommitment = noCommitment;
    }

    public void setMonths(final JsonObject months)
    {
        mMonths = months;
    }

    public List<SubscriptionFrequency> getUniqueFrequencies()
    {
        return mUniqueFrequencies;
    }

    public List<SubscriptionLength> getUniqueLengths()
    {
        return mUniqueLengths;
    }

    public Map<String, Map<String, SubscriptionPrices>> getSubscriptionPrices()
    {
        return mSubscriptionPrices;
    }
}
