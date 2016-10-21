package com.handybook.handybook.module.referral.manager;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.manager.DefaultPreferencesManager;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetailsResponse;
import com.handybook.handybook.module.referral.model.ReferralResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ReferralsManager
{
    public enum Source
    {
        POST_BOOKING, POST_RATING, REFERRAL_PAGE
    }

    private final Bus mBus;
    private final DataManager mDataManager;
    private final DefaultPreferencesManager mDefaultPreferencesManager;

    /**
     * the minimum time interval between requests for the referral dialog we don't want it show too
     * frequently and annoy users
     */
    private final static long REFERRAL_DIALOG_MINIMUM_REQUEST_INTERVAL_MS =
            48 * DateTimeUtils.MILLISECONDS_IN_SECOND
                    * DateTimeUtils.SECONDS_IN_MINUTE
                    * DateTimeUtils.MINUTES_IN_HOUR; //milliseconds in 48 hours

    @Inject
    public ReferralsManager(
            final Bus bus,
            final DataManager dataManager,
            final DefaultPreferencesManager defaultPreferencesManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mDefaultPreferencesManager = defaultPreferencesManager;
    }

    @Subscribe
    public void onRequestPrepareReferrals(final ReferralsEvent.RequestPrepareReferrals event)
    {
        final long referralDialogLastRequestedTimeMs =
                mDefaultPreferencesManager.getLong(
                        PrefsKey.REFERRAL_DIALOG_LAST_REQUESTED_TIME_MS,
                        0
                );
        /**
         * don't request for the referral dialog more often than every X ms
         */
        if (System.currentTimeMillis() - referralDialogLastRequestedTimeMs > REFERRAL_DIALOG_MINIMUM_REQUEST_INTERVAL_MS)
        {
            mDefaultPreferencesManager.setLong(
                    PrefsKey.REFERRAL_DIALOG_LAST_REQUESTED_TIME_MS,
                    System.currentTimeMillis()
            );
            mDataManager.requestPrepareReferrals(new DataManager.Callback<ReferralResponse>()
            {
                @Override
                public void onSuccess(final ReferralResponse response)
                {
                    mBus.post(new ReferralsEvent.ReceivePrepareReferralsSuccess(
                            response, event.isForDialog(), event.getSource()));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new ReferralsEvent.ReceivePrepareReferralsError(error));
                }
            });
        }
    }

    @Subscribe
    public void onRequestConfirmReferral(final ReferralsEvent.RequestConfirmReferral event)
    {
        mDataManager.requestConfirmReferral(event.getGuid(), new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
            }
        });
    }

    @Subscribe
    public void onRequestRedemptionDetails(final ReferralsEvent.RequestRedemptionDetails event)
    {
        mDataManager.requestRedemptionDetails(
                event.getGuid(),
                new DataManager.Callback<RedemptionDetailsResponse>()
                {
                    @Override
                    public void onSuccess(final RedemptionDetailsResponse redemptionDetailsResponse)
                    {
                        mBus.post(new ReferralsEvent.ReceiveRedemptionDetailsSuccess(
                                redemptionDetailsResponse.getRedemptionDetails()));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new ReferralsEvent.ReceiveRedemptionDetailsError(error));
                    }
                }
        );
    }
}
