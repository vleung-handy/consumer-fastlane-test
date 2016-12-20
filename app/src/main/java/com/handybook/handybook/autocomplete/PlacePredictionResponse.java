package com.handybook.handybook.autocomplete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.library.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlacePredictionResponse implements Serializable
{
    @SerializedName("predictions")
    @NonNull
    public ArrayList<PlacePrediction> predictions = new ArrayList<>();

    /**
     * Since we don't have zip in the prediction response,
     * the next best thing is to filter it by city
     */
    public void filter(@Nullable ZipValidationResponse.ZipArea filterBy)
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
            if (filterBy != null && !isMatchingCity(filterBy, p))
            {
                predictions.remove(p);
            }

        }
    }

    /**
     * If the city matches, then return true;
     *
     * @return
     */
    private boolean isMatchingCity(
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

        return true;
    }

    @NonNull
    public List<String> getFullAddresses()
    {
        ArrayList<String> rval = new ArrayList();

        if (predictions.isEmpty())
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
