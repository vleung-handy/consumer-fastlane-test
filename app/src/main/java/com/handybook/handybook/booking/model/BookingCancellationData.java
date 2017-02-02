package com.handybook.handybook.booking.model;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.io.Serializable;

/**
 * {
 *   "warning_message": "A $15 fee will be charged when skipping less than 24 hours in advance.",
 *   "cancellation_info": {
 *     "title": "Please let us know why you're skipping.",
 *     "reasons": [{
 *       "id": 4,
 *       "label": "Solid Reason"
 *     }, {
 *       "id": 3,
 *       "label": "Another great reason"
 *     }, {
 *       "id": 2,
 *       "label": "Not really sure"
 *     }, {
 *       "id": 1,
 *       "label": "I really like radio buttons"
 *     }]
 *   },
 *   "precancellation_info": {
 *     "title": "Your plan term ends Dec 12, 2017",
 *     "message": "The value of your cleaning ($75.50) will be charged, and added to your account as credits for later use."
 *   }
 * }
 */
public class BookingCancellationData implements Serializable
{
    @SerializedName("warning_message")
    private String mWarningMessage;
    @SerializedName("cancellation_info")
    private CancellationInfo mCancellationInfo;
    @SerializedName("precancellation_info")
    private PreCancellationInfo mPreCancellationInfo;

    public static BookingCancellationData fromJson(final String json)
    {
        return new GsonBuilder()
                .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                .create()
                .fromJson(json, BookingCancellationData.class);
    }

    public static String toJson(final BookingCancellationData value)
    {
        return new GsonBuilder()
                .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                .create()
                .toJson(value, BookingCancellationData.class);
    }

    public String getWarningMessage()
    {
        return mWarningMessage;
    }

    public CancellationInfo getCancellationInfo()
    {
        return mCancellationInfo;
    }

    public PreCancellationInfo getPreCancellationInfo()
    {
        return mPreCancellationInfo;
    }

    public boolean hasWarning()
    {
        return !TextUtils.isEmpty(getWarningMessage());
    }

    public boolean hasPrecancellationInfo()
    {
        return getPreCancellationInfo() != null;
    }

    public class CancellationInfo implements Serializable
    {
        @SerializedName("title")
        private String mTitle;
        @SerializedName("reasons")
        private CancellationReason[] mReasons;

        public String getTitle()
        {
            return mTitle;
        }

        public CancellationReason[] getReasons()
        {
            return mReasons;
        }
    }


    public class PreCancellationInfo implements Serializable
    {
        @SerializedName("title")
        private String mTitle;
        @SerializedName("message")
        private String mMessage;

        public String getTitle()
        {
            return mTitle;
        }

        public String getMessage()
        {
            return mMessage;
        }
    }


    public class CancellationReason implements Serializable
    {
        @SerializedName("id")
        private Integer mId;
        @SerializedName("label")
        private String mLabel;

        public Integer getId()
        {
            return mId;
        }

        public String getLabel()
        {
            return mLabel;
        }
    }


    public static String EXAMPLE_PAYLOAD = "{\n" +
            "  \"warning_message\": \"A $15 fee will be charged when skipping less than 24 hours in advance.\",\n" +
            "  \"cancellation_info\": {\n" +
            "    \"title\": \"Please let us know why you're skipping.\",\n" +
            "    \"reasons\": [\n" +
            "      {\n" +
            "        \"id\": 4,\n" +
            "        \"label\": \"Solid Reason\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 3,\n" +
            "        \"label\": \"Another great reason\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"label\": \"Not really sure\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"label\": \"I really like radio buttons\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"precancellation_info\": {\n" +
            "    \"title\": \"Your plan term ends Dec 12, 2017\",\n" +
            "    \"message\": \"The value of your cleaning ($75.50) will be charged, and added to your account as credits for later use.\"\n" +
            "  }\n" +
            "}";
}
