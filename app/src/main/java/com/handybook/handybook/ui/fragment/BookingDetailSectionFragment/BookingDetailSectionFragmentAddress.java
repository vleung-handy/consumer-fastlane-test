package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentAddress extends BookingDetailSectionFragment
{

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(R.string.address);
        view.entryActionText.setText(R.string.edit);

        final Booking.Address address = booking.getAddress();
        view.entryText.setText(TextUtils.formatAddress(address.getAddress1(), address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));
    }

    @Override
    protected void onActionClick()
    {
        //TODO:
    }
}
