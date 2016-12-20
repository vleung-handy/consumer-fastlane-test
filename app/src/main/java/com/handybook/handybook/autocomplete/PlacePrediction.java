package com.handybook.handybook.autocomplete;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * See this for sample JSON
 * https://maps.googleapis.com/maps/api/place/autocomplete/json?input=1720%2077&key=AIzaSyAHF9HgE7xMYv0oPExv9mNUxKnOTtfTOAc&types=address
 *
 */
public class PlacePrediction implements Serializable
{
    @SerializedName("description")
    private String mDescription;

    @SerializedName("terms")
    private List<Map<String, String>> mTerms;

    @SerializedName("types")
    private List<String> mTypes;

    @Nullable
    public String getAddress()
    {
        try
        {
            return mTerms.get(0).get("value") + " " + mTerms.get(1).get("value");
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
            return mTerms.get(2).get("value");
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
            return mTerms.get(3).get("value");
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getDescription()
    {
        return mDescription;
    }

    public List<String> getTypes()
    {
        return mTypes;
    }
}
