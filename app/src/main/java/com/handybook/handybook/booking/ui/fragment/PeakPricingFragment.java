package com.handybook.handybook.booking.ui.fragment;

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

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.PeakPriceInfo;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class PeakPricingFragment extends BookingFlowFragment
{
    static final String EXTRA_RESCHEDULE_PRICE_TABLE = "com.handy.handy.EXTRA_RESCHEDULE_PRICE_TABLE";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_ALL = "com.handy.handy.EXTRA_RESCHEDULE_ALL";
    static final String EXTRA_FOR_VOUCHER = "com.handy.handy.EXTRA_FOR_VOUCHER";
    static final String STATE_PRICE_TABLE = "PRICE_TABLE";

    private ArrayList<ArrayList<PeakPriceInfo>> mPeakPriceTable;
    private int mPageIndex;
    private boolean mIsForReschedule;
    private boolean mIsForVoucher;
    private boolean mIsForRescheduleAll;
    private Booking mBookingToReschedule;

    @Bind(R.id.skip_button)
    Button mSkipButton;
    @Bind(R.id.date_text)
    TextView mDateText;
    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.pager)
    ViewPager mDatePager;
    @Bind(R.id.arrow_left)
    ImageView mArrowLeft;
    @Bind(R.id.arrow_right)
    ImageView mArrowRight;

    public static PeakPricingFragment newInstance(final boolean forVoucher)
    {
        return newInstance(null, null, false, forVoucher);
    }

    public static PeakPricingFragment newInstance(
            final ArrayList<ArrayList<PeakPriceInfo>> reschedulePriceTable,
            final Booking rescheduleBooking,
            final boolean rescheduleAll
    )
    {
        return newInstance(reschedulePriceTable, rescheduleBooking, rescheduleAll, false);
    }

    public static PeakPricingFragment newInstance(
            final ArrayList<ArrayList<PeakPriceInfo>> reschedulePriceTable,
            final Booking rescheduleBooking,
            final boolean rescheduleAll, final boolean forVoucher
    )
    {
        final PeakPricingFragment fragment = new PeakPricingFragment();

        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_RESCHEDULE_PRICE_TABLE, reschedulePriceTable);
        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putBoolean(EXTRA_RESCHEDULE_ALL, rescheduleAll);
        args.putBoolean(EXTRA_FOR_VOUCHER, forVoucher);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        ArrayList<ArrayList<PeakPriceInfo>> reschedulePriceTable = null;

        if (args != null)
        {
            reschedulePriceTable = (ArrayList<ArrayList<PeakPriceInfo>>)
                    args.getSerializable(EXTRA_RESCHEDULE_PRICE_TABLE);
            mIsForVoucher = args.getBoolean(EXTRA_FOR_VOUCHER);
        }

        if (reschedulePriceTable != null)
        {
            mIsForReschedule = true;
            mPeakPriceTable = reschedulePriceTable;
            mBookingToReschedule = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
            mIsForRescheduleAll = getArguments().getBoolean(EXTRA_RESCHEDULE_ALL);
        }
        else
        {
            mPeakPriceTable = bookingManager.getCurrentQuote().getPeakPriceTable();
        }

        if (savedInstanceState != null)
        {
            mPeakPriceTable = (ArrayList<ArrayList<PeakPriceInfo>>)
                    savedInstanceState.getSerializable(STATE_PRICE_TABLE);
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_peak_pricing, container, false);

        ButterKnife.bind(this, view);

        if (mIsForVoucher || mIsForReschedule)
        {
            mSkipButton.setVisibility(View.GONE);
            mHeaderText.setText(R.string.peak_price_info_unavailable);
        }
        else
        {
            mSkipButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    continueBookingFlow();
                }
            });
        }

        mDatePager.setOnPageChangeListener(pageListener);
        mPageIndex = getStartIndex();

        mArrowLeft.setOnTouchListener(arrowTouched);
        mArrowRight.setOnTouchListener(arrowTouched);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mDatePager.setAdapter(new PeakPriceTablePagerAdapter(getActivity().getSupportFragmentManager()));
        mDatePager.setCurrentItem(mPageIndex);
        updateDateHeader();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_PRICE_TABLE, mPeakPriceTable);
    }

    private int getStartIndex()
    {
        final Date startDate = mIsForReschedule ? mBookingToReschedule.getStartDate()
                : bookingManager.getCurrentTransaction().getStartDate();

        for (int i = 0; i < mPeakPriceTable.size(); i++)
        {
            for (final PeakPriceInfo info : mPeakPriceTable.get(i))
            {
                if (Utils.equalDates(startDate, info.getDate()))
                {
                    return i;
                }
            }
        }

        return 0;
    }

    private void updateDateHeader()
    {
        //we want to display the time using the booking location's time zone
        final BookingRequest currentRequest = bookingManager.getCurrentRequest();
        String timezone = currentRequest != null ? currentRequest.getTimeZone() : null;
        mDateText.setText(
                DateTimeUtils.formatDate(
                        mPeakPriceTable.get(mPageIndex).get(0).getDate(),
                        "EEEE',' MMMM d",
                        timezone
                )
        );

        mArrowRight.setVisibility(View.VISIBLE);
        mArrowLeft.setVisibility(View.VISIBLE);

        if (mPageIndex == mDatePager.getAdapter().getCount() - 1)
        {
            mArrowRight.setVisibility(View.INVISIBLE);
        }
        else if (mPageIndex == 0) { mArrowLeft.setVisibility(View.INVISIBLE); }
    }

    private View.OnTouchListener arrowTouched = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View v, final MotionEvent event)
        {
            final ImageView view = (ImageView) v;

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    view.setColorFilter(getResources().getColor(R.color.handy_blue_pressed),
                            PorterDuff.Mode.SRC_ATOP);

                    view.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    int currentItem = mDatePager.getCurrentItem();
                    final int count = mDatePager.getAdapter().getCount();

                    if (v == mArrowLeft)
                    {
                        mDatePager.setCurrentItem(Math.max(--currentItem, 0), true);
                    }
                    else if (v == mArrowRight)
                    {
                        mDatePager.setCurrentItem(Math.min(++currentItem, count), true);
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
            = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(final int i, final float v, final int i2)
        {

        }

        @Override
        public void onPageSelected(final int i)
        {
            mPageIndex = i;
            updateDateHeader();
        }

        @Override
        public void onPageScrollStateChanged(final int i)
        {

        }
    };


    private final class PeakPriceTablePagerAdapter extends FragmentPagerAdapter
    {

        PeakPriceTablePagerAdapter(final FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public final Fragment getItem(final int i)
        {
            return PeakPricingTableFragment.newInstance(i, mPeakPriceTable,
                    mBookingToReschedule, mIsForRescheduleAll, mIsForVoucher);
        }

        @Override
        public final int getCount()
        {
            return mPeakPriceTable.size();
        }
    }
}
