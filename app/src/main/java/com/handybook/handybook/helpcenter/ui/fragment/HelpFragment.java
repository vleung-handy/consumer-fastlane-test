package com.handybook.handybook.helpcenter.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.helpcenter.ui.activity.HelpNativeActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpWebViewActivity;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

public class HelpFragment extends InjectedFragment
{
    public static Fragment newInstance(final Bundle extras)
    {
        final Fragment fragment = new HelpFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        requestConfiguration();
    }

    private void requestConfiguration()
    {
        bus.post(new ConfigurationEvent.RequestConfiguration());
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        final Configuration configuration = event.getConfiguration();
        if (configuration.shouldUseHelpCenterWebView())
        {
            final Bundle arguments = new Bundle();
            if (getArguments() != null)
            {
                arguments.putAll(getArguments());
            }
            arguments.putString(BundleKeys.HELP_CENTER_URL, configuration.getHelpCenterUrl());
            activity.navigateToActivity(HelpWebViewActivity.class, arguments);
        }
        else
        {
            activity.navigateToActivity(HelpNativeActivity.class, getArguments());
        }
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    )
    {
        showErrorDialog(event.error.getMessage(), new DialogCallback()
        {
            @Override
            public void onRetry()
            {
                requestConfiguration();
            }

            @Override
            public void onCancel()
            {
                ((MenuDrawerActivity) getActivity())
                        .navigateToActivity(ServiceCategoriesActivity.class);
            }
        });
    }
}
