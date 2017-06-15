package com.handybook.handybook.booking.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.library.ui.view.SingleSpinnerTimePicker;

import java.text.DateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.library.ui.view.SingleSpinnerTimePicker.TimeInterval;

/**
 * dialog fragment specifically to input booking time
 */
public class BookingTimeInputDialogFragment extends BaseDialogFragment {

    public static final String TAG = BookingTimeInputDialogFragment.class.getName();
    private static final String BUNDLE_KEY_SELECTED_MINUTE_OF_DAY
            = "BUNDLE_KEY_SELECTED_MINUTE_OF_DAY";
    private static final String BUNDLE_KEY_SELECTED_MINUTE_INTERVAL
            = "BUNDLE_KEY_SELECTED_MINUTE_INTERVAL";
    private static final String BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN
            = "BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN";
    private static final String BUNDLE_KEY_TIME_INTERVALS
            = "BUNDLE_KEY_TIME_INTERVALS";

    @BindView(R.id.fragment_dialog_booking_time_input_picker)
    SingleSpinnerTimePicker mSingleSpinnerTimePicker;

    public static BookingTimeInputDialogFragment newInstance(
            int minuteOfDay,
            int minuteInterval,
            @NonNull DateFormat timePickerDisplayFormat,
            @NonNull ArrayList<TimeInterval> timeIntervals
    ) {
        BookingTimeInputDialogFragment bookingTimeInputDialogFragment =
                new BookingTimeInputDialogFragment();
        bookingTimeInputDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SELECTED_MINUTE_OF_DAY, minuteOfDay);
        bundle.putInt(BUNDLE_KEY_SELECTED_MINUTE_INTERVAL, minuteInterval);
        bundle.putSerializable(BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN, timePickerDisplayFormat);
        bundle.putSerializable(BUNDLE_KEY_TIME_INTERVALS, timeIntervals);
        bookingTimeInputDialogFragment.setArguments(bundle);
        return bookingTimeInputDialogFragment;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(
                R.layout.fragment_dialog_booking_time_input,
                container,
                true
        );
        ButterKnife.bind(this, view);

        int minuteOfDay = getArguments().getInt(BUNDLE_KEY_SELECTED_MINUTE_OF_DAY);
        int minuteInterval = getArguments().getInt(BUNDLE_KEY_SELECTED_MINUTE_INTERVAL);
        DateFormat timePickerDisplayFormat
                = (DateFormat) getArguments().getSerializable(BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN);
        ArrayList<TimeInterval> intervals
                = (ArrayList<TimeInterval>) getArguments().getSerializable(BUNDLE_KEY_TIME_INTERVALS);

        mSingleSpinnerTimePicker.initialize(intervals, minuteInterval, timePickerDisplayFormat);
        mSingleSpinnerTimePicker.setSelectedHourAndMinute(minuteOfDay);

        return view;
    }

    @OnClick(R.id.fragment_dialog_booking_time_input_cancel_button)
    public void onCancelButtonClicked() {
        dismiss();
    }

    @OnClick(R.id.fragment_dialog_booking_time_input_save_button)
    public void onSaveButtonClicked() {
        ((OnTimeSetListener) getParentFragment()).OnTimeSet(
                mSingleSpinnerTimePicker.getCurrentHourOfDay(),
                mSingleSpinnerTimePicker.getCurrentMinuteOfHour(),
                mSingleSpinnerTimePicker.getCurrentIsInstantBookEnabled()
        );
        dismiss();
    }

    public interface OnTimeSetListener {

        void OnTimeSet(int hourOfDay, int minuteOfHour, boolean isInstantBookEnabled);
    }
}
