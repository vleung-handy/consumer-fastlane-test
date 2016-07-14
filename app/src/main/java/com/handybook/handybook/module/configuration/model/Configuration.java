package com.handybook.handybook.module.configuration.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class Configuration
{
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @SerializedName("use_help_center_web_view")
    private boolean mShouldUseHelpCenterWebView;
    @SerializedName("help_center_url")
    private String mHelpCenterUrl;
    @SerializedName("use_cancel_recurring_web_view")
    private boolean mShouldUseCancelRecurringWebview;

    @SerializedName("my_pro_team_enabled")
    private boolean mMyProTeamEnabled;

    //    FIXME: JIA: put stuff here for sending users to reschedule when they try to cancel
    @SerializedName("show_reschedule_flow_on_cancel")
    private boolean mShowRescheduleFlowOnCancel;

    public boolean shouldUseHelpCenterWebView()
    {
        return mShouldUseHelpCenterWebView;
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

    public boolean isMyProTeamEnabled()
    {
        return mMyProTeamEnabled;
    }

    public String toJson()
    {
        return new GsonBuilder().setDateFormat(DATE_FORMAT).create()
                .toJson(this);
    }

    public static Configuration fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat(DATE_FORMAT).create()
                .fromJson(json, Configuration.class);
    }
}
