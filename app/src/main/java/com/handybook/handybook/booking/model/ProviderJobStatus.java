package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.io.Serializable;
import java.util.Date;

public class ProviderJobStatus implements Serializable
{
    @SerializedName("milestones")
    private Milestone[] mMilestones;
    @SerializedName("links")
    private DeepLinkWrapper[] mDeepLinkWrappers;

    public ProviderJobStatus(final Milestone[] milestones, final DeepLinkWrapper[] deepLinkWrappers)
    {
        mMilestones = milestones;
        mDeepLinkWrappers = deepLinkWrappers;
    }

    public Milestone[] getMilestones() { return mMilestones; }

    public DeepLinkWrapper[] getDeepLinkWrappers() { return mDeepLinkWrappers; }

    public static class Milestone implements Serializable
    {
        public static final String COMPLETE = "complete";
        public static final String INCOMPLETE = "incomplete";
        public static final String WARNING = "warning";
        public static final String ERROR = "error";
        public static final String INVALID = "invalid";
        @SerializedName("title")
        private String mTitle;
        @SerializedName("body")
        private String mBody;
        @SerializedName("status")
        private String mStatus;
        @SerializedName("actions")
        private Action[] mActions;
        @SerializedName("timestamp")
        private Date mTimestamp;

        public Milestone(final String title, final String body, final String status, final Action[] actions, final Date timestamp)
        {
            mTitle = title;
            mBody = body;
            mStatus = status;
            mActions = actions;
            mTimestamp = timestamp;
        }

        public String getTitle() { return mTitle; }

        public String getBody() { return mBody; }

        public String getStatus() { return mStatus; }

        public Action[] getActions() { return mActions; }

        public Date getTimestamp() { return mTimestamp; }

        public int getStatusColorDrawableId()
        {
            if (mStatus == null) { return R.drawable.circle_grey_stroke; }

            switch (mStatus)
            {
                case COMPLETE:
                    return R.drawable.circle_green;
                case INCOMPLETE:
                    return R.drawable.circle_green_stroke;
                case WARNING:
                    return R.drawable.circle_yellow;
                case ERROR:
                    return R.drawable.circle_red;
                case INVALID:
                    return R.drawable.circle_grey;
                default:
                    return R.drawable.circle_grey_stroke;
            }
        }
    }


    public static class Action implements Serializable
    {
        public static final String CALL_OR_TEXT = "call_or_text";

        @SerializedName("type")
        private String mType;

        public Action(final String type) { mType = type; }

        public String getType() { return mType; }
    }


    public static class DeepLinkWrapper implements Serializable
    {
        public static final String TYPE_CANCEL = "cancel";
        public static final String TYPE_RESCHEDULE = "reschedule";

        @SerializedName("text")
        private String mText;
        @SerializedName("type")
        private String mType;
        @SerializedName("deeplink")
        private String mDeeplink;

        public DeepLinkWrapper(final String text, final String type, final String deeplink)
        {
            mText = text;
            mType = type;
            mDeeplink = deeplink;
        }

        public String getText() { return mText; }

        public String getType() { return mType; }

        public String getDeeplink() { return mDeeplink; }
    }
}
