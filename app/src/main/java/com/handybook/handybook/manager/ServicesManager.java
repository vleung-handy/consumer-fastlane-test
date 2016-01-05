package com.handybook.handybook.manager;

import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

public class ServicesManager
{
    private DataManager mDataManager;
    private Bus mBus;

    @Inject
    public ServicesManager(DataManager dataManager, Bus bus)
    {
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestServices(final HandyEvent.RequestServices event)
    {
        mDataManager.getServices(new DataManager.CacheResponse<List<Service>>()
        {
            @Override
            public void onResponse(final List<Service> services)
            {
                mBus.post(new HandyEvent.ReceiveCachedServicesSuccess(services));
            }
        }, new DataManager.Callback<List<Service>>()
        {
            @Override
            public void onSuccess(final List<Service> services)
            {
                mBus.post(new HandyEvent.ReceiveServicesSuccess(services));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveServicesError(error));
            }
        });
    }
}
