package com.handybook.handybook.core.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedDialogFragment;
import com.handybook.handybook.library.ui.view.HandyWebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavbarWebViewDialogFragment extends InjectedDialogFragment {

    public final static String FRAGMENT_TAG = "WEBVIEW_DIALOG_FRAGMENT";
    private final static String BUNDLE_KEY_WEBVIEW_URL = "WEBVIEW_URL";
    private final static String BUNDLE_KEY_NAVBAR_TITLE = "NAVBAR_TITLE";

    @Bind(R.id.handy_webview)
    protected HandyWebView mHandyWebView;

    @Bind(R.id.nav_text)
    protected TextView mTitleText;

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
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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
        mHandyWebView.loadUrl(mWebViewUrl);
        mTitleText.setText(mNavbarTitleString);
        return view;
    }
}
