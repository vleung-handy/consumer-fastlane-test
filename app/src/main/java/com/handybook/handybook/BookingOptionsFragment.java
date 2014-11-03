package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingOptionsFragment extends InjectedFragment {

    @Inject BookingRequestManager requestManager;

    static BookingOptionsFragment newInstance() {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_options, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
