package com.handybook.handybook.module.bookings;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.List;

/**
 * This adapter will display a simple list of bookings. Perfect for usage in displaying
 * past bookings.
 */
public class SimpleBookingListAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final String TAG = SimpleBookingListAdapater.class.getName();

    protected List<Booking> mBookings;
    protected View.OnClickListener mOnClickListener;
    protected FragmentManager mFragmentManager;

    public SimpleBookingListAdapater(
            FragmentManager fragmentManager,
            @NonNull final List<Booking> bookings,
            final View.OnClickListener clickListener
    )
    {
        mFragmentManager = fragmentManager;
        mOnClickListener = clickListener;
        mBookings = bookings;
    }

    public List<Booking> getBookings()
    {
        return mBookings;
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
        Log.d(TAG, "onBindViewHolder: " + mBookings.get(position).getServiceName());
        ((BookingCardHolder) holder).bindToBooking(mBookings.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mBookings == null ? 0 : mBookings.size();
    }

}
