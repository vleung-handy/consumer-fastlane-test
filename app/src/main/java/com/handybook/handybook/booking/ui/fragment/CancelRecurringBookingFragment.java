package com.handybook.handybook.booking.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.HandyWebView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used to display a web view that will take the user through recurring booking cancellation steps.
 */
public class CancelRecurringBookingFragment extends InjectedFragment
{
    @Bind(R.id.web_view)
    HandyWebView mWebView;

    public static CancelRecurringBookingFragment newInstance(final String cancelUrl)
    {
        final CancelRecurringBookingFragment fragment = new CancelRecurringBookingFragment();
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.CANCEL_RECURRING_BOOKING_URL, cancelUrl);
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
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_cancel_recurring_booking, container, false);
        ButterKnife.bind(this, view);

        final String cancelUrl = getArguments().getString(BundleKeys.CANCEL_RECURRING_BOOKING_URL);
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
        mWebView.loadUrl(cancelUrl);
        return view;
    }
}
