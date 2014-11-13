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

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDateFragment extends BookingFlowFragment {
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";

    private ArrayList<BookingOption> postOptions;
    private ProgressDialog progressDialog;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        final Calendar cal = Calendar.getInstance();
        final int hours, minutes;
        final BookingRequest request = bookingManager.getCurrentRequest();
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

        updateRequestDate();

        return view;
    }

    private void disableInputs() {
        nextButton.setClickable(false);
    }

    private void enableInputs() {
        nextButton.setClickable(true);
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
            }

            disableInputs();
            progressDialog.show();

            final BookingRequest request = bookingManager.getCurrentRequest();
            request.setUserId(userManager.getCurrentUser().getId());
            dataManager.getBookingQuote(request, new DataManager.Callback<BookingQuote>() {
                @Override
                public void onSuccess(final BookingQuote quote) {
                    if (!allowCallbacks) return;
                    showBookingAddress(quote);
                    enableInputs();
                    progressDialog.dismiss();
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
