package com.handybook.handybook.booking.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * this is a fragment instead of a custom view because:
 * <p>
 * - need to init and launch dialog fragment to capture time input
 * and want to encapsulate that logic so that this input can be easily moved
 * - may want to inject things like event bus in the future
 * - may want to do things onResume
 */

public class BookingDateTimeInputFragment extends InjectedFragment
        implements BookingTimeInputDialogFragment.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private SimpleDateFormat mEditDateButtonFormatter;
    private SimpleDateFormat mEditTimeButtonFormatter;
    @Bind(R.id.booking_edit_date_button)
    Button mBookingEditDateButton;
    @Bind(R.id.booking_edit_time_button)
    Button mBookingEditTimeButton;
    private DatePickerDialog mDatePickerDialog;

    private Calendar mSelectedDateTime;

    public static final String BUNDLE_KEY_START_DATE_TIME = "BUNDLE_KEY_START_DATE_TIME";
    public static final String BUNDLE_KEY_DATE_DISPLAY_PATTERN = "BUNDLE_KEY_DATE_DISPLAY_PATTERN";
    public static final String BUNDLE_KEY_TIME_DISPLAY_PATTERN = "BUNDLE_KEY_TIME_DISPLAY_PATTERN";

    public static BookingDateTimeInputFragment newInstance(
            @NonNull Calendar startDateTimeWithTimezone,
            @NonNull String dateDisplayPattern,
            @NonNull String timeDisplayPattern
    ) {
        BookingDateTimeInputFragment fragment = new BookingDateTimeInputFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_START_DATE_TIME, startDateTimeWithTimezone);
        args.putString(BUNDLE_KEY_DATE_DISPLAY_PATTERN, dateDisplayPattern);
        args.putString(BUNDLE_KEY_TIME_DISPLAY_PATTERN, timeDisplayPattern);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_date_time_input,
                                               container,
                                               false
                                       );

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        Bundle args = getArguments();
        Calendar startDateAndTime = (Calendar) args.getSerializable(BUNDLE_KEY_START_DATE_TIME);
        String dateDisplayPattern = args.getString(BUNDLE_KEY_DATE_DISPLAY_PATTERN);
        String timeDisplayPattern = args.getString(BUNDLE_KEY_TIME_DISPLAY_PATTERN);

        mEditDateButtonFormatter =
                new SimpleDateFormat(dateDisplayPattern, Locale.getDefault());
        mEditDateButtonFormatter.setTimeZone(startDateAndTime.getTimeZone());
        mEditTimeButtonFormatter =
                new SimpleDateFormat(timeDisplayPattern, Locale.getDefault());
        mEditTimeButtonFormatter.setTimeZone(startDateAndTime.getTimeZone());

        mSelectedDateTime = startDateAndTime;

        final Calendar today = Calendar.getInstance();
        today.setTimeZone(startDateAndTime.getTimeZone());
        today.setTime(new Date());
        long minDateMs = today.getTimeInMillis() - 1000;
        // set max date to one year from today
        today.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
        today.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
        long maxDateMs = today.getTimeInMillis();

        initDatePicker(
                startDateAndTime.get(Calendar.YEAR),
                startDateAndTime.get(Calendar.MONTH),
                startDateAndTime.get(Calendar.DAY_OF_MONTH),
                minDateMs,
                maxDateMs
        );

        updateDateTimeDisplay();
        mBookingEditTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildFragmentManager().findFragmentByTag(BookingTimeInputDialogFragment.TAG) ==
                    null) {
                    int hourOfDay = mSelectedDateTime.get(Calendar.HOUR_OF_DAY);
                    int minuteOfHour = mSelectedDateTime.get(Calendar.MINUTE);

                    int minutesOfDay = (int) (TimeUnit.HOURS.toMinutes(hourOfDay) + minuteOfHour);
                    BookingTimeInputDialogFragment bookingTimeInputDialogFragment
                            = BookingTimeInputDialogFragment.newInstance(
                            minutesOfDay,
                            mEditTimeButtonFormatter
                    );
                    FragmentUtils.safeLaunchDialogFragment(
                            bookingTimeInputDialogFragment,
                            BookingDateTimeInputFragment.this,
                            BookingTimeInputDialogFragment.TAG
                    );
                }

            }
        });
    }

    private void init() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mDatePickerDialog =
                new DatePickerDialog(
                        getContext(),
                        R.style.DateTimePickerTheme,
                        this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
    }

    /**
     * initializes the date picker with a selected date and input range
     *
     * @param selectedYear
     * @param selectedMonth
     * @param selectedDayOfMonth
     * @param minDateMs
     * @param maxDateMs
     */
    public void initDatePicker(
            int selectedYear,
            int selectedMonth,
            int selectedDayOfMonth,
            long minDateMs,
            long maxDateMs
    ) {
        DatePicker datePicker = mDatePickerDialog.getDatePicker();
        mDatePickerDialog.updateDate(selectedYear, selectedMonth, selectedDayOfMonth);
        datePicker.setMinDate(minDateMs);
        datePicker.setMaxDate(maxDateMs);

        //apparently this will not work if set before call to setMaxDate()
        //needed to hide the header
        mDatePickerDialog.setTitle(null);
        mDatePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDatePickerDialog.setCustomTitle(null);
    }

    @OnClick(R.id.booking_edit_date_button)
    public void onEditDateButtonClicked() {
        //show the date picker dialog
        mDatePickerDialog.show();
    }

    public void updateDateTimeDisplay() {
        Date selectedDate = mSelectedDateTime.getTime();

        mBookingEditDateButton.setText(
                mEditDateButtonFormatter.format(selectedDate));
        mBookingEditTimeButton.setText(
                mEditTimeButtonFormatter.format(selectedDate));
    }

    private void notifyTimeUpdatedListener() {
        ((OnSelectedDateTimeUpdatedListener) getParentFragment()).
                                                                         onSelectedDateTimeUpdatedListener(
                                                                                 mSelectedDateTime);
    }

    @Override
    public void OnTimeSet(int hourOfDay, int minuteOfHour) {
        mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mSelectedDateTime.set(Calendar.MINUTE, minuteOfHour);
        updateDateTimeDisplay();
        notifyTimeUpdatedListener();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mSelectedDateTime.set(Calendar.YEAR, year);
        mSelectedDateTime.set(Calendar.MONTH, month);
        mSelectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateTimeDisplay();
        notifyTimeUpdatedListener();
    }

    public interface OnSelectedDateTimeUpdatedListener {

        void onSelectedDateTimeUpdatedListener(Calendar selectedDateTime);
    }
}
