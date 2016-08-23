package com.handybook.handybook.helpcenter.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyWebView;
import com.handybook.handybook.ui.widget.MenuButton;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpWebViewFragment extends InjectedFragment
{
    private static final String REDIRECT_TO = "redirect_to";

    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.web_view)
    HandyWebView mWebView;
    private String helpCenterUrl;

    public static HelpWebViewFragment newInstance(final Bundle arguments)
    {
        final HelpWebViewFragment fragment = new HelpWebViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_help_web_view, container, false);
        ButterKnife.bind(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        menuButton.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mMenuButtonLayout.addView(menuButton);

        mWebView.setWebViewClient(new HandyWebViewClient(getActivity())
        {
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon)
            {
                showUiBlockers();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url)
            {
                showUiBlockers();
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url)
            {
                removeUiBlockers();
                super.onPageFinished(view, url);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null && !args.isEmpty())
        {
            helpCenterUrl = getArguments().getString(BundleKeys.HELP_CENTER_URL);
            if (!Strings.isNullOrEmpty(helpCenterUrl))
            { loadURL(); }
            else
            {
                requestConfiguration();
            }
        }
        else
        {
            requestConfiguration();
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        removeUiBlockers();
        final Configuration configuration = event.getConfiguration();
        if (configuration != null)
        {
            helpCenterUrl = configuration.getHelpCenterUrl();
            loadURL();
        }
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    )
    {
        removeUiBlockers();
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
                        .navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
            }
        });
    }

    private void requestConfiguration()
    {
        showUiBlockers();
        bus.post(new ConfigurationEvent.RequestConfiguration());
    }

    private void loadURL()
    {
        Bundle args = getArguments();
        if (args != null && !args.isEmpty())
        {
            final String id = getArguments().getString(BundleKeys.HELP_ID);
            final String linkType = getArguments().getString(BundleKeys.HELP_LINK_TYPE);
            if (id != null && linkType != null)
            {
                helpCenterUrl = Uri.parse(helpCenterUrl).buildUpon()
                        .appendQueryParameter(REDIRECT_TO, linkType + id)
                        .build().toString();
            }
        }
        mWebView.loadUrl(helpCenterUrl);
    }
}
