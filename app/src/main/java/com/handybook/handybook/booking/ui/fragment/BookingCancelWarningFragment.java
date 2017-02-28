package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingLog;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingCancelWarningFragment extends BookingFlowFragment {

    public static final String EXTRA_BOOKING_CANCELLATION_DATA
            = "com.handy.handy.EXTRA_BOOKING_CANCELLATION_DATA";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking mBooking;
    private BookingCancellationData mBookingCancellationData;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fragment_booking_cancel_warning_warning)
    TextView mWarningMessage;
    @Bind(R.id.fragment_booking_cancel_warning_title)
    TextView mTitle;
    @Bind(R.id.fragment_booking_cancel_warning_message)
    TextView mMessage;
    @Bind(R.id.fragment_booking_cancel_warning_button)
    Button mButton;

    public static BookingCancelWarningFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final BookingCancellationData bookingCancellationData
    ) {
        final BookingCancelWarningFragment fragment = new BookingCancelWarningFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOOKING_CANCELLATION_DATA, bookingCancellationData);
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingCancellationData = (BookingCancellationData) getArguments()
                .getSerializable(EXTRA_BOOKING_CANCELLATION_DATA);
        mBooking = getArguments().getParcelable(EXTRA_BOOKING);
        bus.post(new LogEvent.AddLogEvent(
                         new BookingLog.BookingCancelWarningShown(mBooking.getId())
                 )
        );
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_booking_cancel_warning, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    private void initUI() {
        setupToolbar(
                mToolbar,
                mBookingCancellationData.getPreCancellationInfo().getNavigationTitle(),
                true
        );
        mWarningMessage.setText(mBookingCancellationData.getWarningMessage());
        mWarningMessage.setVisibility(
                mBookingCancellationData.hasWarning() ? View.VISIBLE : View.GONE
        );
        mTitle.setText(mBookingCancellationData.getPreCancellationInfo().getTitle());
        mMessage.setText(mBookingCancellationData.getPreCancellationInfo().getMessage());
        mButton.setText(mBookingCancellationData.getPreCancellationInfo().getButtonLabel());
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                FragmentUtils.switchToFragment(
                        BookingCancelWarningFragment.this,
                        BookingCancelReasonFragment.newInstance(mBooking, mBookingCancellationData),
                        false
                );
            }
        });

    }

}
