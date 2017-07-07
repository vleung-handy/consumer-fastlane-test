package com.handybook.handybook.helpcenter.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.squareup.otto.Subscribe;

public class HelpWebViewFragment extends WebViewFragment {

    private static final String REDIRECT_TO = "redirect_to";

    private String mHelpCenterUrl;
    private String mId;
    private String mLinkType;

    @NonNull
    public static HelpWebViewFragment newInstance(final Bundle arguments) {
        final HelpWebViewFragment fragment = new HelpWebViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    public static HelpWebViewFragment newInstance(@NonNull final String helpCenterUrl) {
        final Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.HELP_CENTER_URL, helpCenterUrl);
        final HelpWebViewFragment fragment = new HelpWebViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && !args.isEmpty()) {
            mHelpCenterUrl = getArguments().getString(BundleKeys.HELP_CENTER_URL);

            // The following will be null if not provided
            mId = getArguments().getString(BundleKeys.HELP_ID);
            mLinkType = getArguments().getString(BundleKeys.HELP_LINK_TYPE);
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle(getString(R.string.help));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Strings.isNullOrEmpty(mHelpCenterUrl)) { loadURL(); }
        else {
            requestConfiguration();
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    ) {
        hideProgressSpinner();
        final Configuration configuration = event.getConfiguration();
        if (configuration != null) {
            mHelpCenterUrl = configuration.getHelpCenterUrl();
            loadURL();
        }
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    ) {
        hideProgressSpinner();
        showErrorDialog(event.error.getMessage(), new DialogCallback() {
            @Override
            public void onRetry() {
                requestConfiguration();
            }

            @Override
            public void onCancel() {
                getActivity().onBackPressed();
            }
        });
    }

    private void requestConfiguration() {
        showProgressSpinner(true);
        bus.post(new ConfigurationEvent.RequestConfiguration());
    }

    private void loadURL() {
        if (!Strings.isNullOrEmpty(mId) && !Strings.isNullOrEmpty(mLinkType)) {
            mHelpCenterUrl = Uri.parse(mHelpCenterUrl).buildUpon()
                                .appendQueryParameter(REDIRECT_TO, mLinkType + mId)
                                .build().toString();
        }
        loadURL(mHelpCenterUrl);
    }
}
