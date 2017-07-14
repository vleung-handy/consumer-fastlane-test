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
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditAddressFragment extends ProgressSpinnerFragment {

    @BindView(R.id.zip_text)
    ZipCodeInputTextView mZipCodeInputTextView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Booking mBooking;
    private static final String AC_FRAG_TAG = AutoCompleteAddressFragment.class.getName();

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
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_edit_booking_address, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.booking_edit_address_title));

        mAutoCompleteFragment
                = (AutoCompleteAddressFragment) getChildFragmentManager().findFragmentByTag(
                AC_FRAG_TAG);

        final Booking.Address address = mBooking.getAddress();
        if (mAutoCompleteFragment == null) {
            if (address != null) {
                mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                        new ZipValidationResponse.ZipArea(
                                address.getCity(),
                                address.getState(),
                                address.getZip()
                        ),
                        address.getAddress1(),
                        address.getAddress2(),
                        address.getCity(),
                        address.getState(),
                        mConfigurationManager.getCachedConfiguration(),
                        true
                );
                mZipCodeInputTextView.setText(address.getZip());
            }
            else {
                mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                        null,
                        null,
                        null,
                        null,
                        null,
                        mConfigurationManager.getCachedConfiguration(),
                        true
                );
            }
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.edit_booking_address_fragment_container,
                        mAutoCompleteFragment,
                        AC_FRAG_TAG
                )
                .commitAllowingStateLoss();
        return view;
    }

    private void sendEditAddressRequest() {
        EditAddressRequest bookingEditAddressRequest = new EditAddressRequest(
                mAutoCompleteFragment.getStreet(),
                mAutoCompleteFragment.getApt(),
                mZipCodeInputTextView.getZipCode(),
                mAutoCompleteFragment.getCity(),
                mAutoCompleteFragment.getState()
        );
        showProgressSpinner(true);
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
        hideProgressSpinner();
        showToast(getString(R.string.updated_booking_address));

        //save the new zip in to shared prefs for the user going forward.
        mDefaultPreferencesManager.setString(PrefsKey.ZIP, mZipCodeInputTextView.getZipCode());

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditBookingAddressError(BookingEditEvent.ReceiveEditBookingAddressError event) {
        dataManagerErrorHandler.handleError(getActivity(), event.error);
        hideProgressSpinner(); //allow user to try again
    }

}
