package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentNoteToPro extends BookingDetailSectionFragment
{

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(R.string.pro_note);
        view.entryActionText.setText(R.string.add);

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
        //TODO:
    }
}
