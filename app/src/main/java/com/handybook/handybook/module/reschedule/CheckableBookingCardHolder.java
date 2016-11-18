package com.handybook.handybook.module.reschedule;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.util.BookingUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CheckableBookingCardHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.booking_item_check)
    CheckBox mCheckBox;
    @Bind(R.id.booking_title)
    TextView mTitle;
    @Bind(R.id.booking_subtitle)
    TextView mSubtitle;

    public CheckableBookingCardHolder(
            final View itemView,
            final CompoundButton.OnCheckedChangeListener checkListener
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mCheckBox.setOnCheckedChangeListener(checkListener);
    }

    public void bindToBooking(
            @NonNull final Booking booking,
            final boolean checked
    )
    {
        mCheckBox.setChecked(checked);
        mTitle.setText(BookingUtil.getTitle(booking));
        mSubtitle.setText(BookingUtil.getSubtitle(booking, itemView.getContext()));
        mCheckBox.setTag(booking);
    }
}
