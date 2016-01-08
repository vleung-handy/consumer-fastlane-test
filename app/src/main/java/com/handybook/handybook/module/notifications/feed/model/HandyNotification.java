package com.handybook.handybook.module.notifications.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class HandyNotification implements Serializable, Parcelable
{
    private static Action[] NO_ACTIONS = {}; // For those cases when we need an empty typed array

    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("body")
    private String mBody;
    @SerializedName("html_body")
    private String mHtmlBody;
    @SerializedName("type")
    private HandyNotificationType mType;
    @SerializedName("created_at")
    private Date mCreatedAt;
    @SerializedName("expires_at")
    private Date mExpiresAt;
    @SerializedName("available")
    private boolean mAvailable;
    @SerializedName("read_status")
    private boolean mReadStatus;
    @SerializedName("images")
    private Image[] mImages;
    @SerializedName("actions")
    private Action[] mActions;

    private HandyNotification() {} //Only server can create notifications

    protected HandyNotification(Parcel in)
    {
        mId = in.readLong();
        mTitle = in.readString();
        mBody = in.readString();
        mHtmlBody = in.readString();
        mAvailable = in.readByte() != 0;
        mReadStatus = in.readByte() != 0;
        mImages = in.createTypedArray(Image.CREATOR);
        mActions = in.createTypedArray(Action.CREATOR);
    }

    public static final Creator<HandyNotification> CREATOR = new Creator<HandyNotification>()
    {
        @Override
        public HandyNotification createFromParcel(Parcel in)
        {
            return new HandyNotification(in);
        }

        @Override
        public HandyNotification[] newArray(int size)
        {
            return new HandyNotification[size];
        }
    };

    public long getId()
    {
        return mId;
    }

    public HandyNotificationType getType()
    {
        return mType;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getBody()
    {
        return mBody;
    }

    public String getHtmlBody()
    {
        return mHtmlBody;
    }

    public Date getCreatedAt()
    {
        return mCreatedAt;
    }

    public Date getExpiresAt()
    {
        return mExpiresAt;
    }

    public boolean getReadStatus()
    {
        return mReadStatus;
    }

    public Image[] getImages()
    {
        return mImages;
    }

    public Action[] getActions()
    {
        if (mActions == null)
        {
            mActions = NO_ACTIONS;
        }
        return mActions;
    }

    public Action[] getActions(HandyNotificationActionType actionType)
    {
        ArrayList<Action> actionsOfType = new ArrayList<>();
        for (Action eAction : getActions())
        {
            if(eAction.getType() == actionType){
                actionsOfType.add(eAction);
            }
        }
        if(actionsOfType.isEmpty()){
            return NO_ACTIONS;
        } else{
            return (Action[]) actionsOfType.toArray();
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mBody);
        dest.writeString(mHtmlBody);
        dest.writeByte((byte) (mAvailable ? 1 : 0));
        dest.writeByte((byte) (mReadStatus ? 1 : 0));
        dest.writeTypedArray(mImages, flags);
        dest.writeTypedArray(mActions, flags);
    }


    /**
     * List of HandyNotifications
     * <p/>
     * Currently implemented as ArrayList
     */
    public static class List extends ArrayList<HandyNotification> implements Serializable {}


    public enum HandyNotificationType implements Serializable
    {
        @SerializedName(Constants.TYPE_STRING_NOTIFICATION)
        NOTIFICATION,
        @SerializedName(Constants.TYPE_STRING_PROMO)
        PROMO,
        @SerializedName(Constants.TYPE_STRING_INVALID)
        INVALID;

        public static HandyNotificationType from(final String string)
        {
            switch (string)
            {
                case Constants.TYPE_STRING_NOTIFICATION:
                    return NOTIFICATION;
                case Constants.TYPE_STRING_PROMO:
                    return PROMO;
                default:
                    return INVALID;
            }
        }

        public static class Constants
        {
            public static final String TYPE_STRING_NOTIFICATION = "notification";
            public static final String TYPE_STRING_PROMO = "promo";
            public static final String TYPE_STRING_INVALID = "invalid";
        }
    }


    public enum HandyNotificationActionType implements Serializable
    {
        @SerializedName(Constants.TYPE_STRING_CALL_TO_ACTION_BUTTON)
        CALL_TO_ACTION_BUTTON,
        @SerializedName(Constants.TYPE_STRING_CALL_TO_ACTION)
        CALL_TO_ACTION,
        @SerializedName(Constants.TYPE_STRING_DEFAULT)
        DEFAULT,
        @SerializedName(Constants.TYPE_STRING_INVALID)
        INVALID;

        public static HandyNotificationActionType from(final String string)
        {
            switch (string)
            {
                case Constants.TYPE_STRING_CALL_TO_ACTION_BUTTON:
                    return CALL_TO_ACTION_BUTTON;
                case Constants.TYPE_STRING_CALL_TO_ACTION:
                    return CALL_TO_ACTION;
                case Constants.TYPE_STRING_DEFAULT:
                    return CALL_TO_ACTION;
                default:
                    return INVALID;
            }
        }

        public static class Constants
        {
            public static final String TYPE_STRING_DEFAULT = "default";
            public static final String TYPE_STRING_CALL_TO_ACTION_BUTTON = "call_to_action_button";
            public static final String TYPE_STRING_CALL_TO_ACTION = "call_to_action";
            public static final String TYPE_STRING_INVALID = "invalid";
        }
    }


    public static class Image implements Serializable, Parcelable
    {
        @SerializedName("scale")
        private float mScale;
        @SerializedName("url")
        private String mUrl;

        private Image() {} //No-one is allowed! Only GSON

        protected Image(Parcel in)
        {
            mScale = in.readFloat();
            mUrl = in.readString();
        }

        public static final Creator<Image> CREATOR = new Creator<Image>()
        {
            @Override
            public Image createFromParcel(Parcel in)
            {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size)
            {
                return new Image[size];
            }
        };

        public float getScale()
        {
            return mScale;
        }

        public String getUrl()
        {
            return mUrl;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags)
        {
            dest.writeFloat(mScale);
            dest.writeString(mUrl);
        }
    }


    public static class Action implements Serializable, Parcelable
    {
        @SerializedName("deeplink")
        private String mDeeplink;
        @SerializedName("type")
        private HandyNotificationActionType mType;
        @SerializedName("text")
        private String mText;

        private Action() {} //No-one is allowed! Only GSON

        protected Action(Parcel in)
        {
            mDeeplink = in.readString();
            mText = in.readString();
            mType = HandyNotificationActionType.from(in.readString());
        }

        public static final Creator<Action> CREATOR = new Creator<Action>()
        {
            @Override
            public Action createFromParcel(Parcel in)
            {
                return new Action(in);
            }

            @Override
            public Action[] newArray(int size)
            {
                return new Action[size];
            }
        };

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

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags)
        {
            dest.writeString(mDeeplink);
            dest.writeString(mText);
        }
    }


    public static class ResultSet implements Serializable, Parcelable
    {
        @SerializedName("notifications")
        HandyNotification.List mHandyNotifications;

        private ResultSet() {} //No-one is allowed! Only GSON

        protected ResultSet(Parcel in)
        {
        }

        public static final Creator<ResultSet> CREATOR = new Creator<ResultSet>()
        {
            @Override
            public ResultSet createFromParcel(Parcel in)
            {
                return new ResultSet(in);
            }

            @Override
            public ResultSet[] newArray(int size)
            {
                return new ResultSet[size];
            }
        };

        public List getHandyNotifications()
        {
            return mHandyNotifications;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags)
        {}
    }
}
