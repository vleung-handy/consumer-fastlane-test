package com.handybook.handybook;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDateFragment extends InjectedFragment {
    private ProgressDialog progressDialog;
    private Toast toast;
    private boolean allowCallbacks;

    @Inject BookingRequestManager requestManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

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

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

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

        // adding 1s to avoid illegal state excpetion being thrown
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis() + 1000);
        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    public final void onStart() {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public final void onStop() {
        super.onStop();
        allowCallbacks = false;
    }

    private void disableInputs() {
        nextButton.setClickable(false);
    }

    private void enableInputs() {
        nextButton.setClickable(true);
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

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            disableInputs();
            progressDialog.show();

            final BookingRequest request = requestManager.getCurrentRequest();
            dataManager.createBooking(request, new DataManager.Callback<String>() {
                    @Override
                    public void onSuccess(String resp) {
                        if (!allowCallbacks) return;

                        enableInputs();
                        progressDialog.dismiss();

                        toast.setText(resp);
                        toast.show();
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
    };
}
