package com.handybook.handybook.module.configuration.model;

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
    @SerializedName("lockbox_entry_method_enabled")
    private boolean mLockboxEntryMethodEnabled;
    @SerializedName("native_help_center_enabled")
    private boolean mNativeHelpCenterEnabled;
    @SerializedName("new_account_enabled")
    private boolean mNewAccountEnabled;
    @SerializedName("address_autocomplete_enabled")
    private boolean mAddressAutoCompleteEnabled;

    @SerializedName("upcoming_and_past_bookings_enabled")
    private boolean mUpcomingAndPastBookingsEnabled;
    @SerializedName("pro_team_facebook_login_enabled")
    private boolean mProTeamFacebookLoginEnabled;
    @SerializedName("appsee_analytics_enabled")
    private boolean mAppseeAnalyticsEnabled;

    public boolean isAppseeAnalyticsEnabled()
    {
        return mAppseeAnalyticsEnabled;
    }

    public boolean isLockboxEntryMethodEnabled()
    {
        return mLockboxEntryMethodEnabled;
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

    public boolean isUpcomingAndPastBookingsEnabled()
    {
        return mUpcomingAndPastBookingsEnabled;
    }

    public boolean isMyProTeamEnabled()
    {
        return mMyProTeamEnabled;
    }

    public boolean isNativeHelpCenterEnabled()
    {
        return mNativeHelpCenterEnabled;
    }

    public boolean isNewAccountEnabled() { return mNewAccountEnabled; }

    public boolean isAddressAutoCompleteEnabled()
    {
        return mAddressAutoCompleteEnabled;
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
}
