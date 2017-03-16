package com.handybook.handybook.configuration.model;

import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Configuration implements Serializable {

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
    @SerializedName("tab_bar_navigation_enabled")
    private boolean mBottomNavEnabled;
    @SerializedName("new_services_home_page_enabled")
    private boolean mHomeScreenV2Enabled;
    @SerializedName("onboarding_zip_email_collection_enabled")
    private boolean mOnboardingV2Enabled;

    // We want to keep the navigation type for the session.
    // Would be weird experience if user switches between zip codes where it's enabled/disabled
    private static Boolean mIsBottomNavEnabledForSession;

    //default this to false
    @SerializedName("snow_enabled")
    private boolean mSnowEnabled;

    @SerializedName("pro_team_reschedule_enabled")
    private boolean mEnableProTeamReschedule;
    @SerializedName("pro_team_reschedule_cta_enabled")
    private boolean mEnableProTeamRescheduleCTA;

    @SerializedName("direct_sms_to_chat_enabled")
    private boolean mDirectSmsToChatEnabled;
    @SerializedName("setting_favorite_pro_enabled")
    private boolean mSettingFavoriteProEnabled;

    /**
     * used to determine whether we should hide the hours field from the booking flow screens, and
     * whether we should show something like "Up to 3 hours" rather than "3 hours" or the end time
     * in the booking details and upcoming/past booking views
     */
    @SerializedName("booking_hours_clarification_experiment_enabled")
    private boolean mBookingHoursClarificationExperimentEnabled;
    @SerializedName("new_rating_flow_enabled")
    private boolean mNewRatingFlowEnabled;
    @SerializedName("rate_app_banner")
    private ReviewAppBanner mReviewAppBanner;

    @SerializedName("consolidated_quote_flow_enabled")
    private boolean mConsolidateBookingGetQuoteFlowExperimentEnabled;

    public ReviewAppBanner getReviewAppBanner() {
        return mReviewAppBanner;
    }

    public boolean isConsolidateBookingGetQuoteFlowExperimentEnabled() {
        return mConsolidateBookingGetQuoteFlowExperimentEnabled;
    }

    public boolean isBookingHoursClarificationExperimentEnabled()
    {
        return mBookingHoursClarificationExperimentEnabled;
    }

    public boolean isAppseeAnalyticsEnabled() {
        return mAppseeAnalyticsEnabled;
    }

    public String getHelpCenterUrl() {
        return mHelpCenterUrl;
    }

    public boolean shouldUseCancelRecurringWebview() {
        return mShouldUseCancelRecurringWebview;
    }

    public boolean isShowRescheduleFlowOnCancel() {
        return mShowRescheduleFlowOnCancel;
    }

    public boolean isNativeHelpCenterEnabled() {
        return mNativeHelpCenterEnabled;
    }

    public boolean isAddressAutoCompleteEnabled() {
        return mAddressAutoCompleteEnabled;
    }

    public boolean isBottomNavEnabled() {
        //Do this if this is first time bottom nav is set for session
        if (mIsBottomNavEnabledForSession == null) {
            mIsBottomNavEnabledForSession = mBottomNavEnabled;
        }

        return mIsBottomNavEnabledForSession;
    }

    public boolean isHomeScreenV2Enabled() {
        return mHomeScreenV2Enabled;
    }

    public String toJson() {
        return new GsonBuilder().setDateFormat(DATE_FORMAT).create().toJson(this);
    }

    public static Configuration fromJson(final String json) {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT).create().fromJson(json, Configuration.class);
    }

    public boolean isProTeamFacebookLoginEnabled() { return mProTeamFacebookLoginEnabled; }

    public boolean isProTeamProfilePicturesEnabled() {
        return isProTeamProfilePicturesEnabled;
    }

    public boolean isSnowEnabled() {
        return mSnowEnabled;
    }

    public boolean isProTeamRescheduleEnabled() { return mEnableProTeamReschedule; }

    public boolean isProTeamRescheduleCTAEnabled() { return mEnableProTeamRescheduleCTA; }

    public boolean isDirectSmsToChatEnabled() {
        return mDirectSmsToChatEnabled;
    }

    public boolean isSettingFavoriteProEnabled() {
        return mSettingFavoriteProEnabled;
    }

    public boolean isOnboardingV2Enabled() {
        return mOnboardingV2Enabled;
    }

    public boolean isNewRatingFlowEnabled() {
        return mNewRatingFlowEnabled;
    }

    // Setters
    public void setBottomNavEnabled(final boolean bottomNavEnabled) {
        mBottomNavEnabled = bottomNavEnabled;
    }

    public void setHomeScreenV2Enabled(final boolean homeScreenV2Enabled) {
        mHomeScreenV2Enabled = homeScreenV2Enabled;
    }

    public static class ReviewAppBanner implements Serializable {

        @SerializedName("enabled")
        private boolean mEnabled;
        /**
         * number of days to wait after the user declines the rate app banner before
         * re-displaying it
         */
        @SerializedName("display_delay_days_after_declined")
        private Integer mDisplayDelayDaysAfterDeclined;

        @Nullable
        public Integer getDisplayDelayDaysAfterDeclined() {
            return mDisplayDelayDaysAfterDeclined;
        }

        public boolean isEnabled() {
            return mEnabled;
        }
    }

    public boolean isShareProEnabled() {
        //hard coding this to false so the new referral screen will never appear
        return false;
    }
}
