package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.library.util.TextUtils;

public class BookingDetailSectionFragmentAddress extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.address;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final Booking.Address address = booking.getAddress();
        getSectionView().getEntryText().setText(TextUtils.formatAddress(address.getAddress1(),
                                                                        address.getAddress2(),
                                                                        address.getCity(),
                                                                        address.getState(),
                                                                        address.getZip()
        ));
    }

    @Override
    protected void updateActionTextView(
            @NonNull final Booking booking,
            @NonNull final TextView actionTextView
    )
    {
        if (booking.isPast())
        {
            actionTextView.setVisibility(View.GONE);
            return;
        }
        actionTextView.setVisibility(View.VISIBLE);
        actionTextView.setText(R.string.edit);
        actionTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onActionClick();
            }
        });
    }

    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditAddressActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
