package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    @Override
    protected int getEntryTitleTextResourceId()
    {
        return R.string.entry_info;
    }

    @Override
    protected int getEntryActionTextResourceId()
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction()
    {
        return true;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            view.entryText.setText(entryInfo + " " + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: edit entry info
    }
}
