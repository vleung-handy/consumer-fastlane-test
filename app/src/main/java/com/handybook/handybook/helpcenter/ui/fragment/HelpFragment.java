package com.handybook.handybook.helpcenter.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.util.BookingUtil;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.model.response.HelpCenterResponse;
import com.handybook.handybook.core.ui.view.HelpCenterActionItemView;
import com.handybook.handybook.helpcenter.model.HelpEvent;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.HelpCenterLog;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpFragment extends ProgressSpinnerFragment {

    @BindView(R.id.native_help_center_layout)
    ViewGroup mNativeHelpCenterLayout;
    @BindView(R.id.recent_booking_actions_layout)
    ViewGroup mRecentBookingActionsLayout;
    @BindView(R.id.fragment_referral_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.help_dynamic_layout)
    ViewGroup mHelpDynamicLayout;
    @BindView(R.id.help_booking_image)
    ImageView mHelpBookingImage;
    @BindView(R.id.recent_booking_date_text)
    TextView mRecentBookingDateText;
    @BindView(R.id.recent_booking_time_text)
    TextView mRecentBookingTimeText;
    @BindView(R.id.report_an_issue_layout)
    ViewGroup mReportAnIssueLayout;
    @BindView(R.id.suggested_actions_layout)
    ViewGroup mSuggestedActionsLayout;

    private String mHelpCenterUrl;
    private Booking mBooking;

    public static HelpFragment newInstance(@Nullable String helpCenterUrl) {
        final HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(BundleKeys.HELP_CENTER_URL, helpCenterUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && !args.isEmpty()) {
            mHelpCenterUrl = args.getString(BundleKeys.HELP_CENTER_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_help, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.help));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressSpinner(true);
        bus.post(new HelpEvent.RequestHelpCenter());
    }

    @Override
    public void onPause() {
        hideProgressSpinner();
        super.onPause();
    }

    @OnClick(R.id.recent_booking_layout)
    public void recentBookingClicked() {
        if (mBooking != null) {
            final String bookingId = mBooking.getId();
            if (!Strings.isNullOrEmpty(bookingId)) {
                bus.post(new LogEvent.AddLogEvent(
                        new HelpCenterLog.BookingDetailsTappedLog(bookingId)));
            }

            Intent intent = new Intent(getContext(), BookingDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BundleKeys.BOOKING, mBooking);
            startActivity(intent);
        }
    }

    @OnClick(R.id.report_an_issue_text)
    public void reportAnIssueClicked() {
        if (mBooking != null) {
            showProgressSpinner(true);

            final String bookingId = mBooking.getId();
            if (!Strings.isNullOrEmpty(bookingId)) {
                bus.post(new LogEvent.AddLogEvent(
                        new HelpCenterLog.ReportIssueTapped(bookingId)));
            }

            dataManager.getBookingMilestones(
                    mBooking.getId(),
                    new DataManager.Callback<JobStatus>() {
                        @Override
                        public void onSuccess(JobStatus status) {
                            hideProgressSpinner();
                            Intent intent = new Intent(getContext(), ReportIssueActivity.class);
                            intent.putExtra(BundleKeys.BOOKING, mBooking);
                            intent.putExtra(BundleKeys.PRO_JOB_STATUS, status);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            hideProgressSpinner();
                            if (!Strings.isNullOrEmpty(error.getMessage())) {
                                showToast(error.getMessage());
                            }
                            else {
                                showToast(R.string.an_error_has_occurred);
                            }
                        }
                    }
            );
        }
    }

    @OnClick(R.id.help_center_layout)
    public void helpCenterOptionClicked() {
        bus.post(new LogEvent.AddLogEvent(new HelpCenterLog.HelpCenterTappedLog()));

        Bundle args = new Bundle();
        args.putString(BundleKeys.HELP_CENTER_URL, mHelpCenterUrl);
        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                HelpWebViewFragment.newInstance(args)
        ).addToBackStack(null).commit();
    }

    @Subscribe
    public void onReceiveHelpCenterSuccess(HelpEvent.ReceiveHelpCenterSuccess event) {
        hideProgressSpinner();
        HelpCenterResponse response = event.helpCenterResponse;
        if (response != null) {
            mBooking = response.getBooking();
            if (mBooking != null) {
                Date bookingStartDate = mBooking.getStartDate();
                if (bookingStartDate != null) {
                    mRecentBookingDateText.setText(
                            DateTimeUtils.DAY_MONTH_DATE_FORMATTER.format(bookingStartDate));

                    String bookingTime = BookingUtil.getFormattedStartTime(mBooking);
                    if(mBooking.shouldHideEndTime()) {
                        mRecentBookingTimeText.setText(bookingTime);
                    }
                    else if (!Strings.isNullOrEmpty(bookingTime)) {
                        /* Show one decimal digit for booking hours
                         when required, Example: 3.0 -> 3, 2.5 - > 2.5 */
                        DecimalFormat df = new DecimalFormat();
                        df.setMinimumFractionDigits(0);
                        df.setMaximumFractionDigits(1);
                        mRecentBookingTimeText.setText(
                                getString(
                                        R.string.help_booking_time_dash_formatted,
                                        bookingTime.toLowerCase(),
                                        df.format(mBooking.getHours())
                                ));
                    }
                }

                if (!mBooking.isMilestonesEnabled()) {
                    mReportAnIssueLayout.setVisibility(View.GONE);
                }
                mHelpBookingImage.setImageResource(
                        BookingUtil.getIconForService(mBooking, BookingUtil.IconType.GRAY));
            }
            else {
                mRecentBookingActionsLayout.setVisibility(View.GONE);
            }

            ArrayList<HelpCenterResponse.Link> links = response.getLinks();
            if (links != null && !links.isEmpty()) {
                mSuggestedActionsLayout.removeAllViews();

                for (HelpCenterResponse.Link link : links) {
                    HelpCenterActionItemView view = new HelpCenterActionItemView(
                            getContext(),
                            bus
                    );
                    view.setDisplay(
                            link.getText(),
                            link.getSubtext(),
                            link.getLink(),
                            link.getIcon()
                    );
                    mSuggestedActionsLayout.addView(view);
                }
            }
            else {
                mSuggestedActionsLayout.setVisibility(View.GONE);
            }
            mNativeHelpCenterLayout.setVisibility(View.VISIBLE);
        }
        else {
            hideDynamicElements();
        }
    }

    @Subscribe
    public void onReceiveHelpCenterError(HelpEvent.ReceiveHelpCenterError event) {
        hideProgressSpinner();
        hideDynamicElements();
    }

    private void hideDynamicElements() {
        mHelpDynamicLayout.setVisibility(View.GONE);
        mNativeHelpCenterLayout.setVisibility(View.VISIBLE);
    }
}
