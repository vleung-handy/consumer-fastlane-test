package com.handybook.handybook.module.bookings;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.util.BookingUtil;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used in the history view
 */
public class BookingCardHolder extends RecyclerView.ViewHolder
{

    private Booking mBooking;

    @Bind(R.id.image_icon)
    ImageView mImageIcon;
    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;
    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    View.OnClickListener mOnClickListener;

    public BookingCardHolder(
            View itemView,
            View.OnClickListener clickListener
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnClickListener = clickListener;
    }

    public void bindToBooking(@NonNull final Booking booking)
    {
        mBooking = booking;
        mImageIcon.setVisibility(View.VISIBLE);

        Date endDate = mBooking.getEndDate();
        BookingUtil.IconType iconType = BookingUtil.IconType.OUTLINE;
        if (new Date().compareTo(endDate) > 0)
        {
            iconType = BookingUtil.IconType.GRAY;
        }

        mImageIcon.setImageResource(BookingUtil.getIconForService(mBooking, iconType));
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
