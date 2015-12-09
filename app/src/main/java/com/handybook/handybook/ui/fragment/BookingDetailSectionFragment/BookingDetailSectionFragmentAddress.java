package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentAddress extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.address;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return true;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final Booking.Address address = booking.getAddress();
        getSectionView().getEntryText().setText(TextUtils.formatAddress(address.getAddress1(),
                address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditAddressActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
