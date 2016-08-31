package com.handybook.handybook.module.bookings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.List;

/**
 */
public class HistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Booking> mBookings;
    private View.OnClickListener mOnClickListener;

    public HistoryListAdapter(final List<Booking> bookings, final View.OnClickListener onClickListener)
    {
        mBookings = bookings;
        mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_booking_list_item, parent, false);

        return new BookingCardHolder(itemView, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        ((BookingCardHolder) holder).bindToBooking(mBookings.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mBookings == null ? 0 : mBookings.size();
    }

}
