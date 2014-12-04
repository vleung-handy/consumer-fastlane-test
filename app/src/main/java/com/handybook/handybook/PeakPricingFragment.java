package com.handybook.handybook;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PeakPricingFragment extends BookingFlowFragment {
    private static final String STATE_PRICE_TABLE = "PRICE_TABLE";

    private ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakPriceTable;
    private int currentIndex;

    @InjectView(R.id.skip_button) Button skipButton;
    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.header_text) TextView headerText;
    @InjectView(R.id.pager) ViewPager datePager;
    @InjectView(R.id.arrow_left) ImageView arrowLeft;
    @InjectView(R.id.arrow_right) ImageView arrowRight;

    static PeakPricingFragment newInstance() {
        final PeakPricingFragment fragment = new PeakPricingFragment();
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            peakPriceTable = (ArrayList<ArrayList<BookingQuote.PeakPriceInfo>>)
                    savedInstanceState.getSerializable(STATE_PRICE_TABLE);
        }
        else peakPriceTable = bookingManager.getCurrentQuote().getPeakPriceTable();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_peak_pricing,container, false);

        ButterKnife.inject(this, view);

        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction.getRecurringFrequency() > 0) {
            skipButton.setVisibility(View.GONE);
            headerText.setText(R.string.peak_price_info_recur);
        }
        else skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                continueBookingFlow();
            }
        });

        datePager.setOnPageChangeListener(pageListener);
        currentIndex = getStartIndex();

        arrowLeft.setOnTouchListener(arrowTouched);
        arrowRight.setOnTouchListener(arrowTouched);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        datePager.setAdapter(new PeakPriceTablePagerAdapter(getActivity().getSupportFragmentManager()));
        datePager.setCurrentItem(currentIndex);
        updateDateHeader();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_PRICE_TABLE, peakPriceTable);
    }

    private int getStartIndex() {
        final Date startDate = bookingManager.getCurrentTransaction().getStartDate();

        for (int i = 0; i < peakPriceTable.size(); i++) {
            for (final BookingQuote.PeakPriceInfo info : peakPriceTable.get(i)) {
                if (Utils.equalDates(startDate, info.getDate())) {
                    return i;
                }
            }
        }

        return 0;
    }

    private void updateDateHeader() {
        dateText.setText(TextUtils.formatDate(peakPriceTable
                .get(currentIndex).get(0).getDate(), "EEEE',' MMMM d"));

        arrowRight.setVisibility(View.VISIBLE);
        arrowLeft.setVisibility(View.VISIBLE);

        if (currentIndex == datePager.getAdapter().getCount() - 1) {
            arrowRight.setVisibility(View.INVISIBLE);
        }
        else if (currentIndex == 0) arrowLeft.setVisibility(View.INVISIBLE);
    }

    private View.OnTouchListener arrowTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            final ImageView view = (ImageView)v;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setColorFilter(getResources().getColor(R.color.handy_blue_pressed),
                            PorterDuff.Mode.SRC_ATOP);

                    view.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    int currentItem = datePager.getCurrentItem();
                    final int count = datePager.getAdapter().getCount();

                    if (v == arrowLeft) {
                        datePager.setCurrentItem(Math.max(--currentItem, 0), true);
                    }
                    else if (v == arrowRight) {
                        datePager.setCurrentItem(Math.min(++currentItem, count), true);
                    }
                case MotionEvent.ACTION_CANCEL:
                    view.clearColorFilter();
                    view.invalidate();
                    break;
            }

            return true;
        }
    };

    private final ViewPager.OnPageChangeListener pageListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(final int i, final float v, final int i2) {

        }

        @Override
        public void onPageSelected(final int i) {
            currentIndex = i;
            updateDateHeader();
        }

        @Override
        public void onPageScrollStateChanged(final int i) {

        }
    };

    private final class PeakPriceTablePagerAdapter extends FragmentPagerAdapter {

        PeakPriceTablePagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public final Fragment getItem(final int i) {
            return PeakPricingTableFragment.newInstance(i, peakPriceTable);
        }

        @Override
        public final int getCount() {
            return peakPriceTable.size();
        }
    }
}
