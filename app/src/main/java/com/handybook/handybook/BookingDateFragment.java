package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDateFragment extends BookingFlowFragment {
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_NOTICE = "com.handy.handy.EXTRA_RESCHEDULE_NOTICE";
    private final int MINUTE_INTERVAL = 15;

    private ArrayList<BookingOption> postOptions;
    private List<String> displayedMinuteValues;
    private Booking rescheduleBooking;
    private String notice;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.date_picker) DatePicker datePicker;
    @InjectView(R.id.time_picker) TimePicker timePicker;
    @InjectView(R.id.nav_text) TextView navText;
    @InjectView(R.id.notice_text) TextView noticeText;

    static BookingDateFragment newInstance(final ArrayList<BookingOption> postOptions) {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        fragment.setArguments(args);

        return fragment;
    }

    static BookingDateFragment newInstance(final Booking rescheduleBooking, final String notice) {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();

        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putString(EXTRA_RESCHEDULE_NOTICE, notice);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rescheduleBooking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        if (rescheduleBooking != null) {
            notice = getArguments().getString(EXTRA_RESCHEDULE_NOTICE);

            // flash notice since it may not initially appear in view
            if (savedInstanceState == null && notice != null) {
                toast.setText(notice);
                toast.show();
            }
        }
        else postOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);

        displayedMinuteValues = new ArrayList<>();
        for (int i = 0; i < 60; i += MINUTE_INTERVAL) displayedMinuteValues.add(String.format("%02d", i));
        for (int i = 0; i < 60; i += MINUTE_INTERVAL) displayedMinuteValues.add(String.format("%02d", i));
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_date,container, false);

        ButterKnife.inject(this, view);

        if (rescheduleBooking != null) {
            navText.setText(getString(R.string.reschedule));
            nextButton.setText(getString(R.string.reschedule));

            if (notice != null) {
                noticeText.setText(notice);
                noticeText.setVisibility(View.VISIBLE);
            }
        }

        final Calendar startDate = currentStartDate();

        datePicker.init(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
            startDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                    updateRequestDate();
                }
        });

        // set minutes picker to 15 min intervals
        try {
            final Class<?> classForid = Class.forName("com.android.internal.R$id");
            final Field field = classForid.getField("minute");

            final NumberPicker minutePicker
                    = (NumberPicker)timePicker.findViewById(field.getInt(null));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(7);

            minutePicker.setDisplayedValues(displayedMinuteValues
                    .toArray(new String[displayedMinuteValues.size()]));

        } catch (Exception e) {}

        timePicker.setCurrentHour(startDate.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(startDate.get(Calendar.MINUTE) / MINUTE_INTERVAL);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(final TimePicker view, final int hourOfDay, final int minute) {
                updateRequestDate();
            }
        });

        // subtracting 1s to avoid illegal state excpetion being thrown
        final Calendar today = Calendar.getInstance();
        datePicker.setMinDate(today.getTimeInMillis() - 1000);

        // set max date to one year from today
        today.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
        datePicker.setMaxDate(today.getTimeInMillis());

        nextButton.setOnClickListener(nextClicked);
        updateRequestDate();
        return view;
    }

    @Override
    public final void onResume() {
        super.onResume();

        final Calendar startDate = currentStartDate();
        datePicker.updateDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH));

        timePicker.setCurrentHour(startDate.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(startDate.get(Calendar.MINUTE) / MINUTE_INTERVAL);
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private Calendar currentStartDate() {
        if (rescheduleBooking != null) {
            final Calendar startDate = Calendar.getInstance();
            startDate.setTime(rescheduleBooking.getStartDate());
            return startDate;
        }

        final Calendar cal = Calendar.getInstance();
        final Date requestDate = bookingManager.getCurrentRequest().getStartDate();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        Date tranDate = null;

        if (transaction != null) tranDate = transaction.getStartDate();
        final Date startDate = tranDate != null ? tranDate : requestDate;

        if (startDate != null) {
            cal.setTime(startDate);
        }
        else {
            // initialize date 3 days ahead with random time between 10a - 5p
            final Random random = new Random();
            cal.set(Calendar.HOUR_OF_DAY, random.nextInt(8) + 10);
            cal.set(Calendar.MINUTE, 0);
            cal.add(Calendar.DATE, 3);
        }

        return cal;
    }

    private void updateRequestDate() {
        if (rescheduleBooking != null) return;

        final BookingRequest request = bookingManager.getCurrentRequest();
        final Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        date.set(Calendar.MONTH, datePicker.getMonth());
        date.set(Calendar.YEAR, datePicker.getYear());
        date.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());

        date.set(Calendar.MINUTE, Integer.parseInt(displayedMinuteValues
                .get(timePicker.getCurrentMinute())));

        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        request.setStartDate(date.getTime());
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            if (rescheduleBooking != null) {
                rescheduleBooking();
                return;
            }

            if (postOptions != null && postOptions.size() > 0) {
                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(postOptions));

                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, postOptions.get(0).getPage());
                intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                startActivity(intent);

                return;
            }

            continueBookingFlow();
        }
    };

    private void rescheduleBooking() {
        final Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        date.set(Calendar.MONTH, datePicker.getMonth());
        date.set(Calendar.YEAR, datePicker.getYear());
        date.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());

        date.set(Calendar.MINUTE, Integer.parseInt(displayedMinuteValues
                .get(timePicker.getCurrentMinute())));

        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        final String newDate = TextUtils.formatDate(date.getTime(), "yyyy-MM-dd HH:mm");
        final User user = userManager.getCurrentUser();

        if (rescheduleBooking.getIsRecurring()) {
            toast.setText("Ask Recurring Option");
            toast.show();
            return;
        }

        disableInputs();
        progressDialog.show();

        dataManager.rescheduleBooking(rescheduleBooking.getId(), newDate, user.getId(),
            user.getAuthToken(), new DataManager.Callback<String>() {
                @Override
                public void onSuccess(final String message) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();

                    if (message != null) {
                        toast.setText(message);
                        toast.show();
                    }

                    final Intent intent = new Intent();
                    intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_NEW_DATE,
                            date.getTimeInMillis());

                    getActivity().setResult(BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE, intent);
                    getActivity().finish();
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
        });
    }
}

//TODO reschedule all subsequent (reschedule_all)
//TODO handle surge pricing
