package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDateFragment extends BookingFlowFragment {
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";

    private ArrayList<BookingOption> postOptions;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.date_picker) DatePicker datePicker;
    @InjectView(R.id.time_picker) TimePicker timePicker;

    static BookingDateFragment newInstance(final ArrayList<BookingOption> postOptions) {
        final BookingDateFragment fragment = new BookingDateFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_date,container, false);

        ButterKnife.inject(this, view);

        final Calendar startDate = currentStartDate();

        datePicker.init(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
            startDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                    updateRequestDate();
                }
        });

        timePicker.setCurrentHour(startDate.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(startDate.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(final TimePicker view, final int hourOfDay, final int minute) {
                updateRequestDate();
            }
        });

        // adding 1s to avoid illegal state excpetion being thrown
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis() + 1000);
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
        timePicker.setCurrentMinute(startDate.get(Calendar.MINUTE));
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
        final BookingRequest request = bookingManager.getCurrentRequest();
        final Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        date.set(Calendar.MONTH, datePicker.getMonth());
        date.set(Calendar.YEAR, datePicker.getYear());
        date.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        date.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        request.setStartDate(date.getTime());
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (postOptions != null && postOptions.size() > 0) {
                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(postOptions));

                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, postOptions.get(0).getPage());
                intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                startActivity(intent);

                return;
            } continueBookingFlow();
        }
    };
}
