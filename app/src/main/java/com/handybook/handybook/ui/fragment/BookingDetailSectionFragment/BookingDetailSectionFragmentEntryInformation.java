package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        System.out.println("This update display WAS overridden, it is of fragment entry information type");

        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            bookingDetailSectionView.entryTitle.setText(R.string.entry_info);
            bookingDetailSectionView.entryText.setText(entryInfo + " " + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
            bookingDetailSectionView.entryActionText.setText(R.string.edit);
        }
        else
        {
            bookingDetailSectionView.setVisibility(View.GONE);
        }

    }


    @Override
    protected void testDoThing()
    {
        System.out.println("entry information fragment on click");
    }


}
