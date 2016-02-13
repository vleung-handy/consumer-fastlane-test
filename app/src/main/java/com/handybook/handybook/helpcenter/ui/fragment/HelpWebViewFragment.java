package com.handybook.handybook.helpcenter.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyWebView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpWebViewFragment extends InjectedFragment
{
    @Bind(R.id.web_view)
    HandyWebView mWebView;

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
        showUiBlockers();
        bus.post(new ConfigurationEvent.RequestConfiguration());
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        removeUiBlockers();
        final String helpCenterUrl = event.getConfiguration().getHelpCenterUrl();
        mWebView.loadUrl(helpCenterUrl);
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    )
    {
        // TODO: Implement
    }
}
