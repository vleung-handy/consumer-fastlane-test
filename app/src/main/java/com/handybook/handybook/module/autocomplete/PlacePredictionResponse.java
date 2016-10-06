package com.handybook.handybook.module.autocomplete;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.ZipValidationResponse;
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
     * Since we don't have zip in the prediction response,
     * the next best thing is to filter it by city or state
     */
    public void filter(ZipValidationResponse.ZipArea filterBy)
    {
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

            //if there is something to filter, and it doesn't match, then remove it
            if (filterBy != null && !isMatchingCityAndState(filterBy, p))
            {
                predictions.remove(p);
                continue;
            }

        }
    }

    /**
     * If both the city and state matches, then return true;
     *
     * @return
     */
    private boolean isMatchingCityAndState(
            @NonNull final ZipValidationResponse.ZipArea zipArea,
            @NonNull final PlacePrediction prediction
    )
    {

        //we only want to filter if it's not blank, otherwise, ignore the filter
        if (!TextUtils.isBlank(zipArea.getCity()))
        {
            if (!zipArea.getCity().equalsIgnoreCase(prediction.getCity()))
            {
                return false;
            }
        }

        if (!TextUtils.isBlank(zipArea.getState()))
        {
            if (!zipArea.getState().equalsIgnoreCase(prediction.getState()))
            {
                return false;
            }
        }

        return true;
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
