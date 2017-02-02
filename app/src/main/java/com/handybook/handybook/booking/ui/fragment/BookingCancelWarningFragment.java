package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingCancelWarningFragment extends BookingFlowFragment
{
    public static final String EXTRA_BOOKING_CANCELLATION_DATA
            = "com.handy.handy.EXTRA_BOOKING_CANCELLATION_DATA";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int mOptionIndex = -1;
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
            final Booking booking,
            final BookingCancellationData bookingCancellationData
    )
    {
        final BookingCancelWarningFragment fragment = new BookingCancelWarningFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOOKING_CANCELLATION_DATA, bookingCancellationData);
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookingCancellationData = (BookingCancellationData) getArguments()
                .getSerializable(EXTRA_BOOKING_CANCELLATION_DATA);
        mBooking = getArguments().getParcelable(EXTRA_BOOKING);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_booking_cancel_warning, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    private void initUI()
    {
        final boolean isRecurring = mBooking != null && mBooking.isRecurring();
        setupToolbar(
                mToolbar,
                getString(isRecurring ? R.string.skip_booking : R.string.cancel_booking)
        );
        mWarningMessage.setText(mBookingCancellationData.getWarningMessage());
        mWarningMessage.setVisibility(
                mBookingCancellationData.hasWarning() ? View.VISIBLE : View.GONE
        );
        mTitle.setText(mBookingCancellationData.getPreCancellationInfo().getTitle());
        mMessage.setText(mBookingCancellationData.getPreCancellationInfo().getMessage());
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                ((MenuDrawerActivity) getActivity()).replaceFragment(
                        BookingCancelOptionsFragment.newInstance(mBooking, mBookingCancellationData)
                );
            }
        });

    }

}
