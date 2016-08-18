package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 */
public class BookingGeoStatus implements Serializable
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("notice_text")
    private String mNoticeText;

    @SerializedName("pro_lat")
    private double mProLat;

    @SerializedName("pro_lng")
    private double mProLng;

    @SerializedName("in_progress")
    private boolean mInProgress;

    @SerializedName("cancellation_threshold")
    private String mCancelationThreshold;

    public String getStatus()
    {
        return mStatus;
    }

    public void setStatus(final String status)
    {
        mStatus = status;
    }

    public String getNoticeText()
    {
        return mNoticeText;
    }

    public void setNoticeText(final String noticeText)
    {
        mNoticeText = noticeText;
    }

    public double getProLat()
    {
        return mProLat;
    }

    public void setProLat(final double proLat)
    {
        mProLat = proLat;
    }

    public double getProLng()
    {
        return mProLng;
    }

    public void setProLng(final double proLng)
    {
        mProLng = proLng;
    }

    public boolean isInProgress()
    {
        return mInProgress;
    }

    public void setInProgress(final boolean inProgress)
    {
        mInProgress = inProgress;
    }

    public String getCancelationThreshold()
    {
        return mCancelationThreshold;
    }

    public void setCancelationThreshold(final String cancelationThreshold)
    {
        mCancelationThreshold = cancelationThreshold;
    }
}
