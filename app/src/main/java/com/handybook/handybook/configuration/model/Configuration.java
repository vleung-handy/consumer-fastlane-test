package com.handybook.handybook.configuration.model;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

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
    @SerializedName("onboarding_zip_email_collection_enabled")
    private boolean mSaveZipCodeEnabled;

    //default this to false
    @SerializedName("snow_enabled")
    private boolean mSnowEnabled;

    @SerializedName("pro_team_reschedule_enabled")
    private boolean mEnableProTeamReschedule;
    @SerializedName("pro_team_reschedule_cta_enabled")
    private boolean mEnableProTeamRescheduleCTA;
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

    @SerializedName("instant_book_reschedules_enabled")
    private boolean mInstantBookViaRescheduleEnabled;

    @SerializedName("my_pros_tab_enabled")
    private boolean mMyProsTabEnabled;

    @SerializedName("vat_indicator_enabled")
    private boolean mVatIndicatorEnabled;

    public boolean isVatIndicatorEnabled() {
        return true;
    }

    public boolean isMyProsTabEnabled() {
        return mMyProsTabEnabled;
    }
    /**
     * if true, the "my bookings" tab will show both upcoming and past bookings
     * else it will just show upcoming bookings
     */
    @SerializedName("upcoming_and_past_bookings_merge_enabled")
    private boolean mUpcomingAndPastBookingsMergeEnabled;

    public boolean isUpcomingAndPastBookingsMergeEnabled() {
        return mUpcomingAndPastBookingsMergeEnabled;
    }

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

    public boolean isSettingFavoriteProEnabled() {
        return mSettingFavoriteProEnabled;
    }

    public boolean isSaveZipCodeEnabled() {
        return mSaveZipCodeEnabled;
    }

    public boolean isNewRatingFlowEnabled() {
        return mNewRatingFlowEnabled;
    }

    public boolean isInstantBookViaRescheduleEnabled() {
        return false;
        //TODO note from Sammy: This is going in ghost until Reschedule Datepicker redesign work is scheduled
        // mInstantBookViaRescheduleEnabled;
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

    @VisibleForTesting
    public void setSaveZipCodeEnabled(final boolean saveZipCodeEnabled) {
        mSaveZipCodeEnabled = saveZipCodeEnabled;
    }
}
