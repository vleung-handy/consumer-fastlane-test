package com.handybook.handybook.manager;

import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.data.DataManager;
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
    public void onRequestCachedServices(final BookingEvent.RequestCachedServices event)
    {
        mBus.post(new BookingEvent.ReceiveCachedServicesSuccess(mDataManager.getCachedServices()));
    }

    @Subscribe
    public void onRequestServices(final BookingEvent.RequestServices event)
    {
        mDataManager.getServices(new DataManager.CacheResponse<List<Service>>()
        {
            @Override
            public void onResponse(final List<Service> services)
            {
                mBus.post(new BookingEvent.ReceiveCachedServicesSuccess(services));
            }
        }, new DataManager.Callback<List<Service>>()
        {
            @Override
            public void onSuccess(final List<Service> services)
            {
                mBus.post(new BookingEvent.ReceiveServicesSuccess(services));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveServicesError(error));
            }
        });
    }
}
