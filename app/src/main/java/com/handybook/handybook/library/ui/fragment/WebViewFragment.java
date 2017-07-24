package com.handybook.handybook.library.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.view.HandyWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewFragment extends InjectedFragment {

    @BindView(R.id.handy_web_view)
    HandyWebView mWebView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.horizontal_progress_bar)
    ProgressBar mHorizontalProgressBar;

    public static WebViewFragment newInstance(@NonNull final String uri) {
        return newInstance(uri, "");
    }

    public static WebViewFragment newInstance(@NonNull String uri, @NonNull String title) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.TARGET_URL, uri);
        args.putString(BundleKeys.TITLE, title);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getArguments().getString(BundleKeys.TITLE));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                else {
                    getActivity().onBackPressed();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        mWebView.setWebViewClient(new HandyWebViewClient(getActivity()) {
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                showProgressBar();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                showProgressBar();
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                hideProgressBar();
                super.onPageFinished(view, url);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);

        String uri = getArguments().getString(BundleKeys.TARGET_URL);
        mWebView.loadUrl(uri);
    }

    protected void loadURL(String url) {
        mWebView.loadUrl(url);
    }

    protected void showProgressBar() {
        mHorizontalProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        mHorizontalProgressBar.setVisibility(View.GONE);
    }
}
