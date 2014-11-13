package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingPaymentFragment extends InjectedFragment {
    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;

    static BookingPaymentFragment newInstance() {
        final BookingPaymentFragment fragment = new BookingPaymentFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment,container, false);

        ButterKnife.inject(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        if (user != null) {
            //TODO handle if user has a card and doesnt have card
        }
        else {
            //TODO handle no user so show card input fields
        }

        return view;
    }
}
