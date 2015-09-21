package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentProInformation extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.professional;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        if (booking.hasAssignedProvider())
        {
            return R.string.blank;
        }
        else
        {
            return R.string.request_pro;
        }
    }

    @Override
    protected boolean hasEnabledAction()
    {
        if (booking.hasAssignedProvider())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        final Booking.Provider pro = booking.getProvider();

        if (booking.hasAssignedProvider())
        {
            view.entryText.setText(pro.getFullName());
        }
        else
        {
            //if no pro has been assigned indicate the ability to request a pro
            view.entryText.setText(R.string.pro_assignment_pending);
        }
    }


    @Override
    protected void onActionClick()
    {
        //If no pro assigned can request a pro
        if (booking.hasAssignedProvider())
        {
            //how will we handle the call / phone action buttons....  , are they going to be in here?
        }
        else
        {



            //need a new UI where we request the requestable pros, then display them, then get the result back

//            final Intent intent = new Intent(getActivity(), BookingEditNoteToProActivity.class);
//            intent.putExtra(BundleKeys.BOOKING, this.booking);
//            getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
        }
    }

}
