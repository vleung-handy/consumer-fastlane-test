package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.BookingDetailSectionExtrasView;

import butterknife.InjectView;

public class BookingDetailSectionFragmentExtras extends BookingDetailSectionFragment
{

    @InjectView(R.id.booking_detail_section_extras_view)
    protected BookingDetailSectionExtrasView view;

    @Override
    protected int getFragmentResourceId(){ return R.layout.fragment_booking_detail_section_extras; }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        view.updateExtrasDisplay(booking);
    }

    @Override
    protected void setupClickListeners(Booking booking)
    {
        //TODO: Probably some additional constraints on this for certain edit actions
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }


    @Override
    protected void onActionClick()
    {
        //TODO:
    }
}
