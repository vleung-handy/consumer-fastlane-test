package com.handybook.handybook.module.notifications.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class HandyNotification implements Serializable
{
    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("body")
    private String mBody;
    @SerializedName("type")
    private HandyNotification.Type mType;
    @SerializedName("images")
    private HandyNotification.Image[] mImages;
    @SerializedName("created_at")
    private Calendar mCreatedOn;
    @SerializedName("expires_at")
    private Calendar mExpiresOn;

    public long getId()
    {
        return mId;
    }

    public void setId(final long id)
    {
        mId = id;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getBody()
    {
        return mBody;
    }

    public Type getType()
    {
        return mType;
    }

    public Image[] getImages()
    {
        return mImages;
    }

    public Calendar getCreatedOn()
    {
        return mCreatedOn;
    }

    public Calendar getExpiresOn()
    {
        return mExpiresOn;
    }

    public static class List extends ArrayList<HandyNotification>
    {

    }

    public enum Type implements Serializable{
        @SerializedName("notification")
        NOTIFICATION
    }
    static class Image implements Serializable{
        @SerializedName("scale")
        private float mScale;
        @SerializedName("url")
        private String mUrl;

        public Image(final float scale, final String url)
        {
            mScale = scale;
            mUrl = url;
        }

        public float getScale()
        {
            return mScale;
        }

        public String getUrl()
        {
            return mUrl;
        }
    }
}
