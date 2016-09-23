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
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.helpcenter.model.HelpEvent;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.model.response.HelpCenterResponse;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.view.HelpCenterActionItemView;
import com.handybook.handybook.util.BookingUtil;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HelpFragment extends InjectedFragment
{
    @Bind(R.id.native_help_center_layout)
    ViewGroup mNativeHelpCenterLayout;
    @Bind(R.id.recent_booking_actions_layout)
    ViewGroup mRecentBookingActionsLayout;
    @Bind(R.id.fragment_referral_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.help_dynamic_layout)
    ViewGroup mHelpDynamicLayout;
    @Bind(R.id.help_booking_image)
    ImageView mHelpBookingImage;
    @Bind(R.id.recent_booking_date_text)
    TextView mRecentBookingDateText;
    @Bind(R.id.recent_booking_time_text)
    TextView mRecentBookingTimeText;
    @Bind(R.id.report_an_issue_layout)
    ViewGroup mReportAnIssueLayout;
    @Bind(R.id.suggested_actions_layout)
    ViewGroup mSuggestedActionsLayout;

    private String mHelpCenterUrl;
    private Booking mBooking;

    public static HelpFragment newInstance(@Nullable String helpCenterUrl)
    {
        final HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(BundleKeys.HELP_CENTER_URL, helpCenterUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && !args.isEmpty())
        {
            mHelpCenterUrl = args.getString(BundleKeys.HELP_CENTER_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.help));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HelpEvent.RequestHelpCenter());
    }

    @OnClick(R.id.recent_booking_layout)
    public void recentBookingClicked()
    {
        if (mBooking != null)
        {
            Intent intent = new Intent(getContext(), BookingDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BundleKeys.BOOKING, mBooking);
            startActivity(intent);
        }
    }

    @OnClick(R.id.report_an_issue_text)
    public void reportAnIssueClicked()
    {
        if (mBooking != null)
        {
            showUiBlockers();
            dataManager.getBookingMilestones(mBooking.getId(), new DataManager.Callback<JobStatus>()
                    {
                        @Override
                        public void onSuccess(JobStatus status)
                        {
                            removeUiBlockers();
                            Intent intent = new Intent(getContext(), ReportIssueActivity.class);
                            intent.putExtra(BundleKeys.BOOKING, mBooking);
                            intent.putExtra(BundleKeys.PRO_JOB_STATUS, status);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            removeUiBlockers();
                            if (!Strings.isNullOrEmpty(error.getMessage()))
                            {
                                showToast(error.getMessage());
                            }
                            else
                            {
                                showToast(R.string.an_error_has_occurred);
                            }
                        }
                    }
            );
        }
    }

    @OnClick(R.id.help_center_layout)
    public void helpCenterOptionClicked()
    {
        Bundle args = new Bundle();
        args.putString(BundleKeys.HELP_CENTER_URL, mHelpCenterUrl);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                HelpWebViewFragment.newInstance(args)).addToBackStack(null).commit();
    }

    @Subscribe
    public void onReceiveHelpCenterSuccess(HelpEvent.ReceiveHelpCenterSuccess event)
    {
        removeUiBlockers();
        HelpCenterResponse response = event.helpCenterResponse;
        if (response != null)
        {
            mBooking = response.getBooking();
            if (mBooking != null)
            {
                Date bookingStartDate = mBooking.getStartDate();
                if (bookingStartDate != null)
                {
                    mRecentBookingDateText.setText(
                            DateTimeUtils.DAY_MONTH_DATE_FORMATTER.format(bookingStartDate));

                    String bookingTime = DateTimeUtils.LOCAL_TIME_12_HOURS_FORMATTER
                            .format(bookingStartDate);
                    if (!Strings.isNullOrEmpty(bookingTime))
                    {
                        /* Show one decimal digit for booking hours
                         when required, Example: 3.0 -> 3, 2.5 - > 2.5 */
                        DecimalFormat df = new DecimalFormat();
                        df.setMinimumFractionDigits(0);
                        df.setMaximumFractionDigits(1);
                        mRecentBookingTimeText.setText(
                                getString(R.string.help_booking_time_dash_formatted,
                                        bookingTime.toLowerCase(),
                                        df.format(mBooking.getHours())));
                    }
                }

                if (!mBooking.isMilestonesEnabled())
                {
                    mReportAnIssueLayout.setVisibility(View.GONE);
                }
                mHelpBookingImage.setImageResource(
                        BookingUtil.getIconForService(mBooking, BookingUtil.IconType.GRAY));
            }
            else
            {
                mRecentBookingActionsLayout.setVisibility(View.GONE);
            }

            ArrayList<HelpCenterResponse.Link> links = response.getLinks();
            if (links != null && !links.isEmpty())
            {
                for (HelpCenterResponse.Link link : links)
                {
                    HelpCenterActionItemView view = new HelpCenterActionItemView(getContext());
                    view.setDisplay(link.getText(), link.getSubtext(), link.getLink(), link.getIcon());
                    mSuggestedActionsLayout.addView(view);
                }
            }
            else
            {
                mSuggestedActionsLayout.setVisibility(View.GONE);
            }
            mNativeHelpCenterLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            hideDynamicElements();
        }
    }

    @Subscribe
    public void onReceiveHelpCenterError(HelpEvent.ReceiveHelpCenterError event)
    {
        removeUiBlockers();
        hideDynamicElements();
    }

    private void hideDynamicElements()
    {
        mHelpDynamicLayout.setVisibility(View.GONE);
        mNativeHelpCenterLayout.setVisibility(View.VISIBLE);
    }
}
