package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.request.BookingEditFrequencyRequest;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.viewmodel.BookingEditFrequencyViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditFrequencyFragment extends BookingFlowFragment
{
    //TODO: need to consolidate all booking edit fragments with booking flow fragments that are used in booking creation
    private Booking mBooking;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?
    @Bind(R.id.next_button)
    TextView mSaveButton;
    @Bind(R.id.subtitle_text)
    TextView mSubtitleText;

    private BookingEditFrequencyViewModel mBookingEditFrequencyViewModel;
    private BookingOptionsSelectView mOptionsView;

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
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HandyEvent.RequestGetEditFrequencyViewModel(Integer.parseInt(mBooking.getId()))); //TODO: investigate why ID is a string?
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_booking_edit_frequency, container, false);
        ButterKnife.bind(this, view);

        mSubtitleText.setText(R.string.how_often_should_come);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            //TODO: investigate: onClick annotation (as used by edit hours) does not work
            @Override
            public void onClick(final View v)
            {
                sendEditFrequencyRequest();
            }
        });
        return view;
    }

    public void sendEditFrequencyRequest()
    {
        showUiBlockers();

        //get the current selected index of the options view
        final int selectedIndex = mOptionsView.getCurrentIndex();

        //create and set the booking request object
        BookingEditFrequencyRequest bookingEditFrequencyRequest = new BookingEditFrequencyRequest();
        bookingEditFrequencyRequest.setRecurringFrequency(
                mBookingEditFrequencyViewModel.getFrequencyOptionValue(selectedIndex));

        //post the booking request object
        bus.post(new HandyEvent.RequestEditBookingFrequency(
                Integer.parseInt(mBooking.getId()),
                bookingEditFrequencyRequest));
    }

    private void createOptionsView()
    {
        //create the options view
        BookingOption bookingOption = mBookingEditFrequencyViewModel.getBookingOptionFromBooking(
                this.getActivity(), mBooking);
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), bookingOption, null);
        mOptionsView.setCurrentIndex(mBookingEditFrequencyViewModel.getOptionIndexForFrequencyValue());
        mOptionsView.hideTitle();
        optionsLayout.removeAllViews(); //TODO: use a stub or replaceview instead
        optionsLayout.addView(mOptionsView);
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencySuccess(HandyEvent.ReceiveEditBookingFrequencySuccess event)
    {
        removeUiBlockers();
        showToast(R.string.updated_booking_frequency);

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencyError(HandyEvent.ReceiveEditBookingFrequencyError event)
    {
        onReceiveErrorEvent(event);
    }

    @Subscribe
    public final void onReceiveBookingPricesForFrequenciesSuccess(HandyEvent.ReceiveGetEditFrequencyViewModelSuccess event)
    {
        mBookingEditFrequencyViewModel = event.bookingEditFrequencyViewModel;
        createOptionsView();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveBookingPricesForFrequenciesError(HandyEvent.ReceiveGetEditFrequencyViewModelError event)
    {
        onReceiveErrorEvent(event);
    }
}
