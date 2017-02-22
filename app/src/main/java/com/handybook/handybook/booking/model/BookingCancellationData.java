package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.io.Serializable;

/**
 * {
 *   "warning_message": "A $15 fee will be charged when skipping less than 24 hours in advance.",
 *   "cancellation_info": {
 *     "title": "Please let us know why you're skipping.",
 *     "navigation_title": "Skip Booking",
 *     "button_label": "Skip and Add Credits",
 *     "reasons": [
 *       {
 *         "id": 100,
 *         "label": "Lost track of this booking"
 *       },
 *       {
 *         "id": 110,
 *         "label": "Lost track of this"
 *       },
 *       {
 *         "id": 120,
 *         "label": "Lost track"
 *       }
 *     ]
 *   },
 *   "precancellation_info": {
 *     "title": "Your plan term ends Dec 12, 2017",
 *     "navigation_title": "Skip Booking",
 *     "button_label": "Continue",
 *     "message": "The value of your cleaning ($75.50) will be charged, and added to your account as credits for later use."
 *   }
 * }
 */
public class BookingCancellationData implements Serializable {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
            .create();

    @SerializedName("warning_message")
    private String mWarningMessage;
    @SerializedName("cancellation_info")
    private CancellationInfo mCancellationInfo;
    @SerializedName("precancellation_info")
    private PreCancellationInfo mPreCancellationInfo;

    @NonNull
    public static BookingCancellationData fromJson(@NonNull final String json) {
        return GSON.fromJson(json, BookingCancellationData.class);
    }

    @NonNull
    public static String toJson(final BookingCancellationData value) {
        return GSON.toJson(value, BookingCancellationData.class);
    }

    @Nullable
    public String getWarningMessage() { return mWarningMessage; }

    @NonNull
    public CancellationInfo getCancellationInfo() { return mCancellationInfo; }

    @Nullable
    public PreCancellationInfo getPreCancellationInfo() { return mPreCancellationInfo; }

    public boolean hasWarning() { return !TextUtils.isEmpty(getWarningMessage()); }

    public boolean hasPrecancellationInfo() { return getPreCancellationInfo() != null; }

    public static class CancellationInfo implements Serializable {

        @SerializedName("title")
        private String mTitle;
        @SerializedName("reasons")
        private CancellationReason[] mReasons;
        @SerializedName("button_label")
        private String mButtonLabel;
        @SerializedName("navigation_title")
        private String mNavigationTitle;

        @NonNull
        public String getTitle() { return mTitle; }

        @NonNull
        public CancellationReason[] getReasons() { return mReasons; }

        @NonNull
        public String getButtonLabel() { return mButtonLabel; }

        @NonNull
        public String getNavigationTitle() { return mNavigationTitle; }
    }


    public static class PreCancellationInfo implements Serializable {

        @SerializedName("title")
        private String mTitle;
        @SerializedName("message")
        private String mMessage;
        @SerializedName("button_label")
        private String mButtonLabel;
        @SerializedName("navigation_title")
        private String mNavigationTitle;
        @SerializedName("url")
        private String mUrl;

        @NonNull
        public String getTitle() { return mTitle; }

        @NonNull
        public String getMessage() { return mMessage; }

        @NonNull
        public String getButtonLabel() { return mButtonLabel; }

        @NonNull
        public String getNavigationTitle() { return mNavigationTitle; }

        public boolean hasUrl() {
            return mUrl != null;
        }

        @Nullable
        public String getUrl() {
            return mUrl;
        }

    }


    public static class CancellationReason implements Serializable {

        @SerializedName("id")
        private Integer mId;
        @SerializedName("label")
        private String mLabel;

        @NonNull
        public Integer getId() { return mId; }

        @NonNull
        public String getLabel() { return mLabel; }
    }
}
