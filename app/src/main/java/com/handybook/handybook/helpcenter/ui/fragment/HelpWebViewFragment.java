package com.handybook.handybook.helpcenter.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.HandyWebView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpWebViewFragment extends InjectedFragment
{
    private static final String REDIRECT_TO = "redirect_to";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.web_view)
    HandyWebView mWebView;
    private String mHelpCenterUrl;
    private String mId;
    private String mLinkType;

    public static HelpWebViewFragment newInstance(final Bundle arguments)
    {
        final HelpWebViewFragment fragment = new HelpWebViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && !args.isEmpty())
        {
            mHelpCenterUrl = getArguments().getString(BundleKeys.HELP_CENTER_URL);

            // The following will be null if not provided
            mId = getArguments().getString(BundleKeys.HELP_ID);
            mLinkType = getArguments().getString(BundleKeys.HELP_LINK_TYPE);
        }
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

        setupToolbar(mToolbar, getString(R.string.help));
        if (!mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled()
                && getActivity() instanceof MenuDrawerActivity)
        {
            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }

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
        if (!Strings.isNullOrEmpty(mHelpCenterUrl))
        { loadURL(); }
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
            mHelpCenterUrl = configuration.getHelpCenterUrl();
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
                if (getActivity() instanceof MenuDrawerActivity)
                {
                    ((MenuDrawerActivity) getActivity())
                            .navigateToActivity(
                                    ServiceCategoriesActivity.class,
                                    R.id.nav_menu_home
                            );
                }
                else
                {
                    getActivity().onBackPressed();
                }
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
        if (!Strings.isNullOrEmpty(mId) && !Strings.isNullOrEmpty(mLinkType))
        {
            mHelpCenterUrl = Uri.parse(mHelpCenterUrl).buildUpon()
                                .appendQueryParameter(REDIRECT_TO, mLinkType + mId)
                                .build().toString();
        }
        mWebView.loadUrl(mHelpCenterUrl);
    }
}
