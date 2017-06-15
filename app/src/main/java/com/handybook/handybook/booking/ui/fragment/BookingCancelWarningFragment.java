package com.handybook.handybook.booking.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.core.HandyWebViewClient;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.HandyWebView;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingCancelWarningFragment extends ProgressSpinnerFragment {

    public static final String EXTRA_BOOKING_CANCELLATION_DATA
            = "com.handy.handy.EXTRA_BOOKING_CANCELLATION_DATA";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking mBooking;
    private BookingCancellationData mBookingCancellationData;
    private BookingCancellationData.PreCancellationInfo mPreCancellationInfo;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_booking_cancel_warning_warning)
    TextView mWarning;
    @BindView(R.id.fragment_booking_cancel_warning_title)
    TextView mTitle;
    @BindView(R.id.fragment_booking_cancel_warning_message)
    TextView mMessage;
    @BindView(R.id.fragment_booking_cancel_warning_button)
    Button mButton;
    @BindView(R.id.fragment_booking_cancel_warning_webview)
    HandyWebView mWebView;

    @NonNull
    public static BookingCancelWarningFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final BookingCancellationData bookingCancellationData
    ) {
        final BookingCancelWarningFragment fragment = new BookingCancelWarningFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOOKING_CANCELLATION_DATA, bookingCancellationData);
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingCancellationData = (BookingCancellationData) getArguments()
                .getSerializable(EXTRA_BOOKING_CANCELLATION_DATA);
        mBooking = getArguments().getParcelable(EXTRA_BOOKING);
        if ( // Make sure we have all the necessary data
                mBooking == null
                || mBookingCancellationData == null
                || !mBookingCancellationData.hasPrecancellationInfo()) {
            onNextClicked();
        }
        mPreCancellationInfo = mBookingCancellationData.getPreCancellationInfo();
        bus.post(new LogEvent.AddLogEvent(
                new BookingLog.BookingCancelWarningShown(mBooking.getId())
        ));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_cancel_warning, container, false));

        ButterKnife.bind(this, view);
        if (mPreCancellationInfo.hasUrl()) { initWebUi(); }
        else { initUi(); }
        return view;
    }

    private void initUi() {
        setupToolbar(mToolbar, mPreCancellationInfo.getNavigationTitle(), true);
        mWebView.setVisibility(View.GONE);
        mWarning.setText(mBookingCancellationData.getWarningMessage());
        mWarning.setVisibility(mBookingCancellationData.hasWarning() ? View.VISIBLE : View.GONE);
        mTitle.setText(mPreCancellationInfo.getTitle());
        mMessage.setText(mPreCancellationInfo.getMessage());
        mButton.setText(mPreCancellationInfo.getButtonLabel());

    }

    @SuppressWarnings("SetJavaScriptEnabled")
    private void initWebUi() {
        setupToolbar(mToolbar, mPreCancellationInfo.getNavigationTitle(), true);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new HandyWebViewClient(getActivity()) {
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                showProgressSpinner();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                showProgressSpinner();
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                hideProgressSpinner();
                super.onPageFinished(view, url);
            }

            /**
             * API23 and higher
             */
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onReceivedError(
                    final WebView view,
                    final WebResourceRequest request,
                    final WebResourceError error
            ) {
                super.onReceivedError(view, request, error);
                // Page failed to load (>400 AFAIK)
                //This check is more involved to try to fix issues with auto-http->https redirects..
                if (request.getUrl().getEncodedSchemeSpecificPart().equals(
                        Uri.parse(view.getUrl()).getEncodedSchemeSpecificPart()
                )) {
                    hideProgressSpinner();
                    onNextClicked();
                }
            }

            /**
             *  API22 and below
             */
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(
                    final WebView view,
                    final int errorCode,
                    final String description,
                    final String failingUrl
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // Page failed to load (>400 AFAIK)
                if (failingUrl.equals(view.getUrl())) {
                    hideProgressSpinner();
                    onNextClicked();
                }
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                return true;
            }
        });
        mWebView.setLongClickable(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mPreCancellationInfo.getUrl());
        mWarning.setText(mBookingCancellationData.getWarningMessage());
        mWarning.setVisibility(mBookingCancellationData.hasWarning() ? View.VISIBLE : View.GONE);
        mTitle.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
        mButton.setText(mPreCancellationInfo.getButtonLabel());

    }

    @OnClick(R.id.fragment_booking_cancel_warning_button)
    public void onNextClicked() {
        FragmentUtils.switchToFragment(
                BookingCancelWarningFragment.this,
                BookingCancelReasonFragment.newInstance(mBooking, mBookingCancellationData),
                false
        );
    }
}
