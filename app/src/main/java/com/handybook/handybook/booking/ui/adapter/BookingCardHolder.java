package com.handybook.handybook.booking.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.util.BookingUtil;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used in the history view
 */
public class BookingCardHolder extends RecyclerView.ViewHolder {

    private Booking mBooking;

    @Bind(R.id.image_icon)
    ImageView mImageIcon;
    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;
    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    View.OnClickListener mOnClickListener;

    /**
     * used to determine how to display the subtitle
     */
    private final boolean mIsBookingHoursClarificationExperimentEnabled;

    /**
     * @param isBookingHoursClarificationExperimentEnabled this is passed here because don't want to pass
     *                                                     the entire config object or inject config manager,
     *                                                     and don't want to pass it to bindBooking()
     *                                                     since it should be constant
     *                                                     throughout this holder's lifetime
     */
    public BookingCardHolder(
            View itemView,
            View.OnClickListener clickListener,
            boolean isBookingHoursClarificationExperimentEnabled
    ) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnClickListener = clickListener;
        mIsBookingHoursClarificationExperimentEnabled
                = isBookingHoursClarificationExperimentEnabled;
    }

    public void bindToBooking(@NonNull final Booking booking) {
        mBooking = booking;
        mImageIcon.setVisibility(View.VISIBLE);

        Date endDate = mBooking.getEndDate();
        BookingUtil.IconType iconType = BookingUtil.IconType.OUTLINE;
        if (new Date().compareTo(endDate) > 0) {
            iconType = BookingUtil.IconType.GRAY;
        }

        mImageIcon.setImageResource(BookingUtil.getIconForService(mBooking, iconType));
        mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingUtil.getSubtitle(
                mBooking,
                itemView.getContext(),
                mIsBookingHoursClarificationExperimentEnabled
        ));

        itemView.setTag(mBooking);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });
    }
}
