package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.ui.view.GroovedTimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingDateFragment extends BookingFlowFragment
{
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_NOTICE = "com.handy.handy.EXTRA_RESCHEDULE_NOTICE";
    private static final String STATE_RESCHEDULE_DATE = "RESCHEDULE_DATE";
    private final int MINUTE_INTERVAL = 30;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.date_picker)
    DatePicker mDatePicker;
    @Bind(R.id.time_picker)
    GroovedTimePicker mGroovedTimePicker;
    @Bind(R.id.nav_text)
    TextView mNavTextView;
    @Bind(R.id.notice_text)
    TextView mNoticeTextView;
    private ArrayList<BookingOption> mBookingOptions;
    private Booking mRescheduleBooking;
    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            updateRequestDate(mDatePicker);
            if (mRescheduleBooking != null)
            {
                final Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                date.set(Calendar.MONTH, mDatePicker.getMonth());
                date.set(Calendar.YEAR, mDatePicker.getYear());
                date.set(Calendar.HOUR_OF_DAY, mGroovedTimePicker.getCurrentHour());
                date.set(Calendar.MINUTE, mGroovedTimePicker.getCurrentMinute());
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                if (mRescheduleBooking.isRecurring())
                {
                    final Intent intent = new Intent(
                            getActivity(),
                            BookingRescheduleOptionsActivity.class
                    );
                    intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mRescheduleBooking);
                    intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTimeInMillis());
                    startActivityForResult(intent, ActivityResult.RESULT_RESCHEDULE_NEW_DATE);
                } else
                {
                    rescheduleBooking(mRescheduleBooking, date.getTime(), false);
                }
            } else if (mBookingOptions != null && mBookingOptions.size() > 0)
            {
                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(
                        BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(mBookingOptions)
                );
                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, mBookingOptions.get(0).getPage());
                intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                startActivity(intent);
            } else
            {
                continueBookingFlow();
            }
        }
    };
    private Date mRescheduleDate;
    private String mNotice;

    public static BookingDateFragment newInstance(final ArrayList<BookingOption> postOptions)
    {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookingDateFragment newInstance(final Booking rescheduleBooking, final String notice)
    {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putString(EXTRA_RESCHEDULE_NOTICE, notice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackTime();
        mRescheduleBooking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        if (mRescheduleBooking != null)
        {
            if (savedInstanceState != null)
            {
                mRescheduleDate = new Date(savedInstanceState.getLong(STATE_RESCHEDULE_DATE, 0));
            } else
            {
                mRescheduleDate = mRescheduleBooking.getStartDate();
            }
            mNotice = getArguments().getString(EXTRA_RESCHEDULE_NOTICE);
            // flash notice since it may not initially appear in view
            if (savedInstanceState == null && mNotice != null)
            {
                toast.setText(mNotice);
                toast.show();
            }
        } else
        {
            mBookingOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);
        }
    }

    @Override
    public final void onResume()
    {
        super.onResume();
        final Calendar startDate = currentStartDate();
        mDatePicker.updateDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH));
        mGroovedTimePicker.setCurrentHour(startDate.get(Calendar.HOUR_OF_DAY));
        mGroovedTimePicker.setCurrentMinute(startDate.get(Calendar.MINUTE) / MINUTE_INTERVAL);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mNextButton.setClickable(true);
    }


    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
            final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESULT_RESCHEDULE_NEW_DATE)
        {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            getActivity().setResult(ActivityResult.RESULT_RESCHEDULE_NEW_DATE, intent);
            getActivity().finish();
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_date, container, false);
        ButterKnife.bind(this, view);
        mGroovedTimePicker.setInterval(MINUTE_INTERVAL);
        if (mRescheduleBooking != null)
        {
            mNavTextView.setText(getString(R.string.reschedule));
            mNextButton.setText(getString(R.string.reschedule));
            if (mNotice != null)
            {
                mNoticeTextView.setText(mNotice);
                mNoticeTextView.setVisibility(View.VISIBLE);
            }
        }
        final Calendar startDate = currentStartDate();
        mDatePicker.init(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener()
                {
                    @Override
                    public void onDateChanged(
                            final DatePicker view,
                            final int year,
                            final int monthOfYear,
                            final int dayOfMonth
                    )
                    {
                        updateRequestDate(view);
                    }
                });

        // resolves issue https://code.google.com/p/android/issues/detail?id=22754
        mGroovedTimePicker.setSaveFromParentEnabled(false);
        mGroovedTimePicker.setSaveEnabled(true);
        mGroovedTimePicker.setCurrentHour(startDate.get(Calendar.HOUR_OF_DAY));
        mGroovedTimePicker.setCurrentMinute(startDate.get(Calendar.MINUTE) / MINUTE_INTERVAL);
        mGroovedTimePicker.setOnTimeChangedListener(
                new TimePicker.OnTimeChangedListener()
                {
                    @Override
                    public void onTimeChanged(final TimePicker view, final int hourOfDay,
                            final int minute)
                    {
                        updateRequestDate(mDatePicker);
                    }
                });
        // subtracting 1s to avoid illegal state exception being thrown
        final Calendar today = Calendar.getInstance();
        mDatePicker.setMinDate(today.getTimeInMillis() - 1000);
        // set max date to one year from today
        today.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
        mDatePicker.setMaxDate(today.getTimeInMillis());
        mNextButton.setOnClickListener(nextClicked);
        updateRequestDate(mDatePicker);
        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mRescheduleDate != null)
        {
            outState.putLong(STATE_RESCHEDULE_DATE, mRescheduleDate.getTime());
        }
    }

    private Calendar currentStartDate()
    {
        if (mRescheduleBooking != null)
        {
            final Calendar startDate = Calendar.getInstance();
            startDate.setTime(mRescheduleDate);
            return startDate;
        }
        final Calendar cal = Calendar.getInstance();
        final Date requestDate = bookingManager.getCurrentRequest().getStartDate();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        Date tranDate = null;
        if (transaction != null)
        {
            tranDate = transaction.getStartDate();
        }
        final Date startDate = tranDate != null ? tranDate : requestDate;
        //TODO fix issue when going back for surge and date changes to initial date
        if (startDate != null)
        {
            cal.setTime(startDate);
        } else
        {
            // initialize date 3 days ahead with random time between 10a - 5p
            final Random random = new Random();
            cal.set(Calendar.HOUR_OF_DAY, random.nextInt(8) + 10);
            cal.set(Calendar.MINUTE, 0);
            cal.add(Calendar.DATE, 3);
            // if suggested day is on a weekend, suggest new date during the following week
            final int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.FRIDAY || day == Calendar.SATURDAY || day == Calendar.SUNDAY)
            {
                if (day != Calendar.SUNDAY)
                {
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                }
                cal.set(Calendar.DAY_OF_WEEK, random.nextInt(4) + 2);
            }
        }
        return cal;
    }

    private void updateRequestDate(final DatePicker datePicker)
    {
        //this function can be called after butterknife unbinds the views
        //TODO: need to prevent listener from being called when view is unbound
        //below line is needed to prevent NPE caused by above issue
        if (datePicker == null || mGroovedTimePicker == null) return;

        final Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        date.set(Calendar.MONTH, datePicker.getMonth());
        date.set(Calendar.YEAR, datePicker.getYear());
        date.set(Calendar.HOUR_OF_DAY, mGroovedTimePicker.getCurrentHour());
        date.set(Calendar.MINUTE, mGroovedTimePicker.getCurrentMinute());
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        final Date newDate = date.getTime();
        if (mRescheduleBooking != null)
        {
            mRescheduleDate = newDate;
        }
        else
        {
            final BookingRequest request = bookingManager.getCurrentRequest();
            request.setStartDate(newDate);
            final BookingTransaction transaction = bookingManager.getCurrentTransaction();
            if (transaction != null)
            {
                transaction.setStartDate(newDate);
            }
        }
    }
}
