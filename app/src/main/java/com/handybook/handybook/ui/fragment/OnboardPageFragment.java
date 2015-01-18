package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;

import butterknife.ButterKnife;

public final class OnboardPageFragment extends BookingFlowFragment {
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";

    private int page;

    public static OnboardPageFragment newInstance(final int page) {
        final OnboardPageFragment fragment = new OnboardPageFragment();

        final Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE, page);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_PAGE);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        int layout;

        switch (page) {
            case 1:
                layout = R.layout.fragment_onboard_page_pro;
                break;

            case 2:
                layout = R.layout.fragment_onboard_page_manage;
                break;

            case 3:
                layout = R.layout.fragment_onboard_page_rate;
                break;

            default:
                layout = R.layout.fragment_onboard_page_main;
        }

        final View view = getActivity().getLayoutInflater()
                .inflate(layout, container, false);

        ButterKnife.inject(this, view);

        return view;
    }
}
