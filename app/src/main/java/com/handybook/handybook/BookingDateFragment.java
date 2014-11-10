package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDateFragment extends InjectedFragment {

    @Inject BookingRequestManager requestManager;
    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.date_picker) DatePicker datePicker;
    @InjectView(R.id.time_picker) TimePicker timePicker;

    static BookingDateFragment newInstance() {
        return new BookingDateFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_date, container, false);
        ButterKnife.inject(this, view);

        final Calendar cal = Calendar.getInstance();
        final int hours, minutes;
        final BookingRequest request = requestManager.getCurrentRequest();
        final Date startDate = request.getStartDate();

        if (startDate != null) {
            cal.setTime(startDate);
            hours = cal.get(Calendar.HOUR_OF_DAY);
            minutes = cal.get(Calendar.MINUTE);
        }
        else {
            // initialize date 3 days ahead with random time between 10a - 5p
            final Random random = new Random();
            hours = random.nextInt(8) + 10;
            minutes = 0;
            cal.add(Calendar.DATE, 3);
        }

        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                    updateRequestDate();
                }
        });

        timePicker.setCurrentHour(hours);
        timePicker.setCurrentMinute(minutes);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(final TimePicker view, final int hourOfDay, final int minute) {
                updateRequestDate();
            }
        });

        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        return view;

        //TODO handle next button click
    }

    private void updateRequestDate() {
        final BookingRequest request = requestManager.getCurrentRequest();
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
}
