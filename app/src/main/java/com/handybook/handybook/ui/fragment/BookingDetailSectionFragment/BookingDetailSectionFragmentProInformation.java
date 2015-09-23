package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.BookingAction;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.BookingDetailSectionProInfoView;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


//TODO: Request a pro feature requires breaking down the BookingOptionsFragment into a usable fragment, request a pro disabled until that time

public class BookingDetailSectionFragmentProInformation extends BookingDetailSectionFragment
{
    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionProInfoView view;

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_pro_info;
    }

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
            //TODO: Request a pro functionality
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
            //TODO: Request a pro functionality
            return false;
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
        if (!booking.hasAssignedProvider())
        {
            //TODO: Request a pro functionality
            //need a new UI where we request the requestable pros, then display them, then get the result back
        }
    }


    //Setup the contact booking action buttons

    @Override
    protected void clearBookingActionButtons()
    {
        view.actionButtonsLayoutSlot1.removeAllViews();
        view.actionButtonsLayoutSlot2.removeAllViews();
    }

    @Override
    protected ViewGroup getBookingActionButtonLayout()
    {
        return view.actionButtonsLayout;
    }

    @Override
    protected List<String> getActionButtonTypeList(Booking booking)
    {
        List<String> actionButtonTypes = new ArrayList<>();
        actionButtonTypes.add(BookingAction.ACTION_CONTACT_PHONE);
        actionButtonTypes.add(BookingAction.ACTION_CONTACT_TEXT);
        return actionButtonTypes;
    }

    @Override
    protected ViewGroup getParentForActionButtonType(String actionButtonType)
    {
        switch (actionButtonType)
        {
            case BookingAction.ACTION_CONTACT_PHONE:
                return view.actionButtonsLayoutSlot1;
            case BookingAction.ACTION_CONTACT_TEXT:
                return view.actionButtonsLayoutSlot2;
        }
        return null;
    }

    @Override
    protected View.OnClickListener getOnClickListenerForAction(String actionButtonType)
    {
        switch (actionButtonType)
        {
            case BookingAction.ACTION_CONTACT_PHONE:
                return contactPhoneClicked;
            case BookingAction.ACTION_CONTACT_TEXT:
                return contactTextClicked;
        }
        return null;
    }

    private View.OnClickListener contactPhoneClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            callPhoneNumber(booking.getProvider().getPhone());
        }
    };

    private View.OnClickListener contactTextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            textPhoneNumber(booking.getProvider().getPhone());
        }
    };


    //use native functionality to trigger a phone call
    private void callPhoneNumber(final String phoneNumber)
    {
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), this.getActivity());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    //use native functionality to trigger a text message interface
    private void textPhoneNumber(final String phoneNumber)
    {
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), this.getActivity());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", activityException));
        }
    }


}
