package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.proteam.ui.view.ProTeamProItemView;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingDateFragment extends BookingFlowFragment
        implements BookingDateTimeInputFragment.OnSelectedDateTimeUpdatedListener {

    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_NOTICE = "com.handy.handy.EXTRA_RESCHEDULE_NOTICE";
    static final String EXTRA_RESCHEDULE_TYPE = "com.handy.handy.EXTRA_RESCHEDULE_TYPE";
    static final String EXTRA_PROVIDER_ID = "com.handy.handy.EXTRA_PROVIDER_ID";
    static final String EXTRA_PRO_TEAM_PRO = "com.handy.handy.EXTRA_PRO_TEAM_PRO";
    private static final String STATE_RESCHEDULE_DATE = "RESCHEDULE_DATE";
    private static final String STATE_RESCHEDULE_IS_INSTANT_BOOK_ENABLED
            = "RESCHEDULE_INSTANT_BOOK";

    @BindView(R.id.next_button)
    Button mNextButton;

    @BindView(R.id.notice_text)
    TextView mNoticeTextView;

    @BindView(R.id.location_text)
    TextView mLocationText;

    @BindView(R.id.selected_pro_container)
    ViewGroup mSelectedProContainer;

    @BindView(R.id.reschedule_cancel_text)
    TextView mRescheduleCancelText;

    private Date mSelectedDateTime;

    private ArrayList<BookingOption> mBookingOptions;
    private Booking mRescheduleBooking;
    private ProAvailabilityResponse mProAvailability;
    private Date mRescheduleDate;
    private boolean mRescheduleInstantBookEnabled;
    private String mNotice;
    private BookingDetailFragment.RescheduleType mRescheduleType;
    private String mProviderId;
    private ProTeamProViewModel mProTeamProViewModel;
    private final View.OnClickListener mNextClicked;

    {
        mNextClicked = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mRescheduleBooking != null) {
                    //we only do recurring reschedules if not coming from chat.
                    if ((mRescheduleBooking.isRecurring()
                         && (mRescheduleType == null ||
                             mRescheduleType != BookingDetailFragment.RescheduleType.FROM_CHAT))) {
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingRescheduleOptionsActivity.class
                        );
                        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mRescheduleBooking);
                        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, mRescheduleType);
                        intent.putExtra(
                                BundleKeys.RESCHEDULE_IS_INSTANT_BOOK_ENABLED,
                                mRescheduleInstantBookEnabled
                        );
                        intent.putExtra(
                                BundleKeys.RESCHEDULE_NEW_DATE,
                                mSelectedDateTime.getTime()
                        );
                        intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
                        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
                    }
                    else {
                        rescheduleBooking(
                                mRescheduleBooking,
                                mSelectedDateTime,
                                false,
                                mProviderId,
                                mRescheduleType,
                                null,
                                mRescheduleInstantBookEnabled
                        );
                    }
                }
                else {
                    BookingRequest bookingRequest = bookingManager.getCurrentRequest();
                    if (bookingRequest != null) {
                        Date dateStart = bookingRequest.getStartDate();
                        String timezone = bookingRequest.getTimeZone();
                        if (dateStart != null && !Strings.isNullOrEmpty(timezone)) {
                            String dateString = DateTimeUtils.formatDate(
                                    dateStart,
                                    DateTimeUtils.UNIVERSAL_DATE_FORMAT,
                                    timezone
                            );
                            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingSchedulerSubmittedLog(
                                    dateString)));
                        }

                    }

                    if (mBookingOptions != null && mBookingOptions.size() > 0) {
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingOptionsActivity.class
                        );
                        intent.putExtras(createProgressBundle());
                        intent.putParcelableArrayListExtra(
                                BookingOptionsActivity.EXTRA_OPTIONS,
                                new ArrayList<>(mBookingOptions)
                        );
                        intent.putExtra(
                                BookingOptionsActivity.EXTRA_PAGE,
                                mBookingOptions.get(0).getPage()
                        );
                        intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                        startActivity(intent);
                    }
                    else {
                        continueBookingFlow();
                    }
                }

            }
        };
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

    public static BookingDateFragment newInstance(
            final ArrayList<BookingOption> postOptions,
            @Nullable final Bundle extras
    ) {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        if (extras != null) {
            args.putAll(extras);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public static BookingDateFragment newInstance(
            final Booking rescheduleBooking,
            final String notice,
            BookingDetailFragment.RescheduleType type,
            final String providerId,
            final ProTeamProViewModel proTeamProViewModel,
            final ProAvailabilityResponse availabilityResponse,
            @Nullable final Bundle extras
    ) {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putString(EXTRA_RESCHEDULE_NOTICE, notice);
        args.putSerializable(EXTRA_RESCHEDULE_TYPE, type);
        args.putString(EXTRA_PROVIDER_ID, providerId);
        args.putSerializable(EXTRA_PRO_TEAM_PRO, proTeamProViewModel);
        args.putSerializable(BundleKeys.PRO_AVAILABILITY, availabilityResponse);
        if (extras != null) {
            args.putAll(extras);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRescheduleBooking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        if (mRescheduleBooking != null) {
            if (savedInstanceState != null) {
                mRescheduleDate = new Date(savedInstanceState.getLong(STATE_RESCHEDULE_DATE, 0));
                mRescheduleInstantBookEnabled = savedInstanceState.getBoolean(
                        STATE_RESCHEDULE_IS_INSTANT_BOOK_ENABLED,
                        false
                );
            }
            else {
                mRescheduleDate = mRescheduleBooking.getStartDate();
            }
            mNotice = getArguments().getString(EXTRA_RESCHEDULE_NOTICE);
            mProviderId = getArguments().getString(EXTRA_PROVIDER_ID);
            mProTeamProViewModel =
                    (ProTeamProViewModel) getArguments().getSerializable(EXTRA_PRO_TEAM_PRO);

            mRescheduleType = (BookingDetailFragment.RescheduleType)
                    getArguments().getSerializable(EXTRA_RESCHEDULE_TYPE);

            mProAvailability = (ProAvailabilityResponse)
                    getArguments().getSerializable(BundleKeys.PRO_AVAILABILITY);

            EventLog eventLog;

            if (mRescheduleType == BookingDetailFragment.RescheduleType.FROM_CHAT) {
                eventLog = new ChatLog.RescheduleDatePickerShownLog(
                        mProviderId,
                        mRescheduleBooking.getId(),
                        mRescheduleDate,
                        mRescheduleType
                );
            }
            else {
                eventLog = new BookingDetailsLog.RescheduleDatePickerShownLog(
                        mProviderId,
                        mRescheduleBooking.getId(),
                        mRescheduleDate,
                        mRescheduleType
                );
            }
            bus.post(new LogEvent.AddLogEvent(eventLog));
        }
        else {
            mBookingOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingSchedulerShownLog()));
        }

    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    @Override
    public final void onActivityResult(
            final int requestCode, final int resultCode,
            final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
            getActivity().finish();
        }
        else if (resultCode == ActivityResult.BOOKING_CANCELED) {
            getActivity().setResult(ActivityResult.BOOKING_CANCELED, new Intent());
            getActivity().finish();
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_date, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.time));

        if (mRescheduleBooking != null) {
            mNextButton.setText(getString(R.string.reschedule));

            if (mRescheduleType != null &&
                mRescheduleType == BookingDetailFragment.RescheduleType.FROM_CANCELATION) {
                //this is the reschedule flow from cancelation
                //log that we are here.
                bus.post(new LogEvent.AddLogEvent(
                        new BookingDetailsLog.RescheduleBookingSelectedLog(
                                mRescheduleBooking.getProvider(),
                                mRescheduleBooking.getId(),
                                mRescheduleBooking.getRecurringId()
                        )
                ));

                setToolbarTitle(getString(R.string.reschedule_instead));
                mLocationText.setText(getString(R.string.reschedule_instead_of_canceling));
                mRescheduleCancelText.setVisibility(View.VISIBLE);
            }
            else {
                String toolbarTitle = getString(R.string.reschedule);
                if (mProTeamProViewModel != null) {
                    mSelectedProContainer.setVisibility(View.VISIBLE);
                    mSelectedProContainer.removeAllViews();
                    final ProTeamProItemView selectedProView =
                            new ProTeamProItemView(
                                    getActivity(),
                                    true,
                                    mRescheduleBooking.getProvider() != null
                                    ? mRescheduleBooking.getProvider().getId()
                                    : null
                            );
                    selectedProView.bind(mProTeamProViewModel);
                    final String proFirstName = mProTeamProViewModel.getProTeamPro().getFirstName();
                    mSelectedProContainer.addView(selectedProView);
                    mNextButton.setText(getString(R.string.request_pro_formatted, proFirstName));
                    toolbarTitle = getString(R.string.pro_x_availability_formatted, proFirstName);
                }
                setToolbarTitle(toolbarTitle);
                mRescheduleCancelText.setVisibility(View.GONE);
            }
            if (mNotice != null) {
                mNoticeTextView.setText(mNotice);
                mNoticeTextView.setVisibility(View.VISIBLE);
            }
        }
        initializeDateTimeInput();
        mNextButton.setOnClickListener(mNextClicked);
        return view;
    }

    private void initializeDateTimeInput() {
        final Calendar startDateTime = getInitialStartDateTimeWithTimeZone();
        BookingDateTimeInputFragment bookingDateTimeInputFragment
                = BookingDateTimeInputFragment.newInstance(
                startDateTime,
                DateTimeUtils.DEFAULT_DATE_DISPLAY_PATTERN,
                //not using device defaults because the format is too long
                mProAvailability
        );
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(
                R.id.booking_date_time_input_fragment_container,
                bookingDateTimeInputFragment,
                BookingTimeInputDialogFragment.TAG
        );
        transaction.commit();
        updateBookingRequestDateTime(startDateTime.getTime(), false);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRescheduleDate != null) {
            outState.putLong(STATE_RESCHEDULE_DATE, mRescheduleDate.getTime());
            outState.putBoolean(
                    STATE_RESCHEDULE_IS_INSTANT_BOOK_ENABLED,
                    mRescheduleInstantBookEnabled
            );
        }
    }

    private Calendar getInitialStartDateTimeWithTimeZone() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(getBookingTimeZone());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        /*
        just in case the device default time format that we use
        specifies to display seconds or milliseconds
         */

        if (mRescheduleBooking != null) {
            cal.setTime(mRescheduleDate);
            return cal;
        }

        final Date requestDate = bookingManager.getCurrentRequest().getStartDate();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        Date tranDate = null;
        if (transaction != null) {
            tranDate = transaction.getStartDate();
        }
        final Date startDate = tranDate != null ? tranDate : requestDate;
        //TODO fix issue when going back for surge and date changes to initial date
        if (startDate != null) {
            cal.setTime(startDate);
        }
        else {
            // initialize date 3 days ahead with random time between 10a - 5p
            final Random random = new Random();
            cal.set(Calendar.HOUR_OF_DAY, random.nextInt(8) + 10);
            cal.set(Calendar.MINUTE, 0);
            cal.add(Calendar.DATE, 3);
            // if suggested day is on a weekend, suggest new date during the following week
            final int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.FRIDAY || day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                if (day != Calendar.SUNDAY) {
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                }
                cal.set(Calendar.DAY_OF_WEEK, random.nextInt(4) + 2);
            }
        }
        return cal;
    }

    @NonNull
    private TimeZone getBookingTimeZone() {
        if (mRescheduleBooking != null &&
            !TextUtils.isEmpty(mRescheduleBooking.getBookingTimezone())) {
            return TimeZone.getTimeZone(mRescheduleBooking.getBookingTimezone());
        }
        if (bookingManager.getCurrentRequest() != null &&
            !TextUtils.isEmpty(bookingManager.getCurrentRequest().getTimeZone())) {
            return TimeZone.getTimeZone(bookingManager.getCurrentRequest().getTimeZone());
        }
        return TimeZone.getDefault();
    }

    /**
     * updates the current booking request and transaction
     * with the selected date time input
     * for the booking's timezone
     */
    private void updateBookingRequestDateTime(
            final Date selectedDateTime,
            boolean isInstantBookEnabled
    ) {
        if (mRescheduleBooking != null) {
            mRescheduleDate = selectedDateTime;
            mRescheduleInstantBookEnabled = isInstantBookEnabled;
        }
        else {
            final BookingRequest request = bookingManager.getCurrentRequest();
            if (request != null) {
                request.setStartDate(selectedDateTime);
            }
            final BookingTransaction transaction = bookingManager.getCurrentTransaction();
            if (transaction != null) {
                transaction.setStartDate(selectedDateTime);
            }
        }
        mSelectedDateTime = selectedDateTime;
    }

    /**
     * Request to process cancelation success.
     *
     * @param event
     */
    @Subscribe
    public void onReceivePreCancelationInfoSuccess(BookingEvent.ReceiveBookingCancellationDataSuccess event) {
        hideProgressSpinner();
        BookingCancellationData bookingCancellationData = event.result;

        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        intent.putExtra(BundleKeys.BOOKING, mRescheduleBooking);
        intent.putExtra(BundleKeys.BOOKING_CANCELLATION_DATA, bookingCancellationData);
        startActivityForResult(intent, ActivityResult.BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancelationInfoError(BookingEvent.ReceiveBookingCancellationDataError event) {
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    /**
     * User clicks on the option to cancel booking anyways.
     */
    @OnClick(R.id.reschedule_cancel_text)
    public void cancelClicked() {
        showProgressSpinner(true);
        if (mRescheduleBooking != null) {
            bus.post(new LogEvent.AddLogEvent(
                    new BookingDetailsLog.ContinueSkipSelected(mRescheduleBooking.getId())
            ));
            bus.post(new BookingEvent.RequestBookingCancellationData(mRescheduleBooking.getId()));
        }
    }

    @Override
    public void onSelectedDateTimeUpdatedListener(
            Calendar selectedDateTime,
            boolean isInstantBookEnabled
    ) {
        updateBookingRequestDateTime(selectedDateTime.getTime(), isInstantBookEnabled);
    }
}
