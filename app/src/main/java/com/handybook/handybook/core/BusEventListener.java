package com.handybook.handybook.core;

import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Subscribe;

public class BusEventListener //TODO: give better name + package
{
    private BaseActivityInterface mBaseActivityInterface;
    public BusEventListener(BaseActivityInterface baseActivityInterface)
    {
        mBaseActivityInterface = baseActivityInterface;
    }

    @Subscribe
    public void onReceiveSplashPromoSuccess(HandyEvent.ReceiveAvailableSplashPromoSuccess event)
    {
        mBaseActivityInterface.showSplashPromo(event.splashPromo);
    }

}
