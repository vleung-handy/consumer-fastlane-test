package com.handybook.handybook.module.autocomplete;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class PlacePredictionResponse implements Serializable
{
    @SerializedName("predictions")
    public ArrayList<PlacePrediction> predictions;

    /**
     * Since we don't have zip, the next best thing is to filter it by city or state
     *
     * @param city
     * @param state
     */
    public void filter(String city, String state)
    {
        if (TextUtils.isBlank(city) && TextUtils.isBlank(state)) {
            return;
        }

        for (int i = predictions.size() - 1; i >= 0; i--)
        {
            PlacePrediction p = predictions.get(i);

            boolean isAddress = false;
            for (String s : p.getTypes())
            {
                if (s.contains("address"))
                {
                    isAddress = true;
                    break;
                }
            }

            //if this is not an "address" type, remove it
            if (!isAddress)
            {
                predictions.remove(p);
                continue;
            }

            //if neither city nor state matches, remove it
            if (!p.getCity().equalsIgnoreCase(city) && p.getState().equalsIgnoreCase(state)) {
                predictions.remove(p);
                continue;
            }
        }
    }

    public List<String> getFullAddresses()
    {
        ArrayList<String> rval = new ArrayList();

        if (predictions == null || predictions.isEmpty())
        {
            return rval;
        }

        for (PlacePrediction p : predictions)
        {
            rval.add(p.getDescription());
        }

        return rval;

    }

}
