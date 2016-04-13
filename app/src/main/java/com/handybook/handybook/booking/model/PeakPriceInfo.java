package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by odolejsi on 4/13/16.
 */
public class PeakPriceInfo implements Serializable
{
    @SerializedName("date")
    private Date date;
    @SerializedName("price")
    private float price;
    @SerializedName("type")
    private String type;

    public Date getDate()
    {
        return date;
    }

    public String getType()
    {
        return type;
    }

    public float getPrice()
    {
        return price;
    }
}
