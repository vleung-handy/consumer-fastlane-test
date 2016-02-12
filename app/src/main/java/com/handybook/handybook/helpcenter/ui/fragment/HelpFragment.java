package com.handybook.handybook.helpcenter.ui.fragment;

import android.support.v4.app.Fragment;

import com.handybook.handybook.helpcenter.ui.activity.HelpNativeActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpWebViewActivity;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

public class HelpFragment extends InjectedFragment
{
    public static Fragment newInstance()
    {
        return new HelpFragment();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new ConfigurationEvent.RequestConfiguration());
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        if (event.getConfiguration().shouldUseHelpCenterWebView())
        {
            activity.navigateToActivity(HelpWebViewActivity.class);
        }
        else
        {
            activity.navigateToActivity(HelpNativeActivity.class);
        }
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    )
    {
        // TODO: Implement
    }
}
