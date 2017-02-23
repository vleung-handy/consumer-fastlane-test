package com.handybook.handybook.promos.splash;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * this is the model that is returned from the /promos endpoint
 *
 * splash promos are displayed in the notification feed so are actually a type of notification
 */
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
    private String mDeepLinkUrl;
    @SerializedName("action_text")
    private String mActionText;
    @SerializedName("template")
    private String mTemplate;
    @SerializedName("none") //returns 1 if there is no splash promo to display, 0 or null otherwise
    private Integer mShouldNotDisplay;

    /**
     * mShouldNotDisplay == 1 if there is no splash promo to display, 0 otherwise
     *
     * @return
     */
    public boolean shouldDisplay()
    {
        return mShouldNotDisplay == null || mShouldNotDisplay == 0;
    }

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

    public String getDeepLinkUrl()
    {
        return mDeepLinkUrl;
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
        mDeepLinkUrl = stringData[4];
        mActionText = stringData[5];
        mTemplate = stringData[6];

        mShouldNotDisplay = in.readInt();
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
                mDeepLinkUrl,
                mActionText,
                mTemplate
        });

        dest.writeInt(mShouldNotDisplay == null ? 0 : mShouldNotDisplay);
    }
}
