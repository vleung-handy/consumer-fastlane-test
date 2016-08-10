package com.handybook.handybook.module.bookings;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.util.BookingUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingCardHolder extends RecyclerView.ViewHolder
{

    private Booking mBooking;

    @Bind(R.id.image_icon)
    ImageView mImageIcon;
    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;
    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    List<Service> mServices;
    View.OnClickListener mOnClickListener;

    public BookingCardHolder(
            View itemView,
            View.OnClickListener clickListener,
            List<Service> services
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnClickListener = clickListener;
        mServices = services;
    }

    public void bindToBooking(@NonNull final Booking booking)
    {
        mBooking = booking;
        mImageIcon.setVisibility(View.VISIBLE);

        if (mServices != null)
        {
            String machineName = BookingUtil.findParentService(booking, mServices);
            mImageIcon.setImageResource(BookingUtil.getIconForService(machineName));
        }
        else
        {
            //this will give us back the default cleaning icon
            mImageIcon.setImageResource(BookingUtil.getIconForService(null));
        }

        mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingUtil.getSubtitle(mBooking, itemView.getContext()));

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
