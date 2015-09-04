package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

import java.util.ArrayList;

public class BookingDetailSectionFragmentExtras extends BookingDetailSectionFragment
{

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(R.string.extras);
        view.entryActionText.setText(R.string.edit);

        //TODO: Add gfx, the extras data has the relevant gfx

        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        if (extras != null && extras.size() > 0)
        {
            String extraInfo = "";

            for (int i = 0; i < extras.size(); i++)
            {
                final Booking.ExtraInfo info = extras.get(i);
                extraInfo += info.getLabel();

                if (i < extras.size() - 1)
                {
                    extraInfo += ", ";
                }
            }

            view.entryText.setText(extraInfo);
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
