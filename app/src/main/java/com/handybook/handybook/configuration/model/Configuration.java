package com.handybook.handybook.configuration.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Configuration implements Serializable
{
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @SerializedName("help_center_url")
    private String mHelpCenterUrl;
    @SerializedName("use_cancel_recurring_web_view")
    private boolean mShouldUseCancelRecurringWebview;
    @SerializedName("my_pro_team_enabled")
    private boolean mMyProTeamEnabled;
    @SerializedName("show_reschedule_flow_on_cancel")
    private boolean mShowRescheduleFlowOnCancel;
    @SerializedName("native_help_center_enabled")
    private boolean mNativeHelpCenterEnabled;
    @SerializedName("address_auto_complete_enabled")
    private boolean mAddressAutoCompleteEnabled;

    @SerializedName("upcoming_and_past_bookings_enabled")
    private boolean mUpcomingAndPastBookingsEnabled;

    @SerializedName("pro_team_facebook_login_enabled")
    private boolean mProTeamFacebookLoginEnabled;
    @SerializedName("appsee_analytics_enabled")
    private boolean mAppseeAnalyticsEnabled;
    @SerializedName("pro_team_profile_pictures_enabled")
    private boolean isProTeamProfilePicturesEnabled;
    @SerializedName("bottom_nav_enabled")
    private boolean mBottomNavEnabled;

    //default this to false
    @SerializedName("chat_enabled")
    private boolean mChatEnabled;

    //default this to false
    @SerializedName("snow_enabled")
    private boolean mSnowEnabled;

    /**
     * used to determine whether we should hide the hours field from the booking flow screens, and
     * whether we should show something like "Up to 3 hours" rather than "3 hours" or the end time
     * in the booking details and upcoming/past booking views
     */
    @SerializedName("booking_hours_clarification_experiment_enabled")
    private boolean mBookingHoursClarificationExperimentEnabled;

    public boolean isBookingHoursClarificationExperimentEnabled()
    {
        return mBookingHoursClarificationExperimentEnabled;
    }

    public boolean isAppseeAnalyticsEnabled()
    {
        return mAppseeAnalyticsEnabled;
    }

    public String getHelpCenterUrl()
    {
        return mHelpCenterUrl;
    }

    public boolean shouldUseCancelRecurringWebview()
    {
        return mShouldUseCancelRecurringWebview;
    }

    public boolean isShowRescheduleFlowOnCancel()
    {
        return mShowRescheduleFlowOnCancel;
    }

    public boolean isNativeHelpCenterEnabled()
    {
        return mNativeHelpCenterEnabled;
    }

    public boolean isAddressAutoCompleteEnabled()
    {
        return mAddressAutoCompleteEnabled;
    }

    public boolean isBottomNavEnabled() {
        return mBottomNavEnabled;
    }

    public String toJson()
    {
        return new GsonBuilder().setDateFormat(DATE_FORMAT).create().toJson(this);
    }

    public static Configuration fromJson(final String json)
    {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT).create().fromJson(json, Configuration.class);
    }

    public boolean isProTeamFacebookLoginEnabled() { return mProTeamFacebookLoginEnabled; }

    public boolean isProTeamProfilePicturesEnabled()
    {
        return isProTeamProfilePicturesEnabled;
    }

    public boolean isChatEnabled()
    {
        return mChatEnabled;
    }

    public boolean isSnowEnabled()
    {
        return mSnowEnabled;
    }
}
