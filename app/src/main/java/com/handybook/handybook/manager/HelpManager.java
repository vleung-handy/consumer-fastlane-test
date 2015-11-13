package com.handybook.handybook.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.HelpNodeWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
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
    public void onRequestHelpNodeDetails(HandyEvent.RequestHelpNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        if (nodeId != null) //nulls will crash our cache on the getIfPresentCall
        {
            final HelpNode cachedHelpNode = helpNodeCache.getIfPresent(nodeId);
            if (cachedHelpNode != null)
            {
                bus.post(new HandyEvent.ReceiveHelpNodeSuccess(cachedHelpNode));
                return;
            }
        }

        //Server expects us to request will a null node ID if we want root
        if(nodeId != null && nodeId.equals(ROOT_NODE_ID))
        {
            nodeId = null;
        }

        dataManager.getHelpInfo(nodeId, getAuthToken(), bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                helpNodeCache.put(Integer.toString(helpNode.getId()), helpNode);
                //don't cache the child nodes, they look like full data but don't have their children
                bus.post(new HandyEvent.ReceiveHelpNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveHelpNodeError(error));
            }
        });
    }

    @Subscribe
    public void onRequestHelpBookingNodeDetails(HandyEvent.RequestHelpBookingNode event)
    {
        String nodeId = event.nodeId;
        String bookingId = event.bookingId;

        //DO NOT CACHE BOOKING NODES
        dataManager.getHelpBookingsInfo(nodeId, getAuthToken(), bookingId, new DataManager.Callback<HelpNodeWrapper>()
        {
            @Override
            public void onSuccess(HelpNodeWrapper helpNodeWrapper)
            {
                HelpNode helpNode = helpNodeWrapper.getHelpNode();
                bus.post(new HandyEvent.ReceiveHelpBookingNodeSuccess(helpNode));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveHelpBookingNodeError(error));
            }
        });

    }

    private String getAuthToken()
    {
        final User user = userManager.getCurrentUser();
        return (user != null ? user.getAuthToken() : null);
    }
}