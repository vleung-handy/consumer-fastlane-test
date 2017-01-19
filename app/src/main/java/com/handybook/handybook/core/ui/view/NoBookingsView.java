package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.booking.util.BookingUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Just a dumb little view that shows static information to the user when there are
 * active plans, but no generated bookings.
 */
public class NoBookingsView extends FrameLayout
{
    @Bind(R.id.text_date)
    TextView mTextDate;

    @Bind(R.id.text_time)
    TextView mTextTime;

    @Bind(R.id.container_recurrence_booking)
    LinearLayout mContainerRecurrenceBooking;

    @Bind(R.id.container_regular_booking)
    LinearLayout mContainerRegularBooking;

    @Bind(R.id.image_main)
    ImageView mImageMain;

    public NoBookingsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        inflate(getContext(), R.layout.no_bookings_view, this);
        ButterKnife.bind(this);
    }

    /**
     * Takes in a list of recurring bookings, and figures out when the next one is going to be.
     *
     * @param bookings
     */
    public void bindForNoRecurringBookings(@NonNull List<RecurringBooking> bookings)
    {

        if (bookings.isEmpty())
        {
            bindForNoBookingsAtAll();
            return;
        }

        //sort the bookings according to next start date
        Collections.sort(bookings, new Comparator<RecurringBooking>()
        {
            @Override
            public int compare(final RecurringBooking lhs, final RecurringBooking rhs)
            {
                return lhs.getDateStart().compareTo(rhs.getDateStart());
            }
        });

        RecurringBooking booking = bookings.get(0);

        mTextDate.setText(DateTimeUtils.getDayMonthDay(booking.getDateStart()));
        mTextTime.setText(BookingUtil.getRecurrenceSubTitle2(booking));

        mContainerRecurrenceBooking.setVisibility(VISIBLE);
        mContainerRegularBooking.setVisibility(GONE);
        mImageMain.setImageResource(R.drawable.img_clean_home);
    }

    public void bindForNoBookingsAtAll()
    {
        mContainerRecurrenceBooking.setVisibility(GONE);
        mContainerRegularBooking.setVisibility(VISIBLE);
        mImageMain.setImageResource(R.drawable.img_dirty_home);
    }
}