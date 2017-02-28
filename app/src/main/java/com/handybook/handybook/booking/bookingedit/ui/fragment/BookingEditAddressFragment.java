package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditAddressFragment extends BookingFlowFragment {

    @Bind(R.id.zip_text)
    ZipCodeInputTextView mZipCodeInputTextView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private Booking mBooking;

    AutoCompleteAddressFragment mAutoCompleteFragment;

    public static BookingEditAddressFragment newInstance(Booking booking) {
        final BookingEditAddressFragment fragment = new BookingEditAddressFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater
                .inflate(R.layout.fragment_edit_booking_address, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.booking_edit_address_title));

        if (mBooking.getAddress() != null) {
            mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                    new ZipValidationResponse.ZipArea(
                            mBooking.getAddress().getCity(),
                            mBooking.getAddress().getState(),
                            mBooking.getAddress().getZip()
                    ),
                    mBooking.getAddress().getAddress1(),
                    mBooking.getAddress().getAddress2(),
                    mConfigurationManager.getCachedConfiguration()
            );

            mZipCodeInputTextView.setText(mBooking.getAddress().getZip());
        }
        else {
            mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                    null,
                    null,
                    null,
                    mConfigurationManager.getCachedConfiguration()
            );
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.edit_booking_address_fragment_container, mAutoCompleteFragment)
                .commitAllowingStateLoss();

        return view;
    }

    private void sendEditAddressRequest() {
        EditAddressRequest bookingEditAddressRequest = new EditAddressRequest(
                mAutoCompleteFragment.mStreet.getAddress(),
                mAutoCompleteFragment.mOther.getText().toString(),
                mZipCodeInputTextView.getZipCode()
        );
        showUiBlockers();
        bus.post(new BookingEditEvent.RequestEditBookingAddress(
                Integer.parseInt(mBooking.getId()),
                bookingEditAddressRequest
        ));
    }

    private boolean validateFields() {
        return (mAutoCompleteFragment.validateFields() && mZipCodeInputTextView.validate());
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if (validateFields()) {
            sendEditAddressRequest();
        }
    }

    @Subscribe
    public final void onReceiveEditBookingAddressSuccess(BookingEditEvent.ReceiveEditBookingAddressSuccess event) {
        removeUiBlockers();
        showToast(getString(R.string.updated_booking_address));

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditBookingAddressError(BookingEditEvent.ReceiveEditBookingAddressError event) {
        onReceiveErrorEvent(event);
        removeUiBlockers(); //allow user to try again
    }

}
