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
    @SerializedName("actions")
    private Action[] mActions;

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

    public Action[] getActions()
    {
        return mActions;
    }

    public static class List extends ArrayList<HandyNotification>
    {

    }


    public enum Type implements Serializable
    {
        @SerializedName("notification")
        NOTIFICATION
    }


    static class Image implements Serializable
    {
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


    static class Action implements Serializable
    {
        @SerializedName("deeplink_path")
        private String mDeeplinkPath;
        @SerializedName("type")
        private String mType;
        @SerializedName("deeplink")
        private String mDeeplink;
        @SerializedName("promo_code")
        private String mPromoCode;
        @SerializedName("promo_url")
        private String mPromoUrl;
        @SerializedName("text")
        private String mText;
        @SerializedName("booking_id")
        private Long mBookingId;
        @SerializedName("deeplink_type")
        private String mDeeplinkType;

        public Action(final String deeplinkPath, final String type, final String deeplink, final String promoCode, final String promoUrl, final String text, final Long bookingId, final String deeplinkType)
        {
            mDeeplinkPath = deeplinkPath;
            mType = type;
            mDeeplink = deeplink;
            mPromoCode = promoCode;
            mPromoUrl = promoUrl;
            mText = text;
            mBookingId = bookingId;
            mDeeplinkType = deeplinkType;
        }

        public String getDeeplinkPath()
        {
            return mDeeplinkPath;
        }

        public String getType()
        {
            return mType;
        }

        public String getDeeplink()
        {
            return mDeeplink;
        }

        public String getPromoCode()
        {
            return mPromoCode;
        }

        public String getPromoUrl()
        {
            return mPromoUrl;
        }

        public String getText()
        {
            return mText;
        }

        public Long getBookingId()
        {
            return mBookingId;
        }

        public String getDeeplinkType()
        {
            return mDeeplinkType;
        }
    }
}
