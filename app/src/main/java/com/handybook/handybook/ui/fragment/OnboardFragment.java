package com.handybook.handybook.ui.fragment;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class OnboardFragment extends BookingFlowFragment {
    private static final String STATE_ANIMATE_PAGES = "ANIMATED_PAGES";

    private int currentIndex;
    private boolean[] animatePages;
    private int count = 4;

    @InjectView(R.id.layout) View layout;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.start_button) Button startButton;
    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.indicator) CirclePageIndicator indicator;

    public static OnboardFragment newInstance() {
        return new OnboardFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_onboard, container, false);

        ButterKnife.inject(this, view);

        mixpanel.trackOnboardingShown();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mixpanel.trackOnboardingActionSkip(currentIndex + 1);

                edit.putBoolean("APP_ONBOARD_SHOWN", true);
                edit.apply();

                getActivity().finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mixpanel.trackOnboardingActionLogin(currentIndex + 1);

                edit.putBoolean("APP_ONBOARD_SHOWN", true);
                edit.apply();

                final Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            animatePages = savedInstanceState.getBooleanArray(STATE_ANIMATE_PAGES);
        }
        else {
            animatePages = new boolean[count];
            for (int i = 0; i < count; i++) animatePages[i] = true;
        }

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(pageListener);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STATE_ANIMATE_PAGES, animatePages);
    }

    private final ViewPager.OnPageChangeListener pageListener
            = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(final int position, final float offset, final int pixelOffset) {
            // transition colors
            final int[] backgroundColors = {getResources().getColor(R.color.white),
                    getResources().getColor(R.color.handy_blue),
                    getResources().getColor(R.color.handy_purple),
                    getResources().getColor(R.color.handy_teal),
                    getResources().getColor(R.color.white)};

            final int[] indicatorFillColors = {getResources().getColor(R.color.black),
                    getResources().getColor(R.color.white),
                    getResources().getColor(R.color.white),
                    getResources().getColor(R.color.white),
                    getResources().getColor(R.color.black)};

            final int[] indicatorPageColors = {getResources().getColor(R.color.dark_grey),
                    getResources().getColor(R.color.white_trans),
                    getResources().getColor(R.color.white_trans),
                    getResources().getColor(R.color.white_trans),
                    getResources().getColor(R.color.dark_grey)};

            final int fromBackgroundColor = backgroundColors[position];
            final int toBackgroundColor = backgroundColors[position + 1];
            final int fromIndicatorPageColor = indicatorPageColors[position];
            final int toIndicatorPageColor = indicatorPageColors[position + 1];
            final int fromIndicatorFillColor = indicatorFillColors[position];
            final int toIndicatorFillColor = indicatorFillColors[position + 1];

            final ArgbEvaluator rgbEval = new ArgbEvaluator();

            layout.setBackgroundColor((int)rgbEval.evaluate(offset,
                    fromBackgroundColor, toBackgroundColor));

            indicator.setFillColor((int) rgbEval.evaluate(offset,
                    fromIndicatorFillColor, toIndicatorFillColor));

            indicator.setPageColor((int) rgbEval.evaluate(offset,
                    fromIndicatorPageColor, toIndicatorPageColor));
        }

        @Override
        public void onPageSelected(final int i) {
            currentIndex = i;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            final OnboardPageFragment page = ((PagerAdapter) pager
                    .getAdapter()).getFragment(currentIndex);

            if (page != null) page.animate();
        }
    };

    private final class PagerAdapter extends FragmentPagerAdapter {

        private OnboardPageFragment[] fragments;

        PagerAdapter(final FragmentManager fm) {
            super(fm);
            fragments = new OnboardPageFragment[count];
        }

        @Override
        public final Fragment getItem(final int i) {
            final OnboardPageFragment fragment = OnboardPageFragment.newInstance(i, animatePages[i]);
            fragments[i] = fragment;
            animatePages[i] = false;
            return fragment;
        }

        @Override
        public final int getCount() {
            return count;
        }

        final OnboardPageFragment getFragment(final int i) {
            return fragments[i];
        }
    }
}