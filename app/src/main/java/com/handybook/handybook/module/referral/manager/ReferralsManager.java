package com.handybook.handybook.module.referral.manager;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.ReferralResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ReferralsManager
{
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
    public void onRequestPrepareReferrals(ReferralsEvent.RequestPrepareReferrals event)
    {
        mDataManager.requestPrepareReferrals(new DataManager.Callback<ReferralResponse>()
        {
            @Override
            public void onSuccess(final ReferralResponse response)
            {
                mBus.post(new ReferralsEvent.ReceivePrepareReferralsSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ReferralsEvent.ReceivePrepareReferralsError(error));
            }
        });
    }
}
