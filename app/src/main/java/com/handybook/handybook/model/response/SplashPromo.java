package com.handybook.handybook.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SplashPromo implements Parcelable
{
    @SerializedName("id")
    private String mId;
    @SerializedName("image")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("subtitle")
    private String mSubtitle;
    @SerializedName("action")
    private String mAction;
    @SerializedName("action_text")
    private String mActionText;
    @SerializedName("template")
    private String mTemplate;

    public String getId()
    {
        return mId;
    }

    public String getImageUrl()
    {
        return mImageUrl;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getSubtitle()
    {
        return mSubtitle;
    }

    public String getAction()
    {
        return mAction;
    }

    public String getActionText()
    {
        return mActionText;
    }

    public String getTemplate()
    {
        return mTemplate;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    private SplashPromo(final Parcel in)
    {
        final String[] stringData = new String[7];
        in.readStringArray(stringData);
        mId = stringData[0];
        mImageUrl = stringData[1];
        mTitle = stringData[2];
        mSubtitle = stringData[3];
        mAction = stringData[4];
        mActionText = stringData[5];
        mTemplate = stringData[6];
    }

    public static final Parcelable.Creator<SplashPromo> CREATOR
            = new Parcelable.Creator<SplashPromo>() {
        public SplashPromo createFromParcel(Parcel in) {
            return new SplashPromo(in);
        }

        public SplashPromo[] newArray(int size) {
            return new SplashPromo[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeStringArray(new String[]{
                mId,
                mImageUrl,
                mTitle,
                mSubtitle,
                mAction,
                mActionText,
                mTemplate
        });
    }
}
