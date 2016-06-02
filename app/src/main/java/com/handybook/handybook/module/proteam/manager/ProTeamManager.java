package com.handybook.handybook.module.proteam.manager;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.HandyRetrofitCallback;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeamWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import javax.inject.Inject;

public class ProTeamManager
{
    private final Bus mBus;
    private final HandyRetrofitService mService;

    @Inject
    public ProTeamManager(
            final Bus bus,
            HandyRetrofitService service
    )
    {
        mBus = bus;
        mBus.register(this);
        mService = service;
    }

    @Subscribe
    public void onRequestProTeam(final ProTeamEvent.RequestProTeam event)
    {
        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>()
        {
            @Override
            public void onSuccess(final ProTeamWrapper proTeamWrapper)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamSuccess(proTeamWrapper.getProTeam()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamError(error));
            }
        };
        mService.requestProTeam(new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
            }
        });
    }

    @Subscribe
    public void onRequestEditProTeam(final ProTeamEvent.RequestProTeamEdit event)
    {
        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>()
        {
            @Override
            public void onSuccess(final ProTeamWrapper response)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditSuccess(response.getProTeam()));

            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditError(error));
            }
        };
        mService.editProTeam(
                event.getProTeamEditWrapper(),
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                });

    }

}
