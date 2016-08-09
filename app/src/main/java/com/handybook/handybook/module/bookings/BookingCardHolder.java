package com.handybook.handybook.module.bookings;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingCardHolder extends RecyclerView.ViewHolder
{

    private Booking mBooking;

    @Bind(R.id.text_service_name)
    TextView mTextServiceName;
    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;
    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    View.OnClickListener mOnClickListener;

    public BookingCardHolder(View itemView, View.OnClickListener clickListener)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnClickListener = clickListener;
    }

    public void bindToBooking(@NonNull final Booking booking)
    {
        mBooking = booking;
        mTextServiceName.setText(mBooking.getServiceName());

        if (!booking.isRecurring())
        {
            mTextServiceName.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextServiceName.setVisibility(View.GONE);
        }

        mTextBookingTitle.setText(BookingHelper.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingHelper.getSubtitle(mBooking, itemView.getContext()));

        itemView.setTag(mBooking);
        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (mOnClickListener != null)
                {
                    mOnClickListener.onClick(v);
                }
            }
        });
    }


}
