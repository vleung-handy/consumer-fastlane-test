package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;

import butterknife.ButterKnife;

public final class OnboardPageFragment extends BookingFlowFragment {

    public static OnboardPageFragment newInstance() {
        return new OnboardPageFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_onboard_page, container, false);

        ButterKnife.inject(this, view);

        return view;
    }
}
