package com.handybook.handybook.module.bookings;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

/**
 */
public class ActiveBookingHolder extends RecyclerView.ViewHolder
{
    private View.OnTouchListener mMapTouchListener;

    public ActiveBookingHolder(final View itemView, View.OnTouchListener onTouchListener)
    {
        super(itemView);
        mMapTouchListener = onTouchListener;
    }

    public void bindActiveBooking(FragmentManager fragmentManager, @NonNull final Booking mBooking)
    {
        ActiveBookingFragment frag = ActiveBookingFragment.newInstance(mBooking);
        frag.setMapTouchListener(mMapTouchListener);
        fragmentManager.beginTransaction()
                .replace(R.id.active_booking_container, frag)
                .commit();


    }

}
