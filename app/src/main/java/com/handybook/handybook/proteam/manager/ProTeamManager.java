package com.handybook.handybook.proteam.manager;

import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeamWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import javax.inject.Inject;

public class ProTeamManager
{
    private static final String DEFAULT_USER_ID = "0";
    private final Bus mBus;
    private final HandyRetrofitService mService;
    private final UserManager mUserManager;

    @Inject
    public ProTeamManager(
            final Bus bus,
            HandyRetrofitService service,
            UserManager userManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mService = service;
        mUserManager = userManager;
    }

    @Subscribe
    public void onRequestProTeam(final ProTeamEvent.RequestProTeam event)
    {
        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>()
        {
            @Override
            public void onSuccess(final ProTeamWrapper proTeamWrapper)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamSuccess(proTeamWrapper.getProTeam(),
                        proTeamWrapper.getProTeamHelpCenterUrl()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new ProTeamEvent.ReceiveProTeamError(error));
            }
        };
        requestProTeam(cb);
    }

    public void requestProTeam(final DataManager.Callback<ProTeamWrapper> cb)
    {
        mService.requestProTeam(
                getUserIdString(),
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                });
    }

    private String getUserIdString()
    {
        final User currentUser = mUserManager.getCurrentUser();
        if (currentUser == null)
        {
            return DEFAULT_USER_ID;
        }
        final String id = currentUser.getId();
        return id == null ? DEFAULT_USER_ID : id;
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
                getUserIdString(),
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
