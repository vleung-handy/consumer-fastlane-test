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
    static final String EXTRA_ANIMATE = "com.handy.handy.EXTRA_ANIMATE";

    private int page;
    private boolean animated;

    @InjectView(R.id.image) View image;
    @Optional @InjectView(R.id.icon_clean) ImageView cleanIcon;
    @Optional @InjectView(R.id.icon_handy) ImageView handyIcon;
    @Optional @InjectView(R.id.icon_paint) ImageView paintIcon;
    @Optional @InjectView(R.id.image_pro_1) ImageView proImage1;
    @Optional @InjectView(R.id.image_pro_3) ImageView proImage3;
    @Optional @InjectView(R.id.image_pro_4) ImageView proImage4;

    public static OnboardPageFragment newInstance(final int page, final boolean animate) {
        final OnboardPageFragment fragment = new OnboardPageFragment();

        final Bundle args = new Bundle();
        args.putInt(EXTRA_PAGE, page);
        args.putBoolean(EXTRA_ANIMATE, animate);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(EXTRA_PAGE);
        animated = savedInstanceState != null || !getArguments().getBoolean(EXTRA_ANIMATE);
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

        if (animated) {
            image.setVisibility(View.VISIBLE);

            if (page == 0) {
                cleanIcon.setVisibility(View.VISIBLE);
                handyIcon.setVisibility(View.VISIBLE);
                paintIcon.setVisibility(View.VISIBLE);
            }
            else if (page == 1) {
                proImage1.setAlpha(0.4f);
                proImage3.setAlpha(0.4f);
                proImage4.setAlpha(0.4f);
            }
        }

        return view;
    }

    void animate() {
        if (!isVisible() || animated) return;

        final Animation onboardAppear = AnimationUtils
                .loadAnimation(getActivity(), R.anim.onboard_appear);

        if (page == 1) {
            onboardAppear.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {}

                @Override
                public void onAnimationEnd(final Animation animation) {
                    final Animation fadeOut = AnimationUtils
                            .loadAnimation(getActivity(), R.anim.onboard_pro_fade);

                    fadeOut.setFillAfter(true);
                    proImage1.startAnimation(fadeOut);
                    proImage3.startAnimation(fadeOut);
                    proImage4.startAnimation(fadeOut);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {}
            });
        }

        onboardAppear.setFillAfter(true);
        image.startAnimation(onboardAppear);
        animated = true;
    }

    private void animateIcons() {
        if (!isVisible() || animated) return;

        Animation onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);

        onboardAppear.setStartOffset(250);
        onboardAppear.setFillAfter(true);
        cleanIcon.startAnimation(onboardAppear);

        onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);
        onboardAppear.setStartOffset(450);
        onboardAppear.setFillAfter(true);
        handyIcon.startAnimation(onboardAppear);

         onboardAppear = AnimationUtils.loadAnimation(getActivity(),
                R.anim.onboard_appear_icons);
        onboardAppear.setStartOffset(650);
        onboardAppear.setFillAfter(true);
        paintIcon.startAnimation(onboardAppear);
    }
}
