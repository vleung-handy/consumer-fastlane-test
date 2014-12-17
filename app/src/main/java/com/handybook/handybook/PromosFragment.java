package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PromosFragment extends BookingFlowFragment {

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;

    static PromosFragment newInstance() {
        return new PromosFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_promos,container, false);

        ButterKnife.inject(this, view);

        final MenuButton menuButton = new MenuButton(getActivity());
        menuButton.setColor(getResources().getColor(R.color.black_pressed));
        Utils.extendHitArea(menuButton, menuButtonLayout, Utils.toDP(32, getActivity()));
        menuButtonLayout.addView(menuButton);

        return view;
    }
}
