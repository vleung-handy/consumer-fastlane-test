package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentLaundry extends BookingDetailSectionFragment
{

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(R.string.laundry);
        view.entryActionText.setText(R.string.set_dropoff);

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
        //TODO:
    }
}
