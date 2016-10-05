package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentLaundry extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.laundry;
    }

//
//    @Override
//    protected void updateActionTextView(
//            @NonNull final Booking booking, @NonNull final TextView actionTextView
//    )
//    {
//        if (booking.isPast())
//        {
//            actionTextView.setVisibility(View.GONE);
//            return;
//        }
//        actionTextView.setVisibility(View.VISIBLE);
//        actionTextView.setText(R.string.set_dropoff);
//        actionTextView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(final View v)
//            {
//                onActionClick();
//            }
//        });
//    }

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

    protected void onActionClick()
    {
        //TODO: Edit laundry drop off associated with booking
    }
}
