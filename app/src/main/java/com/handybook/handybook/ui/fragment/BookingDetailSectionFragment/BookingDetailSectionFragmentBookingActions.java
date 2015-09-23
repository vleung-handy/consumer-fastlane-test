package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BookingAction;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.HandyEvent;

import java.util.ArrayList;
import java.util.List;


public class BookingDetailSectionFragmentBookingActions extends BookingDetailSectionFragment
{
    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_booking_actions;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        view.entryText.setVisibility(View.GONE);
        view.entryActionText.setVisibility(View.GONE);
        view.entryTitle.setVisibility(View.GONE);
    }

    //Setup the contact booking action buttons
    @Override
    protected List<String> getActionButtonTypeList(Booking booking)
    {
        List<String> actionButtonTypes = new ArrayList<>();
        if(!booking.isPast())
        {
            actionButtonTypes.add(BookingAction.ACTION_RESCHEDULE);
            actionButtonTypes.add(BookingAction.ACTION_CANCEL);
        }
        return actionButtonTypes;
    }

    @Override
    protected View.OnClickListener getOnClickListenerForAction(String actionButtonType)
    {
        switch (actionButtonType)
        {
            case BookingAction.ACTION_CANCEL:
                return cancelClicked;
            case BookingAction.ACTION_RESCHEDULE:
                return rescheduleClicked;
        }
        return null;
    }

    private View.OnClickListener cancelClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            //TODO: Need to work out a system for enabling/disabling at proper times, listening to events from parent fragment is flakey b/c of on resume timing
            //disableInputs();
            //TODO: investigate, do not activate the progress dialog here, causes issues when returning from activity
            bus.post(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
        }
    };

    private View.OnClickListener rescheduleClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            //TODO: Need to work out a system for enabling/disabling at proper times, listening to events from parent fragment is flakey b/c of on resume timing
            //disableInputs();
            //TODO: investigate, do not activate the progress dialog here, causes issues when returning from activity
            bus.post(new HandyEvent.RequestPreRescheduleInfo(booking.getId()));
        }
    };




}
