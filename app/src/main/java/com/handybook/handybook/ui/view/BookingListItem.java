package com.handybook.handybook.ui.view;


import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.util.BookingUtil;

import java.util.List;

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

    private List<Service> mServices;
    private View.OnClickListener mOnClickListener;

    public BookingListItem(
            Context context,
            View.OnClickListener clickListener,
            List<Service> services,
            Booking booking
    )
    {
        super(context);
        mOnClickListener = clickListener;
        mServices = services;
        mBooking = booking;
        init();
    }

    void init()
    {
        inflate(getContext(), R.layout.layout_booking_list_item, this);
        ButterKnife.bind(this);
        bindToBooking();
    }

    public void bindToBooking()
    {
        mImageIcon.setVisibility(View.VISIBLE);

        if (mServices != null)
        {
            String machineName = BookingUtil.findParentService(mBooking, mServices);
            mImageIcon.setImageResource(BookingUtil.getIconForService(machineName, BookingUtil.IconType.OUTLINE));
        }
        else
        {
            //this will give us back the default cleaning icon
            mImageIcon.setImageResource(BookingUtil.getIconForService(null, BookingUtil.IconType.OUTLINE));
        }

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
