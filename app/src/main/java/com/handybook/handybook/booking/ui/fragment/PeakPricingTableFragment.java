package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.PeakPriceInfo;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class PeakPricingTableFragment extends BookingFlowFragment
{
    static final String EXTRA_PEAK_PRICE_INDEX = "com.handy.handy.EXTRA_PEAK_PRICE_INDEX";
    static final String EXTRA_PEAK_PRICE_TABLE = "com.handy.handy.EXTRA_PEAK_PRICE_TABLE";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_ALL = "com.handy.handy.EXTRA_RESCHEDULE_ALL";
    static final String EXTRA_FOR_VOUCHER = "com.handy.handy.EXTRA_FOR_VOUCHER";

    @PeakPriceInfo.Recurrence
    private String mRecurrence;
    private ArrayList<ArrayList<PeakPriceInfo>> mPeakPriceTable;
    private int mPageIndex;
    private boolean mIsForReschedule;
    private boolean mIsForVoucher;
    private boolean mIsForRescheduleAll;

    private Booking mBookingToReschedule;
    @Bind(R.id.peak_pricing_table_container)
    LinearLayout mTimeSlotContainer;
    private String mCurrencyCharacter;
    private User mUser;
    private BookingQuote mQuote;
    private ArrayList<PeakPriceInfo> mPriceTablePage;

    public static PeakPricingTableFragment newInstance(
            final int index,
            final ArrayList<ArrayList<PeakPriceInfo>> peakPriceTable,
            final Booking rescheduleBooking,
            final boolean rescheduleAll, final boolean forVoucher
    )
    {
        final PeakPricingTableFragment fragment = new PeakPricingTableFragment();
        final Bundle args = new Bundle();
        args.putInt(EXTRA_PEAK_PRICE_INDEX, index);
        args.putSerializable(EXTRA_PEAK_PRICE_TABLE, peakPriceTable);
        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putBoolean(EXTRA_RESCHEDULE_ALL, rescheduleAll);
        args.putBoolean(EXTRA_FOR_VOUCHER, forVoucher);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        mPageIndex = args.getInt(EXTRA_PEAK_PRICE_INDEX, 0);
        mIsForVoucher = args.getBoolean(EXTRA_FOR_VOUCHER, false);
        mPeakPriceTable = (ArrayList<ArrayList<PeakPriceInfo>>) args.getSerializable(EXTRA_PEAK_PRICE_TABLE);
        mBookingToReschedule = args.getParcelable(EXTRA_RESCHEDULE_BOOKING);
        mIsForRescheduleAll = args.getBoolean(EXTRA_RESCHEDULE_ALL);
        mIsForReschedule = mBookingToReschedule != null;
        mQuote = bookingManager.getCurrentQuote();
        mUser = userManager.getCurrentUser();
        mCurrencyCharacter = mIsForReschedule ? mUser.getCurrencyChar() : mQuote.getCurrencyChar();
        mPriceTablePage = mPeakPriceTable.get(mPageIndex);
        mRecurrence = PeakPriceInfo.recurrenceFrom(
                mIsForVoucher || mIsForReschedule ? -1
                        : bookingManager.getCurrentTransaction().getRecurringFrequency());
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(
                R.layout.fragment_peak_pricing_table,
                container,
                false
        );
        ButterKnife.bind(this, view);
        for (final PeakPriceInfo eachInfo : mPriceTablePage)
        {
            final View eachRow = createRowView(eachInfo);
            mTimeSlotContainer.addView(eachRow, mTimeSlotContainer.getChildCount());
        }
        return view;
    }

    private View createRowView(final PeakPriceInfo peakPriceInfo)
    {
        final Date date = peakPriceInfo.getDate();
        final PeakPriceInfo.Type type = peakPriceInfo.getType(mRecurrence);
        final float price = peakPriceInfo.getPrice(mRecurrence);

        final View row = getActivity().getLayoutInflater().inflate(R.layout.table_item_price, null, false);
        final TextView timeText = (TextView) row.findViewById(R.id.time_text);
        final TextView priceText = (TextView) row.findViewById(R.id.price_text);

        //we want to display the time using the booking location's time zone
        timeText.setText(DateTimeUtils.formatDate(date, "h:mm aaa", bookingManager.getCurrentRequest().getTimeZone()
        ));
        priceText.setText(TextUtils.formatPrice(price, mCurrencyCharacter, null));

        switch (type)
        {
            case PEAK_PRICE:
                if (mIsForReschedule || mIsForVoucher)
                {
                    disableRow(row);
                }
                else
                {
                    priceText.setTextColor(getResources().getColor(R.color.error_red));
                }
                break;
            case REG_PRICE:
                priceText.setTextColor(getResources().getColor(R.color.price_green));
                if (mIsForReschedule || mIsForVoucher)
                {
                    priceText.setText(getString(R.string.available));
                }
                break;
            case DISABLED_PRICE:
                disableRow(row);
            default:
        }

        if (mRecurrence.equals(PeakPriceInfo.RECURRENCE_NONRECURRING)
                && (type.equals(PeakPriceInfo.Type.PEAK_PRICE) || type.equals(PeakPriceInfo.Type.REG_PRICE))
                )
        {
            row.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (mIsForReschedule)
                    {
                        rescheduleBooking(mBookingToReschedule, date, mIsForRescheduleAll);
                    }
                    else
                    {
                        mQuote.setStartDate(date);
                        continueBookingFlow();
                    }
                }
            });
        }
        return row;
    }

    private void disableRow(final View row)
    {
        final TextView timeText = (TextView) row.findViewById(R.id.time_text);
        final TextView priceText = (TextView) row.findViewById(R.id.price_text);
        priceText.setTextColor(getResources().getColor(R.color.black_pressed));
        priceText.setText(getString(R.string.unavailable));
        timeText.setTextColor(getResources().getColor(R.color.black_pressed));
    }
}
