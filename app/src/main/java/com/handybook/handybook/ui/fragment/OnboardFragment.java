package com.handybook.handybook.ui.fragment;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.manager.DefaultPreferencesManager;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.SplashActivity;
import com.viewpagerindicator.CirclePageIndicator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class OnboardFragment extends BookingFlowFragment
{
    private static final String STATE_ANIMATE_PAGES = "ANIMATED_PAGES";

    private int currentIndex;
    private boolean[] animatePages;
    private int count = 4;

    @Bind(R.id.layout)
    View layout;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.start_button)
    Button startButton;
    @Bind(R.id.login_button)
    Button loginButton;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;

    public static OnboardFragment newInstance()
    {
        return new OnboardFragment();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_onboard, container, false);

        ButterKnife.bind(this, view);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mDefaultPreferencesManager.setBoolean(PrefsKey.APP_ONBOARD_SHOWN, true);
                final Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mDefaultPreferencesManager.setBoolean(PrefsKey.APP_ONBOARD_SHOWN, true);

                final Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null)
        {
            animatePages = savedInstanceState.getBooleanArray(STATE_ANIMATE_PAGES);
        }
        else
        {
            animatePages = new boolean[count];
            for (int i = 0; i < count; i++) { animatePages[i] = true; }
        }

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        pager.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager()));
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(pageListener);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STATE_ANIMATE_PAGES, animatePages);
    }

    private final ViewPager.OnPageChangeListener pageListener
            = new ViewPager.OnPageChangeListener()
    {

        @Override
        public void onPageScrolled(final int position, final float offset, final int pixelOffset)
        {
            // transition colors
            final int[] backgroundColors = {
                    ContextCompat.getColor(getContext(), R.color.white),
                    ContextCompat.getColor(getContext(), R.color.handy_blue),
                    ContextCompat.getColor(getContext(), R.color.handy_service_painter),
                    ContextCompat.getColor(getContext(), R.color.handy_teal),
                    ContextCompat.getColor(getContext(), R.color.white)
            };

            final int[] indicatorFillColors = {
                    ContextCompat.getColor(getContext(), R.color.handy_text_black),
                    ContextCompat.getColor(getContext(), R.color.white),
                    ContextCompat.getColor(getContext(), R.color.white),
                    ContextCompat.getColor(getContext(), R.color.white),
                    ContextCompat.getColor(getContext(), R.color.handy_text_black)
            };

            final int[] indicatorPageColors = {
                    ContextCompat.getColor(getContext(), R.color.dark_grey),
                    ContextCompat.getColor(getContext(), R.color.white_trans),
                    ContextCompat.getColor(getContext(), R.color.white_trans),
                    ContextCompat.getColor(getContext(), R.color.white_trans),
                    ContextCompat.getColor(getContext(), R.color.dark_grey)
            };

            final int fromBackgroundColor = backgroundColors[position];
            final int toBackgroundColor = backgroundColors[position + 1];
            final int fromIndicatorPageColor = indicatorPageColors[position];
            final int toIndicatorPageColor = indicatorPageColors[position + 1];
            final int fromIndicatorFillColor = indicatorFillColors[position];
            final int toIndicatorFillColor = indicatorFillColors[position + 1];

            final ArgbEvaluator rgbEval = new ArgbEvaluator();

            layout.setBackgroundColor((int) rgbEval.evaluate(offset,
                    fromBackgroundColor, toBackgroundColor));

            indicator.setFillColor((int) rgbEval.evaluate(offset,
                    fromIndicatorFillColor, toIndicatorFillColor));

            indicator.setPageColor((int) rgbEval.evaluate(offset,
                    fromIndicatorPageColor, toIndicatorPageColor));
        }

        @Override
        public void onPageSelected(final int i)
        {
            currentIndex = i;
        }

        @Override
        public void onPageScrollStateChanged(final int state)
        {
            final OnboardPageFragment page = ((PagerAdapter) pager
                    .getAdapter()).getFragment(currentIndex);

            if (page != null) { page.animate(); }
        }
    };


    private final class PagerAdapter extends FragmentPagerAdapter
    {

        private OnboardPageFragment[] fragments;

        PagerAdapter(final FragmentManager fm)
        {
            super(fm);
            fragments = new OnboardPageFragment[count];
        }

        @Override
        public final Fragment getItem(final int i)
        {
            final OnboardPageFragment fragment = OnboardPageFragment.newInstance(i, animatePages[i]);
            fragments[i] = fragment;
            animatePages[i] = false;
            return fragment;
        }

        @Override
        public final int getCount()
        {
            return count;
        }

        final OnboardPageFragment getFragment(final int i)
        {
            return fragments[i];
        }
    }
}
