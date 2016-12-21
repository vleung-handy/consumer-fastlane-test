package com.handybook.handybook.booking.reschedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.ui.view.BookingListItem;

public class BookingItemHolder extends RecyclerView.ViewHolder
{
    BookingListItem mBookingListItem;

    public BookingItemHolder(final View itemView)
    {
        super(itemView);
        mBookingListItem = (BookingListItem) itemView;
    }

    public void bindToBooking(@NonNull final Booking booking)
    {
        mBookingListItem.bindToBooking(booking);
    }
}
