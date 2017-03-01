package com.handybook.handybook.booking.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.library.ui.view.SingleSpinnerTimePicker;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * dialog fragment specifically to input booking time
 */
public class BookingTimeInputDialogFragment extends BaseDialogFragment {

    public static final String TAG = BookingTimeInputDialogFragment.class.getName();
    private static final String BUNDLE_KEY_SELECTED_MINUTE_OF_DAY
            = "BUNDLE_KEY_SELECTED_MINUTE_OF_DAY";
    private static final String BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN
            = "BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN";

    private static int TIME_PICKER_MINUTE_INTERVAL = 30;

    /**
     * would be nice if server sent us this
     *
     */
    private static int MIN_HOUR_OF_DAY = 7;
    private static int MIN_MINUTE_OF_MIN_HOUR_OF_DAY = 0;
    private static int MAX_HOUR_OF_DAY = 21;
    private static int MAX_MINUTE_OF_MAX_HOUR_OF_DAY = 0;

    @Bind(R.id.fragment_dialog_booking_time_input_picker)
    SingleSpinnerTimePicker mSingleSpinnerTimePicker;

    public static BookingTimeInputDialogFragment newInstance(
            int minuteOfDay,
            @NonNull SimpleDateFormat timePickerDisplayPattern
    ) {
        BookingTimeInputDialogFragment bookingTimeInputDialogFragment =
                new BookingTimeInputDialogFragment();
        bookingTimeInputDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SELECTED_MINUTE_OF_DAY, minuteOfDay);
        bundle.putSerializable(BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN, timePickerDisplayPattern);
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
        SimpleDateFormat timePickerFormatPattern
                = (SimpleDateFormat) getArguments().getSerializable(
                BUNDLE_KEY_TIME_PICKER_DISPLAY_PATTERN);

        mSingleSpinnerTimePicker.initialize(
                MIN_HOUR_OF_DAY,
                MIN_MINUTE_OF_MIN_HOUR_OF_DAY,
                MAX_HOUR_OF_DAY,
                MAX_MINUTE_OF_MAX_HOUR_OF_DAY,
                TIME_PICKER_MINUTE_INTERVAL,
                timePickerFormatPattern
        );
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
                mSingleSpinnerTimePicker.getCurrentMinuteOfHour()
        );
        dismiss();
    }

    public interface OnTimeSetListener {

        void OnTimeSet(int hourOfDay, int minuteOfHour);
    }
}
