package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BookingAction;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BookingEditHoursActivity;

import java.util.ArrayList;
import java.util.List;


public class BookingDetailSectionFragmentBookingActions extends BookingDetailSectionFragment
{
    public static final String TAG= "BookingDetailSectionFragmentBookingAction";

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_booking_actions;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
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
            if(booking.canEditHours())
            {
                actionButtonTypes.add(BookingAction.ACTION_EDIT_HOURS);
            }
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
            case BookingAction.ACTION_EDIT_HOURS:
                return editHoursClicked;
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

    private View.OnClickListener editHoursClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            final Intent intent = new Intent(getActivity(), BookingEditHoursActivity.class);
            intent.putExtra(BundleKeys.BOOKING, booking);
            getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);

        }
    };



}
