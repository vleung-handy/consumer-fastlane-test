package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public final class PeakPricingTableFragment extends BookingFlowFragment {

    static PeakPricingTableFragment newInstance() {
        final PeakPricingTableFragment fragment = new PeakPricingTableFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_peak_pricing_table,container, false);

        ButterKnife.inject(this, view);



        return view;
    }
}
