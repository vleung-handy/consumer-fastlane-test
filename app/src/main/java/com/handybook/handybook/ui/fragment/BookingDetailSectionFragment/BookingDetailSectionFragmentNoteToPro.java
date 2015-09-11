package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentNoteToPro extends BookingDetailSectionFragment
{

    @Override
    protected int getEntryTitleTextResourceId()
    {
        return R.string.pro_note;
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
        final String proNote = booking.getProNote();
        if (proNote != null)
        {
            view.entryText.setText(proNote);
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Edit the note to pro
    }
}
