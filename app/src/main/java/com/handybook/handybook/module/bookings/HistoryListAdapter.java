package com.handybook.handybook.module.bookings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;

import java.util.List;

/**
 */
public class HistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final String TAG = HistoryListAdapter.class.getName();

    protected List<Booking> mBookings;
    protected View.OnClickListener mOnClickListener;
    protected List<Service> mServices;

    public HistoryListAdapter(final List<Booking> bookings, final View.OnClickListener onClickListener, final List<Service> services)
    {
        mBookings = bookings;
        mOnClickListener = onClickListener;
        mServices = services;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_booking_list_item, parent, false);

        return new BookingCardHolder(itemView, mOnClickListener, mServices);
    }


    public void setServices(final List<Service> services)
    {
        mServices = services;
        notifyDataSetChanged();
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
