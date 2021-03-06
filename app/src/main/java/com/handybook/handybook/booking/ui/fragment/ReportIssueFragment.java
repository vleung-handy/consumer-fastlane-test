package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.view.ProMilestoneView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.IssueResolutionLog;
import com.handybook.handybook.logger.handylogger.model.booking.ProContactedLog;
import com.handybook.handybook.proteam.callback.ConversationCallback;
import com.handybook.handybook.proteam.callback.ConversationCallbackWrapper;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class ReportIssueFragment extends ProgressSpinnerFragment
        implements ConversationCallback {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.report_issue_date)
    TextView mDateText;
    @BindView(R.id.report_issue_time)
    TextView mTimeText;
    @BindView(R.id.report_issue_provider)
    TextView mProviderText;
    @BindView(R.id.report_issue_milestones)
    LinearLayout mMilestonesLayout;
    @BindView(R.id.report_issue_links)
    LinearLayout mDeepLinksLayout;

    private Booking mBooking;
    private JobStatus mJobStatus;
    private View.OnClickListener mCallButtonOnClickListener;
    private View.OnClickListener mTextButtonOnClickListener;

    // initialization block
    {
        mCallButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                        EventContext.ISSUE_RESOLUTION, mBooking.getId(), ProContactedLog.PHONE)));
                final String phone = mBooking.getProvider().getPhone();
                Intent intent =
                        new Intent(
                                Intent.ACTION_DIAL,
                                Uri.fromParts("tel", phone, null)
                        );
                Utils.safeLaunchIntent(intent, getContext());
            }
        };

        mTextButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mBooking.getChatOptions() != null &&
                    mBooking.getChatOptions().shouldDirectToInAppChat()) {
                    showProgressSpinner(true);
                    bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                            EventContext.ISSUE_RESOLUTION,
                            mBooking.getId(),
                            ProContactedLog.CHAT
                    )));
                    HandyLibrary.getInstance()
                                .getHandyService()
                                .createConversation(
                                        mBooking.getProvider().getId(),
                                        userManager.getCurrentUser().getAuthToken(),
                                        "",
                                        new ConversationCallbackWrapper(ReportIssueFragment.this)
                                );
                }
                else {
                    bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                            EventContext.ISSUE_RESOLUTION,
                            mBooking.getId(),
                            ProContactedLog.SMS
                    )));
                    final String phone = mBooking.getProvider().getPhone();
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_SENDTO,
                                    Uri.fromParts("sms", phone, null)
                            );
                    Utils.safeLaunchIntent(intent, getContext());
                }
            }
        };
    }

    @Override
    public void onCreateConversationSuccess(@Nullable final String conversationId) {
        hideProgressSpinner();
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, Uri.parse(conversationId));
        intent.putExtra(
                BundleKeys.PRO_MESSAGES_VIEW_MODEL,
                new ProMessagesViewModel(mBooking.getProvider())
        );
        startActivity(intent);
    }

    @Override
    public void onCreateConversationError() {
        hideProgressSpinner();
        showToast(R.string.an_error_has_occurred);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    public static ReportIssueFragment newInstance(
            final Booking booking,
            final JobStatus proStatuses
    ) {
        final ReportIssueFragment fragment = new ReportIssueFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        args.putSerializable(BundleKeys.PRO_JOB_STATUS, proStatuses);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mJobStatus = (JobStatus) getArguments().getSerializable(BundleKeys.PRO_JOB_STATUS);

        bus.post(new LogEvent.AddLogEvent(new IssueResolutionLog.ReportIssueOpened(
                mBooking.getId(),
                getLastMilestoneTitle()
        )));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_report_issue, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.help));
        //Add the booking id to the subtitle
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                                           .setSubtitle(getString(
                                                   R.string.booking_number,
                                                   mBooking.getId()
                                           ));
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        //sometimes, the job status may not be passed in. If not, then fetch it.
        if (mJobStatus == null) {
            dataManager.getBookingMilestones(
                    mBooking.getId(),
                    new FragmentSafeCallback<JobStatus>(ReportIssueFragment.this) {
                        @Override
                        public void onCallbackSuccess(JobStatus status) {
                            mJobStatus = status;
                            initialize();
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            if (!Strings.isNullOrEmpty(error.getMessage())) {
                                showToast(error.getMessage());
                            }
                            else {
                                showToast(R.string.an_error_has_occurred);
                            }
                            //still try to initialize, we'll do our best without the job statuses
                            initialize();
                        }
                    }
            );
        }
        else {
            initialize();
        }
    }

    private void initialize() {
        setHeader();
        setProMilestones();
        setDeepLinks();
    }

    private void setHeader() {
        mDateText.setText(DateTimeUtils.formatDate(mBooking.getStartDate(), "EEEE',' MMM d',' yyyy",
                                                   mBooking.getBookingTimezone()
        ));

        final String startTime = DateTimeUtils.formatDate(mBooking.getStartDate(), "h:mm aaa",
                                                          mBooking.getBookingTimezone()
        ).toLowerCase();
        final String endTime = DateTimeUtils.formatDate(mBooking.getEndDate(), "h:mm aaa",
                                                        mBooking.getBookingTimezone()
        ).toLowerCase();
        mTimeText.setText(getString(R.string.dash_formatted, startTime, endTime));

        mProviderText.setText(mBooking.getProvider().getFirstNameAndLastInitial());
    }

    private void setProMilestones() {
        if (mJobStatus == null) {
            return;
        }
        JobStatus.Milestone[] milestones = mJobStatus.getMilestones();
        if (milestones != null) {
            for (int i = 0; i < milestones.length; ++i) {
                JobStatus.Milestone milestone = milestones[i];
                ProMilestoneView milestoneView = new ProMilestoneView(getContext());
                milestoneView.setDotColor(mJobStatus.getStatusDrawableId(i));
                milestoneView.setTitleText(milestone.getTitle());
                milestoneView.setIsCurrentMilestone(i == milestones.length - 1);
                milestoneView.setBodyText(milestone.getBody());
                if (milestone.getTimestamp() != null) {
                    milestoneView.setTimeText(DateTimeUtils.getTimeWithoutDate(milestone.getTimestamp()));
                }
                if (milestone.getAction() != null) {
                    JobStatus.Action action = milestone.getAction();

                    if (JobStatus.Action.CALL_OR_TEXT.equals(action.getType())
                        && !Strings.isNullOrEmpty(mBooking.getProvider().getPhone())) {
                        milestoneView.setCallAndTextButtonVisibility(View.VISIBLE);
                        milestoneView.setCallButtonOnClickListener(mCallButtonOnClickListener);
                        milestoneView.setTextButtonOnClickListener(mTextButtonOnClickListener);
                    }
                }
                mMilestonesLayout.addView(milestoneView);
            }

            if (!mJobStatus.isComplete()) {
                ProMilestoneView milestoneView = new ProMilestoneView(getContext());
                mMilestonesLayout.addView(milestoneView);
            }

            // Remove the link from last milestone
            ProMilestoneView lastMilestoneView =
                    (ProMilestoneView) mMilestonesLayout.getChildAt(
                            mMilestonesLayout.getChildCount() - 1);
            lastMilestoneView.setLineVisibility(View.INVISIBLE);
        }
    }

    private void setDeepLinks() {
        if (mJobStatus == null) {
            return;
        }
        JobStatus.DeepLinkWrapper[] deepLinkWrappers = mJobStatus.getDeepLinkWrappers();
        if (deepLinkWrappers == null) { return; }

        for (final JobStatus.DeepLinkWrapper deepLinkWrapper : deepLinkWrappers) {
            TextView view = (TextView) LayoutInflater.from(
                    getContext()).inflate(R.layout.text_list_element, mDeepLinksLayout, false);
            view.setText(deepLinkWrapper.getText());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    bus.post(new LogEvent.AddLogEvent(new IssueResolutionLog.HelpLinkTapped(
                            mBooking.getId(),
                            deepLinkWrapper.getText(),
                            deepLinkWrapper.getDeeplink()
                    )));

                    if (JobStatus.DeepLinkWrapper.TYPE_CANCEL.equals(deepLinkWrapper.getType())) {
                        bus.post(new BookingEvent.RequestBookingCancellationData(mBooking.getId()));
                    }
                    else if (JobStatus.DeepLinkWrapper.TYPE_RESCHEDULE.equals(deepLinkWrapper.getType())) {
                        // show reschedule page
                        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBookingSelectedLog(
                                mBooking.getProvider(),
                                mBooking.getId(),
                                mBooking.getRecurringId()
                                 ))
                        );

                        bus.post(new BookingEvent.RequestPreRescheduleInfo(mBooking.getId()));
                    }
                    else if (!Strings.isNullOrEmpty(deepLinkWrapper.getDeeplink())) {
                        Uri uri = Uri.parse(deepLinkWrapper.getDeeplink());
                        Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
                        Utils.safeLaunchIntent(deepLinkIntent, getContext());
                    }
                }
            });
            mDeepLinksLayout.addView(view);
        }
    }

    private String getLastMilestoneTitle() {
        String lastMilestoneTitle = "";
        if (mJobStatus != null) {
            JobStatus.Milestone[] milestones = mJobStatus.getMilestones();
            if (milestones != null && milestones.length > 0) {
                JobStatus.Milestone milestone = milestones[milestones.length - 1];
                if (!Strings.isNullOrEmpty(milestone.getTitle())) {
                    lastMilestoneTitle = milestone.getTitle();
                }
            }
        }

        return lastMilestoneTitle;
    }

    @Subscribe
    public void onReceiveBookingCancellationDataSuccess(
            final BookingEvent.ReceiveBookingCancellationDataSuccess event
    ) {
        hideProgressSpinner();
        BookingCancellationData bookingCancellationData = event.result;
        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        intent.putExtra(BundleKeys.BOOKING_CANCELLATION_DATA, bookingCancellationData);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceiveBookingCancellationDataError(
            final BookingEvent.ReceiveBookingCancellationDataError event
    ) {
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(BookingEvent.ReceivePreRescheduleInfoSuccess event) {
        hideProgressSpinner();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.NORMAL);

        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(BookingEvent.ReceivePreRescheduleInfoError event) {
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
