package com.handybook.handybook.core.ui.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.library.ui.fragment.InjectedDialogFragment;
import com.handybook.handybook.library.ui.view.HandyWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavbarWebViewDialogFragment extends InjectedDialogFragment {

    public final static String FRAGMENT_TAG = "WEBVIEW_DIALOG_FRAGMENT";
    private final static String BUNDLE_KEY_WEBVIEW_URL = "WEBVIEW_URL";
    private final static String BUNDLE_KEY_NAVBAR_TITLE = "NAVBAR_TITLE";

    @BindView(R.id.handy_webview)
    protected HandyWebView mHandyWebView;

    @BindView(R.id.nav_text)
    protected TextView mTitleText;

    @BindView(R.id.horizontal_progress_bar)
    protected ProgressBar mProgressBar;

    private String mWebViewUrl;
    private String mNavbarTitleString;

    public static NavbarWebViewDialogFragment newInstance(
            String navbarTitleString,
            String webviewUrl
    ) {
        NavbarWebViewDialogFragment navbarWebViewDialogFragment = new NavbarWebViewDialogFragment();

        final Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_WEBVIEW_URL, webviewUrl);
        args.putString(BUNDLE_KEY_NAVBAR_TITLE, navbarTitleString);
        navbarWebViewDialogFragment.setArguments(args);
        return navbarWebViewDialogFragment;
    }

    @OnClick(R.id.exit_button)
    public void onExitButtonClick() {
        dismiss();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenWithStatusBarDialogTheme);
        mWebViewUrl = getArguments().getString(BUNDLE_KEY_WEBVIEW_URL);
        mNavbarTitleString = getArguments().getString(BUNDLE_KEY_NAVBAR_TITLE);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_webview_with_navbar, container, false);
        ButterKnife.bind(this, view);
        initWebView();
        mHandyWebView.loadUrl(mWebViewUrl);
        mTitleText.setText(mNavbarTitleString);
        return view;
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Pre-lollipop devices accept third party cookies by default
            CookieManager.getInstance().setAcceptThirdPartyCookies(mHandyWebView, true);
        }
        mHandyWebView.getSettings().setJavaScriptEnabled(true);
        mHandyWebView.setWebViewClient(new HandyWebViewClient(getActivity()) {
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                mProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
    }
}
