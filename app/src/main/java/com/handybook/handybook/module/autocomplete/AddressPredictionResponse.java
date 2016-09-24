package com.handybook.handybook.module.autocomplete;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class AddressPredictionResponse implements Serializable
{
    @SerializedName("results")
    public ArrayList<AddressPrediction> predictions;

    public void filter(String zipToFilter)
    {
        if (TextUtils.isBlank(zipToFilter)) {
            return;
        }

        for (int i = predictions.size() - 1; i >= 0; i--)
        {
            AddressPrediction p = predictions.get(i);

            if (!zipToFilter.equals(p.getZip())) {
                predictions.remove(p);
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

        for (AddressPrediction p : predictions)
        {
            rval.add(p.getFullAddress());
        }

        return rval;

    }

}
