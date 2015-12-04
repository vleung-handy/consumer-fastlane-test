package com.handybook.handybook.module.notifications.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class HandyNotification implements Serializable
{
    @SerializedName("id")
    private long mId;
    @SerializedName("type")
    private HandyNotificationType mType;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("body")
    private String mBody;
    @SerializedName("created_at")
    private Calendar mCreatedAt;
    @SerializedName("expires_at")
    private Calendar mExpiresAt;
    @SerializedName("priority")
    private long mPriority;
    @SerializedName("read_status")
    private long mReadStatus;
    @SerializedName("images")
    private Image[] mImages;
    @SerializedName("actions")
    private Action[] mActions;


    public static class List extends ArrayList<HandyNotification> implements Serializable {}


    public enum HandyNotificationType implements Serializable
    {
        @SerializedName("notification")
        NOTIFICATION,
        @SerializedName("promo")
        PROMO
    }


    public enum HandyNotificationActionType implements Serializable
    {
        @SerializedName("notification")
        NOTIFICATION,
        @SerializedName("call_to_action")
        CALL_TO_ACTION
    }


    static class Image implements Serializable, Parcelable
    {
        @SerializedName("scale")
        private float mScale;
        @SerializedName("url")
        private String mUrl;

        private Image() {} //No-one is allowed! Only GSON

        public float getScale()
        {
            return mScale;
        }

        public String getUrl()
        {
            return mUrl;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeFloat(this.mScale);
            dest.writeString(this.mUrl);
        }

        protected Image(Parcel in)
        {
            this.mScale = in.readFloat();
            this.mUrl = in.readString();
        }

        public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>()
        {
            public Image createFromParcel(Parcel source) {return new Image(source);}

            public Image[] newArray(int size) {return new Image[size];}
        };
    }


    static class Action implements Serializable, Parcelable
    {
        @SerializedName("deeplink")
        private String mDeeplink;
        @SerializedName("type")
        private HandyNotificationActionType mType;
        @SerializedName("text")
        private String mText;
        @SerializedName("booking_id")
        private Long mBookingId;

        private Action() {} //No-one is allowed! Only GSON

        public String getDeeplink()
        {
            return mDeeplink;
        }

        public HandyNotificationActionType getType()
        {
            return mType;
        }

        public String getText()
        {
            return mText;
        }

        public Long getBookingId()
        {
            return mBookingId;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(this.mDeeplink);
            dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
            dest.writeString(this.mText);
            dest.writeValue(this.mBookingId);
        }

        protected Action(Parcel in)
        {
            this.mDeeplink = in.readString();
            int tmpMType = in.readInt();
            this.mType = tmpMType == -1 ? null : HandyNotificationActionType.values()[tmpMType];
            this.mText = in.readString();
            this.mBookingId = (Long) in.readValue(Long.class.getClassLoader());
        }

        public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>()
        {
            public Action createFromParcel(Parcel source) {return new Action(source);}

            public Action[] newArray(int size) {return new Action[size];}
        };
    }
}
