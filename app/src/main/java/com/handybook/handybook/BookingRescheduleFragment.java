package com.handybook.handybook;

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
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingRescheduleFragment extends BookingFlowFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_NOTICE = "com.handy.handy.EXTRA_NOTICE";
    private final int MINUTE_INTERVAL = 15;

    private Booking rescheduleBooking;
    private List<String> displayedMinuteValues;
    private String notice;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.date_picker) DatePicker datePicker;
    @InjectView(R.id.time_picker) TimePicker timePicker;
    @InjectView(R.id.nav_text) TextView navText;
    @InjectView(R.id.notice_text) TextView noticeText;

    static BookingRescheduleFragment newInstance(final Booking booking, final String notice) {
        final BookingRescheduleFragment fragment = new BookingRescheduleFragment();
        final Bundle args = new Bundle();

        args.putParcelable(EXTRA_BOOKING, booking);
        args.putString(EXTRA_NOTICE, notice);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rescheduleBooking = getArguments().getParcelable(EXTRA_BOOKING);
        notice = getArguments().getString(EXTRA_NOTICE);

        // flash notice since it may not initially appear in view
        if (savedInstanceState == null && notice != null) {
            toast.setText(notice);
            toast.show();
        }

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

        navText.setText(getString(R.string.reschedule));
        nextButton.setText(getString(R.string.reschedule));

        if (notice != null) {
            noticeText.setText(notice);
            noticeText.setVisibility(View.VISIBLE);
        }

        final Calendar startDate = Calendar.getInstance();
        startDate.setTime(rescheduleBooking.getStartDate());

        datePicker.init(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH), null);

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

        // subtracting 1s to avoid illegal state exception being thrown
        final Calendar today = Calendar.getInstance();
        datePicker.setMinDate(today.getTimeInMillis() - 1000);

        // set max date to one year from today
        today.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
        datePicker.setMaxDate(today.getTimeInMillis());

        nextButton.setOnClickListener(nextClicked);
        return view;
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

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

        }
    };
}
