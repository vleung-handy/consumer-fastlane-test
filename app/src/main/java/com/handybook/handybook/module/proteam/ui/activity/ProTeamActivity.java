package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.squareup.otto.Subscribe;


public class ProTeamActivity extends MenuDrawerActivity
{

    private Configuration mConfiguration;
    private Object mBusListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //this is to work around the subscriber inheritance issue that Otto has.
        //https://github.com/square/otto/issues/26
        mBusListener = new Object()
        {
            @Subscribe
            public void onReceiveConfigurationSuccess(
                    final ConfigurationEvent.ReceiveConfigurationSuccess event
            )
            {
                if (event != null)
                {
                    mConfiguration = event.getConfiguration();
                    if (event.getConfiguration() != null && !event.getConfiguration().isMyProTeamEnabled())
                    {
                        //pro team is not enabled, not supposed to be here to begin with, so bye bye.
                        startActivity(new Intent(ProTeamActivity.this, ServiceCategoriesActivity.class));
                    }
                }
            }
        };
    }

    @Override
    protected Fragment createFragment()
    {
        return ProTeamFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(mBusListener);

        if (mConfiguration == null)
        {
            mBus.post(new ConfigurationEvent.RequestConfiguration());
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBus.unregister(mBusListener);
    }
}
