package com.handybook.handybook.module.referral.manager;

import com.handybook.handybook.data.DataManager;
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

    @Inject
    public ReferralsManager(final Bus bus, final DataManager dataManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
    }

    @Subscribe
    public void onRequestPrepareReferrals(final ReferralsEvent.RequestPrepareReferrals event)
    {
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
