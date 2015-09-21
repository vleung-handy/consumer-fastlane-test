package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.BookingDetailSectionExtrasView;

import butterknife.Bind;

public class BookingDetailSectionFragmentExtras extends BookingDetailSectionFragment
{
    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionExtrasView view;

    @Override
    protected int getFragmentResourceId(){ return R.layout.fragment_booking_detail_section_extras; }

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.extras;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction()
    {
        return false;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        view.updateExtrasDisplay(booking);
    }

    @Override
    protected void setupClickListeners(Booking booking)
    {
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Don't support editing extras yet, will probably pop up some variant of the edit extras booking flow
    }
}
