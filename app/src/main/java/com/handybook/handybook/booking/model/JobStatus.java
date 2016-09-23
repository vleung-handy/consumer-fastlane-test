package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.io.Serializable;
import java.util.Date;

public class JobStatus implements Serializable
{
    @SerializedName("milestones")
    private Milestone[] mMilestones;
    @SerializedName("links")
    private DeepLinkWrapper[] mDeepLinkWrappers;
    @SerializedName("complete")
    private boolean mComplete;

    public JobStatus(final Milestone[] milestones, final DeepLinkWrapper[] deepLinkWrappers, final boolean complete)
    {
        mMilestones = milestones;
        mDeepLinkWrappers = deepLinkWrappers;
        mComplete = complete;
    }

    public Milestone[] getMilestones() { return mMilestones; }

    public DeepLinkWrapper[] getDeepLinkWrappers() { return mDeepLinkWrappers; }

    public boolean isComplete() { return mComplete; }

    // Return stroke if it's the last element and the job is completed
    public int getStatusDrawableId(int index)
    {
        boolean isFill = index < mMilestones.length - 1 || mComplete;
        return mMilestones[index].getStatusDrawableId(isFill);
    }

    public static class Milestone implements Serializable
    {
        public static final String NORMAL = "normal";
        public static final String WARNING = "warning";
        public static final String ERROR = "error";
        public static final String INVALID = "invalid";

        public static final String STARTS_SOON = "starts_soon";
        public static final String ARRIVED = "arrived";
        public static final String ON_MY_WAY = "on_my_way";
        public static final String UNAVAILABLE = "unavailable";
        public static final String BEHIND_SCHEDULE = "behind_schedule";
        public static final String PRO_LATE = "pro_late";
        public static final String PRO_NO_SHOW = "pro_no_show";
        public static final String COMPLETED = "completed";
        public static final String PRO_ARRIVED_LATE = "pro_arrived_late";

        @SerializedName("title")
        private String mTitle;
        @SerializedName("body")
        private String mBody;
        @SerializedName("state")
        private String mState;
        @SerializedName("status")
        private String mStatus;
        @SerializedName("action")
        private Action mAction;
        @SerializedName("timestamp")
        private Date mTimestamp;

        public Milestone(final String title, final String body, final String status, final Action action, final Date timestamp)
        {
            mTitle = title;
            mBody = body;
            mStatus = status;
            mAction = action;
            mTimestamp = timestamp;
        }

        public String getTitle() { return mTitle; }

        public String getBody() { return mBody; }

        public String getState()
        {
            return mState;
        }

        public String getStatus() { return mStatus; }

        public Action getAction() { return mAction; }

        public Date getTimestamp() { return mTimestamp; }

        public int getStatusDrawableId(boolean isFill)
        {
            if (mStatus == null)
            {
                return R.drawable.circle_grey_stroke_light;
            }

            switch (mStatus)
            {
                case Milestone.NORMAL:
                    return isFill ? R.drawable.circle_green : R.drawable.circle_green_stroke;
                case Milestone.WARNING:
                    return isFill ? R.drawable.circle_yellow : R.drawable.circle_yellow_stroke;
                case Milestone.ERROR:
                    return isFill ? R.drawable.circle_red : R.drawable.circle_red_stroke;
                case Milestone.INVALID:
                    return isFill ? R.drawable.circle_grey : R.drawable.circle_grey_stroke;
                default:
                    return R.drawable.circle_grey_stroke_light;
            }
        }

    }


    public static class Action implements Serializable
    {
        public static final String CALL_OR_TEXT = "call_sms_contact";

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
        @SerializedName("link")
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
