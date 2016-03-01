package com.handybook.handybook.module.referral.event;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.module.referral.model.ReferralResponse;

public abstract class ReferralsEvent
{
    public static class RequestPrepareReferrals extends HandyEvent.RequestEvent
    {
    }


    public static class ReceivePrepareReferralsSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final ReferralResponse mReferralResponse;

        public ReceivePrepareReferralsSuccess(final ReferralResponse referralResponse)
        {
            mReferralResponse = referralResponse;
        }

        public ReferralResponse getReferralResponse()
        {
            return mReferralResponse;
        }
    }


    public static class ReceivePrepareReferralsError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePrepareReferralsError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestConfirmReferral
    {
        private String mGuid;

        public RequestConfirmReferral(final String guid)
        {
            mGuid = guid;
        }

        public String getGuid()
        {
            return mGuid;
        }
    }


    public static class RequestRedemptionDetails
    {
        private String mGuid;

        public RequestRedemptionDetails(final String guid)
        {
            mGuid = guid;
        }

        public String getGuid()
        {
            return mGuid;
        }
    }


    public static class ReceiveRedemptionDetailsSuccess
    {
        private RedemptionDetails mRedemptionDetails;

        public ReceiveRedemptionDetailsSuccess(final RedemptionDetails redemptionDetails)
        {
            mRedemptionDetails = redemptionDetails;
        }

        public RedemptionDetails getRedemptionDetails()
        {
            return mRedemptionDetails;
        }
    }


    public static class ReceiveRedemptionDetailsError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveRedemptionDetailsError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    @Track("referral screen shown")
    public static class ReferralScreenShown {}


    @Track("invite friends clicked")
    public static class InviteFriendsClicked {}


    @Track("other share option clicked")
    public static class OtherShareOptionsClicked
    {
        @TrackField("share option")
        private String mShareOption;

        public OtherShareOptionsClicked(final String shareOption)
        {
            mShareOption = shareOption;
        }
    }


    @Track("accept referral shown")
    public static class RedemptionScreenShown
    {
    }


    @Track("accept referral signup")
    public static class RedemptionSignUpClicked
    {
        @TrackField("signup type")
        private String mType;

        public RedemptionSignUpClicked(final String type)
        {
            mType = type;
        }
    }


    @Track("accept referral existing login")
    public static class RedemptionLogin
    {
    }
}
