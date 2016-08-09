package com.handybook.handybook.manager;

import android.support.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.data.DataManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ServicesManager
{
    private DataManager mDataManager;
    private Bus mBus;
    private static final String KEY_ALL_SERVICES = "all_services";
    private static final String KEY_ALL_SERVICES_MAP = "all_services_map";
    private static final String KEY_ALL_SERVICES_ICON_MAP = "all_services_map_icon";

    private Cache<String, List<Service>> mServicesCache;
    private Cache<String, Map<String, Service>> mServicesMapCache;

    /**
     * This is the map of all the main services to the drawable that should be used.
     * A "main" service is a service with no parent.
     */
    private Map<String, Integer> mServicesIconMap;

    @Inject
    public ServicesManager(DataManager dataManager, Bus bus)
    {
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);

        mServicesCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();

        mServicesMapCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();

        mServicesIconMap = new HashMap<>();
        //FIXME: JIA: make this icon map, with drawables and stuff.


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

    @Subscribe
    public void onRequestAllServices(BookingEvent.RequestAllServices event)
    {

        if (getCacheAllServices() != null)
        {
            mBus.post(new BookingEvent.ReceiveAllServicesSuccess(getCacheAllServices()));
            return;
        }
        else
        {
            mDataManager.getAllServices(new DataManager.Callback<List<Service>>()
            {
                @Override
                public void onSuccess(final List<Service> response)
                {
                    setCacheAllServices(response);
                    createAndSetServicesMap(response);
                    mBus.post(new BookingEvent.ReceiveAllServicesSuccess(response));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new BookingEvent.ReceiveAllServicesError(error));
                }
            });
        }
    }


    private void setCacheAllServices(List<Service> allServices)
    {
        mServicesCache.put(KEY_ALL_SERVICES, allServices);
    }

    @Nullable
    private List<Service> getCacheAllServices()
    {
        return mServicesCache.getIfPresent(KEY_ALL_SERVICES);
    }

    public void createAndSetServicesMap(List<Service> services)
    {
        if (services == null || services.isEmpty())
        {
            return;
        }

        Map<String, Service> serviceMap = new HashMap<>();
        for (final Service service : services)
        {
            serviceMap.put(String.valueOf(service.getId()), service);
        }

        mServicesMapCache.put(KEY_ALL_SERVICES_MAP, serviceMap);
    }

    /**
     * This is the map of all the services with service ID as the key
     */
    private Map<String, Service> getServicesMap()
    {
        return mServicesMapCache.getIfPresent(KEY_ALL_SERVICES_MAP);
    }

    public Map<String, Integer> getServicesIconMap()
    {
        return mServicesIconMap;
    }
}
