package com.handybook.handybook.core.ui.view;


import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.util.BookingUtil;

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

    /**
     * used to determine how to display the subtitle
     */
    private final boolean mIsBookingHoursClarificationExperimentEnabled;

    /**
     * @param isBookingHoursClarificationExperimentEnabled this is passed here because don't want to
     *                                                     pass the entire config object or inject
     *                                                     config manager, and don't want to pass it
     *                                                     to bindToBooking() since it should be
     *                                                     constant throughout this item's lifetime
     */
    public BookingListItem(
            Context context,
            View.OnClickListener clickListener,
            Booking booking,
            boolean isBookingHoursClarificationExperimentEnabled
    )
    {
        super(context);
        mOnClickListener = clickListener;
        mBooking = booking;
        mIsBookingHoursClarificationExperimentEnabled = isBookingHoursClarificationExperimentEnabled;
        init();
    }

    void init()
    {
        inflate(getContext(), R.layout.layout_booking_list_item, this);
        ButterKnife.bind(this);
        bindToBooking(mBooking);
    }

    public void setClickListener(final OnClickListener onClickListener)
    {
        mOnClickListener = onClickListener;
    }

    public void bindToBooking(Booking booking)
    {
        mBooking = booking;
        if (mBooking == null)
        {
            return;
        }

        mImageIcon.setVisibility(View.VISIBLE);
        mImageIcon.setImageResource(BookingUtil.getIconForService(mBooking, BookingUtil.IconType.OUTLINE));

        mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingUtil.getSubtitle(
                mBooking,
                getContext(),
                mIsBookingHoursClarificationExperimentEnabled
        ));

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
