package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class OnboardFragment extends BookingFlowFragment {

    @InjectView(R.id.next_button) Button nextButton;

    public static OnboardFragment newInstance() {
        return new OnboardFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_onboard, container, false);

        ButterKnife.inject(this, view);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActivity().finish();
            }
        });

        return view;
    }
}
