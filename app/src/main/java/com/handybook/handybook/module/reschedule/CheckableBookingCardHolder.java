package com.handybook.handybook.module.reschedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.library.ui.view.CheckableImageButton;
import com.handybook.handybook.util.BookingUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CheckableBookingCardHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.booking_item_check)
    CheckableImageButton mCheckableImageButton;
    @Bind(R.id.booking_title)
    TextView mTitle;
    @Bind(R.id.booking_subtitle)
    TextView mSubtitle;

    public CheckableBookingCardHolder(
            final View itemView,
            final View.OnClickListener clickListener
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mCheckableImageButton.setListener(clickListener);
    }

    public void bindToBooking(
            @NonNull final Booking booking,
            final boolean checked
    )
    {
        mCheckableImageButton.setChecked(checked);
        mTitle.setText(BookingUtil.getTitle(booking));
        mSubtitle.setText(BookingUtil.getSubtitle(booking, itemView.getContext()));
        mCheckableImageButton.setTag(booking);
    }
}
