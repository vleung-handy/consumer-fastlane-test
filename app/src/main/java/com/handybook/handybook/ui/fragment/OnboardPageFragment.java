package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.handybook.handybook.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public final class OnboardPageFragment extends BookingFlowFragment {
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";

    private int page;
    private boolean animated;

    @InjectView(R.id.image) ImageView image;
    @Optional @InjectView(R.id.icon_clean) ImageView cleanIcon;
    @Optional @InjectView(R.id.icon_handy) ImageView handyIcon;
    @Optional @InjectView(R.id.icon_paint) ImageView paintIcon;

    public static OnboardPageFragment newInstance(final int page) {
        final OnboardPageFragment fragment = new OnboardPageFragment();

        final Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE, page);
        fragment.setArguments(args);

        return fragment;

        //TODO add option to animate or not and animate if only first time on pager
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_PAGE);

        if (savedInstanceState != null) animated = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (page == 0) animateIcons();
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

        if (animated) image.setVisibility(View.VISIBLE);

        return view;
    }

    void animate() {
        if (isVisible() && !animated) {
            final Animation onboardAppear = AnimationUtils
                    .loadAnimation(getActivity(), R.anim.onboard_appear);

            image.startAnimation(onboardAppear);
            image.setVisibility(View.VISIBLE);
            animated = true;
        }
    }

    private void animateIcons() {
        Animation onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);

        onboardAppear.setStartOffset(250);
        cleanIcon.startAnimation(onboardAppear);

        onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);
        onboardAppear.setStartOffset(450);
        handyIcon.startAnimation(onboardAppear);

         onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);
        onboardAppear.setStartOffset(650);
        paintIcon.startAnimation(onboardAppear);

        cleanIcon.setVisibility(View.VISIBLE);
        handyIcon.setVisibility(View.VISIBLE);
        paintIcon.setVisibility(View.VISIBLE);
    }
}
