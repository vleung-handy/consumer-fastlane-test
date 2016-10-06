package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jtse on 3/23/16.
 */
public class ZipValidationResponse
{
    @SerializedName("region_time_zone")
    private String mTimeZone;

    @SerializedName("ziparea")
    private ZipArea mZipArea;


    public static class ZipArea implements Serializable
    {

        @SerializedName("city")
        private String mCity;

        @SerializedName("state")
        private String mState;

        @SerializedName("zip")
        private String mZip;

        public String getZip()
        {
            return mZip;
        }

        public void setZip(final String zip)
        {
            mZip = zip;
        }

        public String getCity()
        {
            return mCity;
        }

        public void setCity(final String city)
        {
            mCity = city;
        }

        public String getState()
        {
            return mState;
        }

        public void setState(final String state)
        {
            mState = state;
        }
    }

    public ZipArea getZipArea()
    {
        return mZipArea;
    }

    public String getTimeZone()
    {
        return mTimeZone;
    }

    public void setTimeZone(final String timeZone)
    {
        mTimeZone = timeZone;
    }
}
