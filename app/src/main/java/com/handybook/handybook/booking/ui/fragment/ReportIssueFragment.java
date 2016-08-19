package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.view.ProMilestoneView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ReportIssueFragment extends InjectedFragment
{
    private static final int MINIMUM_MILESTONES = 3;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.report_issue_date)
    TextView mDateText;
    @Bind(R.id.report_issue_time)
    TextView mTimeText;
    @Bind(R.id.report_issue_provider)
    TextView mProviderText;
    @Bind(R.id.report_issue_milestones)
    LinearLayout mMilestonesLayout;
    @Bind(R.id.report_issue_links)
    LinearLayout mDeepLinksLayout;

    private Booking mBooking;
    private JobStatus mJobStatus;

    public static ReportIssueFragment newInstance(final Booking booking, final JobStatus proStatuses)
    {
        final ReportIssueFragment fragment = new ReportIssueFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        args.putSerializable(BundleKeys.PRO_JOB_STATUS, proStatuses);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mJobStatus = (JobStatus) getArguments().getSerializable(BundleKeys.PRO_JOB_STATUS);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_report_issue, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.help));
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        setHeader();
        setProMilestones();
        setDeepLinks();
    }

    private void setHeader()
    {
        mDateText.setText(DateTimeUtils.formatDate(mBooking.getStartDate(), "EEEE',' MMM d',' yyyy",
                mBooking.getBookingTimezone()));

        final String startTime = DateTimeUtils.formatDate(mBooking.getStartDate(), "h:mm aaa",
                mBooking.getBookingTimezone());
        final String endTime = DateTimeUtils.formatDate(mBooking.getEndDate(), "h:mm aaa",
                mBooking.getBookingTimezone());
        mTimeText.setText(getString(R.string.dash_formatted, startTime, endTime));

        mProviderText.setText(mBooking.getProvider().getFullName());
    }

    private void setProMilestones()
    {
        JobStatus.Milestone[] milestones = mJobStatus.getMilestones();
        if (milestones != null)
        {
            for (final JobStatus.Milestone milestone : milestones)
            {
                ProMilestoneView milestoneView = new ProMilestoneView(getContext());
                milestoneView.setDotColor(milestone.getStatusColorDrawableId());
                milestoneView.setTitleText(milestone.getTitle());
                milestoneView.setBodyText(milestone.getBody());
                if (milestone.getAction() != null)
                {
                    JobStatus.Action action = milestone.getAction();

                    if (JobStatus.Action.CALL_OR_TEXT.equals(action.getType()))
                    {
                        final String phone = mBooking.getProvider().getPhone();
                        milestoneView.setCallAndTextButtonVisibility(View.VISIBLE);
                        milestoneView.setCallButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                Utils.safeLaunchIntent(intent, getContext());

                            }
                        });
                        milestoneView.setTextButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", phone, null));
                                Utils.safeLaunchIntent(intent, getContext());

                            }
                        });
                    }
                }
                mMilestonesLayout.addView(milestoneView);
            }

            // There should be at least 3 milestones. We will add blank ones if missing.
            // Eventually, there should be a boolean value from server to tell us if the task is completed or not.
            int milestoneCount = milestones.length;
            while (milestoneCount < MINIMUM_MILESTONES)
            {
                ProMilestoneView milestoneView = new ProMilestoneView(getContext());
                mMilestonesLayout.addView(milestoneView);
                ++milestoneCount;
            }
            ProMilestoneView lastMilestoneView =
                    (ProMilestoneView) mMilestonesLayout.getChildAt(mMilestonesLayout.getChildCount() - 1);
            lastMilestoneView.setLineVisibility(View.INVISIBLE);
        }
    }

    private void setDeepLinks()
    {
        JobStatus.DeepLinkWrapper[] deepLinkWrappers = mJobStatus.getDeepLinkWrappers();
        if (deepLinkWrappers == null) { return; }

        for (final JobStatus.DeepLinkWrapper deepLinkWrapper : deepLinkWrappers)
        {
            TextView view = (TextView) LayoutInflater.from(
                    getContext()).inflate(R.layout.text_list_element, mDeepLinksLayout, false);
            view.setText(deepLinkWrapper.getText());
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (JobStatus.DeepLinkWrapper.TYPE_CANCEL.equals(deepLinkWrapper.getType()))
                    {
                        // show cancel page
                        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.SkipBooking(
                                BookingDetailsLog.EventType.SELECTED,
                                mBooking.getId())
                        ));

                        bus.post(new BookingEvent.RequestPreCancelationInfo(mBooking.getId()));
                    }
                    else if (JobStatus.DeepLinkWrapper.TYPE_RESCHEDULE.equals(deepLinkWrapper.getType()))
                    {
                        // show reschedule page
                        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBooking(
                                BookingDetailsLog.EventType.SELECTED,
                                mBooking.getId(),
                                mBooking.getStartDate(),
                                null))
                        );

                        bus.post(new BookingEvent.RequestPreRescheduleInfo(mBooking.getId()));
                    }
                    else if (!Strings.isNullOrEmpty(deepLinkWrapper.getDeeplink()))
                    {
                        // should parse deeplink and fallback url
                        Uri uri = Uri.parse(deepLinkWrapper.getDeeplink());
                        Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
                        Utils.safeLaunchIntent(deepLinkIntent, getContext());
                    }
                }
            });
            mDeepLinksLayout.addView(view);
        }
    }

    @Subscribe
    public void onReceivePreCancellationInfoSuccess(BookingEvent.ReceivePreCancelationInfoSuccess event)
    {
        removeUiBlockers();

        Pair<String, List<String>> result = event.result;

        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        if (result.second != null)
        {
            intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>(result.second));
        }
        else
        {
            intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>());
        }
        intent.putExtra(BundleKeys.NOTICE, result.first);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancellationInfoError(BookingEvent.ReceivePreCancelationInfoError event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(BookingEvent.ReceivePreRescheduleInfoSuccess event)
    {
        removeUiBlockers();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.NORMAL);

        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(BookingEvent.ReceivePreRescheduleInfoError event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
