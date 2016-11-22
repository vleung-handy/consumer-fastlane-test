package com.handybook.handybook.ui.view;


import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.util.BookingUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingListItem extends FrameLayout
{
    private Booking mBooking;

    @Bind(R.id.image_icon)
    ImageView mImageIcon;
    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;
    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    private View.OnClickListener mOnClickListener;

    public BookingListItem(
            Context context,
            View.OnClickListener clickListener,
            Booking booking
    )
    {
        super(context);
        mOnClickListener = clickListener;
        mBooking = booking;
        init();
    }

    void init()
    {
        inflate(getContext(), R.layout.layout_booking_list_item, this);
        ButterKnife.bind(this);
        bindToBooking();
    }

    public void setClickListener(final OnClickListener onClickListener)
    {
        mOnClickListener = onClickListener;
    }

    public void setBooking(final Booking booking)
    {
        mBooking = booking;
    }

    public void bindToBooking()
    {
        if (mBooking == null)
        {
            return;
        }

        mImageIcon.setVisibility(View.VISIBLE);
        mImageIcon.setImageResource(BookingUtil.getIconForService(mBooking, BookingUtil.IconType.OUTLINE));

        mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingUtil.getSubtitle(mBooking, getContext()));

        this.setOnClickListener(new View.OnClickListener()
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

    public Booking getBooking()
    {
        return mBooking;
    }
}
