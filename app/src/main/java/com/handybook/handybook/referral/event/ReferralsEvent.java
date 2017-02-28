package com.handybook.handybook.referral.event;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.model.RedemptionDetails;
import com.handybook.handybook.referral.model.ReferralResponse;

public abstract class ReferralsEvent {

    public static class RequestPrepareReferrals extends HandyEvent.RequestEvent {

        private boolean mIsForDialog;
        private ReferralsManager.Source mSource;

        public RequestPrepareReferrals(
                final boolean isForDialog,
                @NonNull final ReferralsManager.Source source
        ) {
            mIsForDialog = isForDialog;
            mSource = source;
        }

        public boolean isForDialog() {
            return mIsForDialog;
        }

        public ReferralsManager.Source getSource() {
            return mSource;
        }
    }


    public static class ReceivePrepareReferralsSuccess extends HandyEvent.ReceiveSuccessEvent {

        private final ReferralResponse mReferralResponse;
        private boolean mIsForDialog;
        private ReferralsManager.Source mSource;

        public ReceivePrepareReferralsSuccess(
                final ReferralResponse referralResponse,
                final boolean isForDialog,
                @NonNull final ReferralsManager.Source source
        ) {
            mReferralResponse = referralResponse;
            mIsForDialog = isForDialog;
            mSource = source;
        }

        public ReferralResponse getReferralResponse() {
            return mReferralResponse;
        }

        public boolean isForDialog() {
            return mIsForDialog;
        }

        public ReferralsManager.Source getSource() {
            return mSource;
        }
    }


    public static class ReceivePrepareReferralsError extends HandyEvent.ReceiveErrorEvent {

        public ReceivePrepareReferralsError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestConfirmReferral {

        private String mGuid;

        public RequestConfirmReferral(final String guid) {
            mGuid = guid;
        }

        public String getGuid() {
            return mGuid;
        }
    }


    public static class RequestRedemptionDetails {

        private String mGuid;

        public RequestRedemptionDetails(final String guid) {
            mGuid = guid;
        }

        public String getGuid() {
            return mGuid;
        }
    }


    public static class ReceiveRedemptionDetailsSuccess {

        private RedemptionDetails mRedemptionDetails;

        public ReceiveRedemptionDetailsSuccess(final RedemptionDetails redemptionDetails) {
            mRedemptionDetails = redemptionDetails;
        }

        public RedemptionDetails getRedemptionDetails() {
            return mRedemptionDetails;
        }
    }


    public static class ReceiveRedemptionDetailsError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveRedemptionDetailsError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }
}
