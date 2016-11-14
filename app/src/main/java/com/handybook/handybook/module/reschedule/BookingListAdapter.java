package com.handybook.handybook.module.reschedule;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.library.ui.view.CheckableImageButton;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<CheckableBookingCardHolder>
{
    private List<Booking> mUpcomingBookings;
    private int mCheckedIndex = -1;

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

        return new CheckableBookingCardHolder(itemView, new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (v.getTag() != null)
                {
                    Booking booking = (Booking) v.getTag();
                    int index = mUpcomingBookings.indexOf(booking);

                    CheckableImageButton imageButton = (CheckableImageButton) itemView.findViewById(
                            R.id.booking_item_check);

                    if (mCheckedIndex != index)
                    {
                        //that means this index is being checked
                        if (!imageButton.isChecked())
                        {
                            throw new IllegalStateException(
                                    "The list adapter thinks this item is not checked, but the item thinks it's checked.");
                        }
                        mCheckedIndex = index;

//                        TODO: JIA: nice to have, calling notifyDataSetChanged will nullify any animations that are happening at this point
                        //including ripple effects. Figure out how to do this elegantly.
                        notifyDataSetChanged();
                    }
                    else
                    {
                        //this means the checked item just got clicked, reset the checked index.
                        mCheckedIndex = -1;
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public int getCheckedIndex()
    {
        return mCheckedIndex;
    }

    @Override
    public void onBindViewHolder(final CheckableBookingCardHolder holder, final int position)
    {
        holder.bindToBooking(mUpcomingBookings.get(position), mCheckedIndex == position);
    }

    @Override
    public int getItemCount()
    {
        return mUpcomingBookings == null ? 0 : mUpcomingBookings.size();
    }

}
