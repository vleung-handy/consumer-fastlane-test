package com.handybook.handybook.manager;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class UserDataManager
{
    private final UserManager mUserManager;
    private final DataManager mDataManager;
    private final Bus mBus;

    @Inject
    public UserDataManager(final UserManager userManager, final DataManager dataManager,
                           final Bus bus)
    {
        mUserManager = userManager;
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestUpdatePayment(HandyEvent.RequestUpdatePayment event)
    {
        mDataManager.updatePayment(mUserManager.getCurrentUser().getId(), event.token.getId(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdatePaymentSuccess());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdatePaymentError(error));
                    }
                });
    }

}
