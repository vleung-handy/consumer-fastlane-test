package com.handybook.handybook.manager;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

public class HelpContactManager
{
    private final Bus bus;
    private final DataManager dataManager;

    @Inject
    public HelpContactManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    @Subscribe
    public void onRequestNotifyHelpContact(HandyEvent.RequestNotifyHelpContact event)
    {
        TypedInput body = event.body;

        dataManager.createHelpCase(body, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(Void response)
            {
                bus.post(new HandyEvent.ReceiveNotifyHelpContactSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveNotifyHelpContactError(error));
            }
        });
    }
}
