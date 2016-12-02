package com.handybook.handybook.module.reschedule;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.ui.view.BookingListItem;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingItemHolder>
{
    private List<Booking> mUpcomingBookings;
    private View.OnClickListener mClickListener;

    /**
     * used by list item views to determine how to display the subtitle
     */
    private boolean mIsBookingHoursClarificationExperimentEnabled;

    public BookingListAdapter(
            final List<Booking> upcomingBookings,
            final View.OnClickListener clickListener,
            final boolean isBookingHoursClarificationExperimentEnabled
    )
    {
        mUpcomingBookings = upcomingBookings;
        mClickListener = clickListener;
        mIsBookingHoursClarificationExperimentEnabled = isBookingHoursClarificationExperimentEnabled;
    }

    @Override
    public BookingItemHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {

        BookingListItem item = new BookingListItem(
                parent.getContext(),
                null,
                null,
                mIsBookingHoursClarificationExperimentEnabled
        );

        item.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        item.setClickListener(mClickListener);
        return new BookingItemHolder(item);
    }

    @Override
    public void onBindViewHolder(final BookingItemHolder holder, final int position)
    {
        holder.bindToBooking(mUpcomingBookings.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mUpcomingBookings == null ? 0 : mUpcomingBookings.size();
    }

}
