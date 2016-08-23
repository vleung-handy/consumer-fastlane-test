package com.handybook.handybook.helpcenter.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.helpcenter.model.HelpEvent;
import com.handybook.handybook.helpcenter.model.HelpNode;
import com.handybook.handybook.helpcenter.model.HelpNodeWrapper;
import com.handybook.handybook.model.response.HelpCenterResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class HelpManager
{
    //server still expects us to send null in the request but we can cache by assuming null means root
    public static final String ROOT_NODE_ID = "root";

    private final Bus bus;
    private final DataManager dataManager;
    private final UserManager userManager;

    private final Cache<String, HelpNode> helpNodeCache;

    @Inject
    public HelpManager(final Bus bus, final DataManager dataManager, final UserManager userManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.userManager = userManager;
        this.bus.register(this);

        //TODO: we don't currently have a way to query to see if a node is changed so we rely on our cache decaying every day
        this.helpNodeCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    @Subscribe
    public void onRequestHelpNodeDetails(HelpEvent.RequestHelpNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        if (nodeId != null) //nulls will crash our cache on the getIfPresentCall
        {
            final HelpNode cachedHelpNode = helpNodeCache.getIfPresent(nodeId);
            if (cachedHelpNode != null)
            {
                bus.post(new HelpEvent.ReceiveHelpNodeSuccess(cachedHelpNode));
                return;
            }
        }

        //Server expects us to request will a null node ID if we want root
        if (nodeId != null && nodeId.equals(ROOT_NODE_ID))
        {
            nodeId = null;
        }

        dataManager.getHelpInfo(nodeId, bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                helpNodeCache.put(Integer.toString(helpNode.getId()), helpNode);
                //don't cache the child nodes, they look like full data but don't have their children
                bus.post(new HelpEvent.ReceiveHelpNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HelpEvent.ReceiveHelpNodeError(error));
            }
        });
    }

    @Subscribe
    public void onRequestHelpBookingNodeDetails(HelpEvent.RequestHelpBookingNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        //DO NOT CACHE BOOKING NODES
        dataManager.getHelpBookingsInfo(nodeId, bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                bus.post(new HelpEvent.ReceiveHelpBookingNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HelpEvent.ReceiveHelpBookingNodeError(error));
            }
        });

    }

    @Subscribe
    public void onRequestHelpCenter(final HelpEvent.RequestHelpCenter event)
    {
        dataManager.getHelpCenterInfo(new DataManager.Callback<HelpCenterResponse>()
        {
            @Override
            public void onSuccess(final HelpCenterResponse response)
            {
                bus.post(new HelpEvent.ReceiveHelpCenterSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                bus.post(new HelpEvent.ReceiveHelpCenterError(error));
            }
        });
    }
}
