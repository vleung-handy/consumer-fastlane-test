package com.handybook.handybook.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.UserRecurringBooking;
import com.handybook.handybook.util.BookingUtil;
import com.handybook.handybook.util.DateTimeUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Just a dumb little view that shows static information to the user when there are
 * active plans, but no generated bookings.
 */
public class ActivePlanNoBookingView extends FrameLayout
{
    @Bind(R.id.text_date)
    TextView mTextDate;

    @Bind(R.id.text_time)
    TextView mTextTime;

    public ActivePlanNoBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        inflate(getContext(), R.layout.active_plan_no_booking_view, this);
        ButterKnife.bind(this);
    }

    /**
     * Takes in a list of recurring bookings, and figures out when the next one is going to be.
     *
     * @param bookings
     */
    public void bind(@NonNull List<UserRecurringBooking> bookings)
    {

        if (bookings.isEmpty())
        {
            return;
        }

        //sort the bookings according to next start date
        Collections.sort(bookings, new Comparator<UserRecurringBooking>()
        {
            @Override
            public int compare(final UserRecurringBooking lhs, final UserRecurringBooking rhs)
            {
                return lhs.getDateStart().compareTo(rhs.getDateStart());
            }
        });

        UserRecurringBooking booking = bookings.get(0);

        mTextDate.setText(DateTimeUtils.getDayMonthDay(booking.getDateStart()));
        mTextTime.setText(BookingUtil.getRecurrenceSubTitle2(booking));
    }
}
