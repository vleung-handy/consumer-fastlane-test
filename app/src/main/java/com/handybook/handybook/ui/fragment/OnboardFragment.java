package com.handybook.handybook.ui.fragment;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class OnboardFragment extends BookingFlowFragment {
    private int currentIndex;

    @InjectView(R.id.layout) View layout;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.start_button) Button startButton;
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(pageListener);
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

            //TODO save & restore index position state
        }

        @Override
        public void onPageSelected(final int i) {
            currentIndex = i;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {}
    };

    private final class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public final Fragment getItem(final int i) {
            return OnboardPageFragment.newInstance(i);
        }

        @Override
        public final int getCount() {
            return 4;
        }
    }
}
