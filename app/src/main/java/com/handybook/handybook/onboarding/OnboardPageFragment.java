package com.handybook.handybook.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class OnboardPageFragment extends BookingFlowFragment
{
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    static final String EXTRA_ANIMATE = "com.handy.handy.EXTRA_ANIMATE";

    private int page;
    private boolean animated;

    @Bind(R.id.image)
    View image;
    @Nullable
    @Bind(R.id.icon_clean)
    ImageView cleanIcon;
    @Nullable
    @Bind(R.id.icon_handy)
    ImageView handyIcon;
    @Nullable
    @Bind(R.id.icon_paint)
    ImageView paintIcon;
    @Nullable
    @Bind(R.id.image_pro_1)
    ImageView proImage1;
    @Nullable
    @Bind(R.id.image_pro_3)
    ImageView proImage3;
    @Nullable
    @Bind(R.id.image_pro_4)
    ImageView proImage4;

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

        ButterKnife.bind(this, view);

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
                    Animation fadeOut = AnimationUtils
                            .loadAnimation(getActivity(), R.anim.onboard_pro_fade);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(50);
                    proImage1.startAnimation(fadeOut);

                    fadeOut = AnimationUtils
                            .loadAnimation(getActivity(), R.anim.onboard_pro_fade);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(300);
                    proImage3.startAnimation(fadeOut);

                    fadeOut = AnimationUtils
                            .loadAnimation(getActivity(), R.anim.onboard_pro_fade);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(500);
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
