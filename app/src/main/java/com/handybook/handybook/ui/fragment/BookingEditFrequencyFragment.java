package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingUpdateFrequencyTransaction;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditFrequencyFragment extends BookingFlowFragment
{
    //TODO: need to consolidate all booking edit fragments with booking flow fragments that are used in booking creation
    private BookingUpdateFrequencyTransaction bookingUpdateFrequencyTransaction;
    private Booking booking;
    private int[] recurValues;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;

    public static BookingEditFrequencyFragment newInstance(Booking booking)
    {
        final BookingEditFrequencyFragment fragment = new BookingEditFrequencyFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackFrequency();
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        initTransaction();
    }

    private void initTransaction()
    {
        bookingUpdateFrequencyTransaction = new BookingUpdateFrequencyTransaction();
        bookingUpdateFrequencyTransaction.setRecurringFrequency(0);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);

        ButterKnife.bind(this, view);

        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setDefaultValue("0");

        option.setOptions(new String[]{getString(R.string.every_week),
                getString(R.string.every_two_weeks), getString(R.string.every_four_weeks)});

        recurValues = new int[]{1, 2, 4}; //allowing edit frequency only for recurring bookings


        option.setOptionsSubText(new String[]
                {null, getString(R.string.most_popular), null});

//        option.setOptionsRightText(getSavingsInfo()); //TODO: need BookingQuote's price table to get savings info

        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);

        optionsView.hideTitle();

        //note: cannot default to booking's current recurring frequency because payload does not return it!

        optionsLayout.addView(optionsView, 0);
        nextButton.setText(R.string.update);
        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            showUiBlockers();
            bus.post(new HandyEvent.RequestUpdateBookingFrequency(Integer.parseInt(booking.getId()), bookingUpdateFrequencyTransaction));
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            bookingUpdateFrequencyTransaction.setRecurringFrequency(recurValues[index]);
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }
    };

    private void showUiBlockers()
    {
        disableInputs();
        progressDialog.show();
    }

    private void removeUiBlockers()
    {
        enableInputs();
        progressDialog.dismiss();
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencySuccess(HandyEvent.ReceiveUpdateBookingFrequencySuccess event)
    {
        removeUiBlockers();
        showToast(R.string.updated_booking_frequency);

        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencyError(HandyEvent.ReceiveUpdateBookingFrequencyError event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
