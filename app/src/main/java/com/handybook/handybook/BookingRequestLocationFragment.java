package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingRequestLocationFragment extends InjectedFragment {

    @Inject BookingRequestManager requestManager;

    static BookingRequestLocationFragment newInstance() {
        return new BookingRequestLocationFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookreq_location, container, false);
        ButterKnife.inject(this, view);

        System.out.println("HERE " + requestManager.getCurrentRequest().getServiceId());

        return view;
    }
}
