package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentLaundry extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.laundry;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.set_dropoff;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return false;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        if (booking.getLaundryStatus() == null
                || booking.getLaundryStatus() == Booking.LaundryStatus.SKIPPED)
        {
            view.setVisibility(View.GONE);
        }
        else
        {
            //TODO: Show laundry information? not sure what should be in here
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Edit laundry drop off associated with booking
    }
}
