package com.handybook.handybook.ui.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class OnboardFragment extends BookingFlowFragment {
    private int currentIndex;

    @InjectView(R.id.layout) View layout;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.start_button) Button startButton;

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

        pager.setOnPageChangeListener(pageListener);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
    }

    private final ViewPager.OnPageChangeListener pageListener
            = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(final int position, final float offset, final int pixelOffset) {
            // transition background colors
            final int[] colors = {getResources().getColor(R.color.white),
                    getResources().getColor(R.color.handy_purple),
                    getResources().getColor(R.color.handy_blue),
                    getResources().getColor(R.color.handy_teal),
                    getResources().getColor(R.color.white)};

            final int fromColor = colors[position];
            final int toColor = colors[position + 1];

            final ArgbEvaluator rgbEval = new ArgbEvaluator();

            if (position == currentIndex && offset > 0) {
                layout.setBackgroundColor((int)rgbEval.evaluate(offset, fromColor, toColor));
            }
            else if (offset > 0) {
                layout.setBackgroundColor((int)rgbEval.evaluate(offset, fromColor, toColor));
            }
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
            return OnboardPageFragment.newInstance();
        }

        @Override
        public final int getCount() {
            return 4;
        }
    }
}
