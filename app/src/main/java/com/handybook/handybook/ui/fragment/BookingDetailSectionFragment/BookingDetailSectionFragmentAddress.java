package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentAddress extends BookingDetailSectionFragment
{
    public static final String TAG= "BookingDetailSectionFragmentAddress";

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
    protected boolean hasEnabledAction()
    {
        return false;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final Booking.Address address = booking.getAddress();
        view.entryText.setText(TextUtils.formatAddress(address.getAddress1(), address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Be able to edit the address
    }
}
