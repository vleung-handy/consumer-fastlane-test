package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.request.BookingEditAddressRequest;
import com.handybook.handybook.ui.widget.StreetAddressInputTextView;
import com.handybook.handybook.ui.widget.ZipCodeInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditAddressFragment extends BookingFlowFragment
{
    @Bind(R.id.street_addr_text)
    StreetAddressInputTextView mStreetAddressInputTextView1;
    @Bind(R.id.other_addr_text)
    EditText mStreetAddressInputTextView2;
    @Bind(R.id.zip_text)
    ZipCodeInputTextView mZipCodeInputTextView;

    private Booking mBooking;

    public static BookingEditAddressFragment newInstance(Booking booking)
    {
        final BookingEditAddressFragment fragment = new BookingEditAddressFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_edit_booking_address, container, false);
        ButterKnife.bind(this, view);

        if (mBooking.getAddress() != null)
        {
            //initialize with the booking's current address
            mStreetAddressInputTextView1.setText(mBooking.getAddress().getAddress1());
            mStreetAddressInputTextView2.setText(mBooking.getAddress().getAddress2());
            mZipCodeInputTextView.setText(mBooking.getAddress().getZip());
        }

        return view;
    }

    private void sendEditAddressRequest()
    {
        BookingEditAddressRequest bookingEditAddressRequest = new BookingEditAddressRequest(
                mStreetAddressInputTextView1.getAddress(),
                mStreetAddressInputTextView2.getText().toString(),
                mZipCodeInputTextView.getZipCode()
        );
        showUiBlockers();
        bus.post(new HandyEvent.RequestEditBookingAddress(Integer.parseInt(mBooking.getId()),
                bookingEditAddressRequest));
    }

    private boolean validateFields()
    {
        return (mStreetAddressInputTextView1.validate()
                && mZipCodeInputTextView.validate());
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick()
    {
        if (validateFields())
        {
            sendEditAddressRequest();
        }
    }

    @Subscribe
    public final void onReceiveEditBookingAddressSuccess(HandyEvent.ReceiveEditBookingAddressSuccess event)
    {
        removeUiBlockers();
        showToast(getString(R.string.updated_booking_address));

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditBookingAddressError(HandyEvent.ReceiveEditBookingAddressError event)
    {
        onReceiveErrorEvent(event);
        removeUiBlockers(); //allow user to try again
    }

}
