package com.handybook.handybook.module.reschedule;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<CheckableBookingCardHolder>
{
    private List<Booking> mUpcomingBookings;
    private int mCheckedIndex = -1;
    private boolean mOnBind;

    public BookingListAdapter(final List<Booking> upcomingBookings)
    {
        mUpcomingBookings = upcomingBookings;
    }

    @Override
    public CheckableBookingCardHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View itemView = LayoutInflater.from(parent.getContext())
                                            .inflate(
                                                    R.layout.checkable_booking_list_item,
                                                    parent,
                                                    false
                                            );

        return new CheckableBookingCardHolder(
                itemView,
                new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(
                            final CompoundButton v,
                            final boolean isChecked
                    )
                    {
                        if (v.getTag() != null && !mOnBind)
                        {
                            Booking booking = (Booking) v.getTag();
                            int index = mUpcomingBookings.indexOf(booking);

                            if (isChecked)
                            {
                                //TODO: JIA: nice to have, calling notifyDataSetChanged will nullify any animations that are happening at this point
                                //including ripple effects. Figure out how to do this elegantly.

                                mCheckedIndex = index;
                                notifyDataSetChanged();
                            }
                            else
                            {
                                mCheckedIndex = -1;
                                notifyDataSetChanged();
                            }
                        }

                    }
                }
        );
    }

    public int getCheckedIndex()
    {
        return mCheckedIndex;
    }

    @Override
    public void onBindViewHolder(final CheckableBookingCardHolder holder, final int position)
    {
        mOnBind = true;
        holder.bindToBooking(mUpcomingBookings.get(position), mCheckedIndex == position);
        mOnBind = false;
    }

    @Override
    public int getItemCount()
    {
        return mUpcomingBookings == null ? 0 : mUpcomingBookings.size();
    }

}
