package com.handybook.handybook.module.autocomplete;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 */
public class PlacePrediction implements Serializable
{

    @SerializedName("place_id")
    public String placeId;
    public String description;

    public List<Map<String, String>> terms;
    public List<String> types;

    @Nullable
    public String getAddress()
    {
        try
        {
            return terms.get(0).get("value") + " " + terms.get(1).get("value");
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public String getCity()
    {
        try
        {
            return terms.get(2).get("value");
        }
        catch (Exception e)
        {
            return null;
        }
    }


    @Nullable
    public String getState()
    {
        try
        {
            return terms.get(3).get("value");
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
