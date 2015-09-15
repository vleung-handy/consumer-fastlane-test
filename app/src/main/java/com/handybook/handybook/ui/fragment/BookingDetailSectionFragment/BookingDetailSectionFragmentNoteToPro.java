package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingNoteToProActivity;

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
            view.entryText.setText("");
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Edit the note to pro

        //BookingNoteToProActivity

        final Intent intent = new Intent(getActivity(), BookingNoteToProActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        startActivity(intent);
    }
}
