package com.handybook.handybook.module.proteam.manager;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
        mService.requestProTeam(new DataManager.Callback<ProTeam>()
        {
            @Override
            public void onSuccess(final ProTeam proTeam)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamSuccess(proTeam));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamError(error));
            }
        });
    }

    @Subscribe
    public void onRequestEditProTeam(final ProTeamEvent.RequestProTeamEdit event)
    {
        mService.editProTeam(event.getProTeamEdit(), new DataManager.Callback<ProTeam>()
        {
            @Override
            public void onSuccess(final ProTeam proTeam)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditSuccess(proTeam));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditError(error));
            }
        });
    }

}
