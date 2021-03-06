package com.handybook.handybook.booking.model.subscription;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.GsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In the new pricing / subscription model, there can only be two commitment types :
 * {no_commitment, months}
 */
public class CommitmentType implements Serializable {

    public static final String STRING_NO_COMMITMENT = "no_commitment";
    public static final String STRING_MONTHS = "months";
    public static final String STRING_TRIAL = "trial";

    private static final String JSON_KEY_DEFAULT = "default";
    private static final String JSON_KEY_TITLE = "title";
    private static final String JSON_KEY_COMMITMENT_SUBTITLE = "commitment_subtitle";
    private static final String JSON_KEY_DISABLED = "disabled";
    private static final String JSON_KEY_HOURS = "hours";
    private static final String JSON_KEY_FREQUENCY = "frequency";
    private static final String JSON_KEY_FULL_PRICE = "full_price";
    private static final String JSON_KEY_AMOUNT_DUE = "amount_due";
    private static final String JSON_KEY_TERMS_OF_USE_TYPE = "terms_of_use_type";

    @SerializedName(STRING_NO_COMMITMENT)
    private JsonObject mNoCommitment;

    @SerializedName(STRING_MONTHS)
    private JsonObject mMonths;

    @SerializedName(STRING_TRIAL)
    private JsonObject mTrial;

    /**
     * In the form of <LengthKey, <FrequencyKey, SubscriptionPrice>>. This is so that you can
     * figure out the price table to use given a "length", and a "frequency"
     */
    private Map<String, Map<String, SubscriptionPrices>> mSubscriptionPrices;

    /**
     * A frequency are values that are similar to weekly, biweekly, monthly, etc. to indicate
     * how often to repeat a booking.
     * For now, we don't have a way to enforce a certain type of ordering
     */
    private List<SubscriptionFrequency> mUniqueFrequencies;

    /**
     * A length are values that are similar to 3 month, 6 month, 9 month -- to indicate the length
     * of the subscription (or term).
     * For now, we don't have a way to enforce a certain type of ordering
     */
    private List<SubscriptionLength> mUniqueLengths;

    private CommitmentTypeName mTransformedCommitment;
    private JsonObject mProcessedCommitmentType;

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
    public void transform(CommitmentTypeName commitmentToUse) {
        mSubscriptionPrices = new HashMap<>();
        mUniqueFrequencies = new ArrayList<>();
        mUniqueLengths = new ArrayList<>();
        mTransformedCommitment = commitmentToUse;

        try {
            if (CommitmentTypeName.MONTHS.equals(commitmentToUse) && mMonths != null) {
                processLengths(mMonths);
            }
            else if (CommitmentTypeName.TRIAL.equals(commitmentToUse) && mTrial != null) {
                processLengths(mTrial);
            }
            else if (mNoCommitment != null) {
                //fall back to use
                processLengths(mNoCommitment);
                Crashlytics.logException(new RuntimeException(
                        "We should not be handling no_commitment inside the new CommitmentType model"));
            }
            else {
                Crashlytics.logException(new RuntimeException(
                        "We should not be in the new CommitmentType model with nothing to process"));
            }
        }
        catch (Exception e) {
            //This shouldn't happen, but if it does, just log it
            Crashlytics.logException(new RuntimeException(e.getMessage()));
        }
    }

    /**
     * Process the lengths. This is at the very top level. The prices are at the lowest level
     *
     * @param data
     */
    private void processLengths(JsonObject data) throws Exception {
        mProcessedCommitmentType = data;
        if (data == null) {
            return;
        }

        //The first level has keys to the subscription Lengths.
        for (final Map.Entry<String, JsonElement> entrySet : data.entrySet()) {
            String lengthKey = entrySet.getKey();       //in the form of {"0", "3", "6", "9"}

            JsonObject lengthInformation = (JsonObject) entrySet.getValue();

            //if we don't already have this length stored, then store it.
            if (!contains(mUniqueLengths, lengthKey)) {
                SubscriptionLength length = new SubscriptionLength(
                        lengthKey,
                        GsonUtil.safeGetAsString(lengthInformation.get(JSON_KEY_TITLE)),
                        GsonUtil.safeGetAsBoolean(lengthInformation.get(JSON_KEY_DEFAULT))
                );

                //This is not added to the unique lengths because 0 is not to be displayed
                // for the Plan terms unless trial of course

                switch (mTransformedCommitment) {
                    case TRIAL:
                        if (lengthKey.equals("0")) {
                            mUniqueLengths.add(length);
                        }
                        else {
                            continue;
                        }
                        break;
                    case MONTHS:
                        if (!lengthKey.equals("0")) {
                            mUniqueLengths.add(length);
                        }
                }
            }

            //digging deeper in the length information, we'll find frequency
            JsonObject frequencyData = (JsonObject) lengthInformation.get(JSON_KEY_FREQUENCY);
            processFrequencies(frequencyData, lengthKey);
        }
    }

    /**
     * Process the frequencies. The prices are contained within this
     * @param data
     * @param lengthKey the length this frequency belongs to
     */
    private void processFrequencies(JsonObject data, String lengthKey) throws Exception {
        if (data == null) {
            return;
        }

        for (final Map.Entry<String, JsonElement> entrySet : data.entrySet()) {
            //The key is in the form of {"price", "weekly_recurring_price", "monthly_recurring_price", etc.},
            //but it needs to be converted to {0, 2, 4, etc. }
            String freqKey = SubscriptionFrequency.convertToFrequencyKey(entrySet.getKey());
            JsonObject freqInformation = (JsonObject) entrySet.getValue();

            //within freqInformation, there is the prices ("hours") table, in the form of
            //{"3", "3.5", "4.0"}
            JsonObject hours = (JsonObject) freqInformation.get(JSON_KEY_HOURS);
            boolean isEnabled = !GsonUtil.safeGetAsBoolean(freqInformation.get(JSON_KEY_DISABLED));
            boolean isDefault = GsonUtil.safeGetAsBoolean(freqInformation.get(JSON_KEY_DEFAULT));

            //if it's enabled and we don't already have this frequency, then add it to frequency list
            if (isEnabled && !contains(mUniqueFrequencies, freqKey)) {
                SubscriptionFrequency frequency = new SubscriptionFrequency(
                        freqKey,
                        GsonUtil.safeGetAsString(freqInformation.get(JSON_KEY_TITLE)),
                        GsonUtil.safeGetAsBoolean(freqInformation.get(JSON_KEY_DEFAULT)),
                        GsonUtil.safeGetAsString(freqInformation.get(JSON_KEY_TERMS_OF_USE_TYPE))
                );
                mUniqueFrequencies.add(frequency);
            }

            processHours(lengthKey, freqKey, hours, isDefault, isEnabled);
        }

        //Sort the unique Frequencies by the subscriptionFrequency key
        Collections.sort(mUniqueFrequencies, new Comparator<SubscriptionFrequency>() {
            @Override
            public int compare(
                    final SubscriptionFrequency o1, final SubscriptionFrequency o2
            ) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
    }

    /**
     * Given a lengthKey and a frequencyKey, return the price that is associated with it. Null if not
     * found
     *
     * @param lengthKey
     * @param frequencyKey
     * @return
     */
    @Nullable
    public Price getPrice(String lengthKey, String frequencyKey, String hour) {
        SubscriptionPrices subscriptionPrices = getSubscriptionPrice(lengthKey, frequencyKey);

        if (subscriptionPrices != null && subscriptionPrices.getPrices() != null) {
            return subscriptionPrices.getPrices().get(hour);
        }

        return null;
    }

    @Nullable
    public String getCommitmentSubtitle(
            @NonNull final String lengthKey,
            @NonNull final String frequencyKey
    ) {
        String subtitle = null;
        try {
            subtitle = GsonUtil.safeGetAsString(
                    mProcessedCommitmentType
                            .getAsJsonObject(lengthKey)
                            .getAsJsonObject(JSON_KEY_FREQUENCY)
                            .getAsJsonObject(frequencyKey)
                            .getAsJsonPrimitive(JSON_KEY_COMMITMENT_SUBTITLE)
            );
        }
        catch (Exception e) {
            Crashlytics.log("Failed to retreive subtitle");
            Crashlytics.logException(e);
        }
        return subtitle;
    }

    public SubscriptionPrices getSubscriptionPrice(String lengthKey, String frequencyKey) {
        if (!TextUtils.isEmpty(lengthKey) && !TextUtils.isEmpty(frequencyKey) &&
            mSubscriptionPrices != null) {

            Map<String, SubscriptionPrices> pricesMap = mSubscriptionPrices.get(lengthKey);

            if (pricesMap != null) {
                return pricesMap.get(frequencyKey);
            }
            else {
                //there is something wrong here, prices not found. Invalid length / frequency combination
                Crashlytics.logException(new RuntimeException(
                        String.format(
                                "Subscription price for length: [%s] and frequency: [%s} not found in subscription list: [%s]",
                                lengthKey,
                                frequencyKey,
                                mSubscriptionPrices.toString()
                        )));
            }
        }

        return null;
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
    ) throws Exception {

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

            for (final Map.Entry<String, JsonElement> entrySet : data.entrySet()) {
                String hourKey = entrySet.getKey(); //in the form of {"3.0", "3.5", "4.0", etc.}
                JsonObject priceData = (JsonObject) entrySet.getValue();
                if (priceData != null) {
                    prices.put(
                            hourKey,
                            new Price(
                                    priceData.get(JSON_KEY_FULL_PRICE).getAsInt(),
                                    priceData.get(JSON_KEY_AMOUNT_DUE).getAsInt()
                            )
                    );
                }
                else {
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

    public List<SubscriptionFrequency> getUniqueFrequencies() {
        return mUniqueFrequencies;
    }

    public List<SubscriptionLength> getUniqueLengths() {
        return mUniqueLengths;
    }

    public Map<String, Map<String, SubscriptionPrices>> getSubscriptionPrices() {
        return mSubscriptionPrices;
    }

    public enum CommitmentTypeName {
        @SerializedName(STRING_NO_COMMITMENT)
        NO_COMMITMENT(STRING_NO_COMMITMENT),
        @SerializedName(STRING_MONTHS)
        MONTHS(STRING_MONTHS),
        @SerializedName(STRING_TRIAL)
        TRIAL(STRING_TRIAL);

        private final String mValue;

        CommitmentTypeName(String value) {
            mValue = value;
        }

        @NonNull
        @Override
        public String toString() {
            return mValue;
        }
    }
}
