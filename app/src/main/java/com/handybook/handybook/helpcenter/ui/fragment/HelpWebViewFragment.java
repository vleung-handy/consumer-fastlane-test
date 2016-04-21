package com.handybook.handybook.helpcenter.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyWebView;
import com.handybook.handybook.ui.widget.MenuButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpWebViewFragment extends InjectedFragment
{
    private static final String REDIRECT_TO = "redirect_to";

    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.web_view)
    HandyWebView mWebView;

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
        menuButton.setColor(getResources().getColor(R.color.white));
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

        String helpCenterUrl = getArguments().getString(BundleKeys.HELP_CENTER_URL);
        final String id = getArguments().getString(BundleKeys.HELP_ID);
        final String linkType = getArguments().getString(BundleKeys.HELP_LINK_TYPE);
        if (id != null)
        {
            helpCenterUrl = Uri.parse(helpCenterUrl).buildUpon()
                    .appendQueryParameter(REDIRECT_TO, linkType + id)
                    .build().toString();
        }
        mWebView.loadUrl(helpCenterUrl);

        return view;
    }
}
