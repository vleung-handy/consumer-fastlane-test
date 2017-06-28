package com.handybook.handybook.vegas;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.vegas.model.RewardsWrapper;

import org.json.JSONObject;

import javax.inject.Inject;

public class VegasManager {

    private final HandyRetrofitService mService;

    @Inject
    public VegasManager(HandyRetrofitService service) {
        mService = service;
    }

    public void getReward(
            final DataManager.Callback<RewardsWrapper> cb
    ) {
        mService.getRewards(new RewardsWrapperCallback(cb) {
            @Override
            public void success(final JSONObject response) {
                cb.onSuccess(RewardsWrapper.fromJson(response.toString()));
            }
        });
    }

    public void claimReward(
            final long rewardId,
            final DataManager.Callback<Void> cb
    ) {
        mService.claimReward(rewardId, new HandyRetrofitCallback(cb) {
            @Override
            protected void success(final JSONObject response) {
                cb.onSuccess(null);
            }
        });
    }


}
