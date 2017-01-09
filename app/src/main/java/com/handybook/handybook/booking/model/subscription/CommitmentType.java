package com.handybook.handybook.booking.model.subscription;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * In the new pricing / subscription model, there can only be two commitment types :
 * {no_commitment, months}
 */
public class CommitmentType implements Serializable
{
    @SerializedName("no_commitment")
    private JSONObject mNoCommitment;

    @SerializedName("months")
    private JSONObject mMonths;

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
        //TODO: JIA: For now, only use mMonths if mNoCommitment is null;
        try
        {
            if (mNoCommitment != null)
            {
                process(mNoCommitment);
            }
            else if (mMonths != null)
            {
                process(mMonths);
            }
        }
        catch (JSONException e)
        {
            //This shouldn't happen, but if it does, just log it
            Crashlytics.logException(new RuntimeException(e.getMessage()));
        }
    }

    /**
     * The bulk of the processing happens here. The prices are nested many levels deep.
     *
     * @param data
     */
    public void process(JSONObject data) throws JSONException
    {

        //The first level has keys to the subscription Lengths.
        Iterator<String> lengthKeys = data.keys();
        while (lengthKeys.hasNext())
        {
            String lengthKey = lengthKeys.next();       //in the form of {"0", "3", "6", "9", etc.}
            JSONObject lengthInformation = data.getJSONObject(lengthKey);


        }
    }

    private boolean contains(List<SubscriptionType> subscriptionTypes, String key) {
        if (subscriptionTypes != null) {
            for (SubscriptionType sub : subscriptionTypes) {
                if (sub.getKey().equals(key)) {
                    return true;
                }
            }
        }

        return false;
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
