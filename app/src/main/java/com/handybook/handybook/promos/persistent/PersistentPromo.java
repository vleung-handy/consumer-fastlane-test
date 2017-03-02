package com.handybook.handybook.promos.persistent;

import com.google.gson.annotations.SerializedName;

/**
 * model for the persistent promo view
 */
public class PersistentPromo {

    @SerializedName("id")
    private String mId;
    @SerializedName("image")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitleText;
    @SerializedName("subtitle")
    private String mSubtitleText;
    @SerializedName("action")
    private String mDeepLinkUrl;
    @SerializedName("action_text")
    private String mActionText;
    @SerializedName("preview_text")
    private String mPreviewText;

    String getId() {
        return mId;
    }

    String getImageUrl() {
        return mImageUrl;
    }

    String getTitleText() {
        return mTitleText;
    }

    String getSubtitleText() {
        return mSubtitleText;
    }

    String getDeepLinkUrl() {
        return mDeepLinkUrl;
    }

    String getActionText() {
        return mActionText;
    }

    String getPreviewText() {
        return mPreviewText;
    }

}
