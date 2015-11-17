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
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.fragment.TipDialogFragment;
import com.handybook.handybook.ui.widget.BookingDetailSectionProInfoView;
import com.handybook.handybook.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class BookingDetailSectionFragmentProInformation extends BookingDetailSectionFragment
{
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";

    private static final long HOURS_TO_ALLOW_CONTACT_PAST_BOOKING = 72L;

    public static final String TAG = "BookingDetailSectionFragmentProInformation";

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
        return booking.canLeaveTip() ? R.string.leave_a_tip : R.string.blank_string;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return booking.canLeaveTip();
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        final Booking.Provider pro = booking.getProvider();

        if (booking.canLeaveTip())
        {
            view.entryActionText.setVisibility(View.VISIBLE);
        }

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
    protected void setupClickListeners(final Booking booking)
    {
        view.entryActionText.setOnClickListener(actionClicked);
    }

    @Override
    protected void onActionClick()
    {
        TipDialogFragment.newInstance(Integer.parseInt(booking.getId()), booking.getProvider().getFirstName())
                .show(getActivity().getSupportFragmentManager(), null);
    }

    @Subscribe
    public void onReceiveTipProSuccess(HandyEvent.ReceiveTipProSuccess event)
    {
        view.entryActionText.setVisibility(View.GONE);
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
        if (booking.hasAssignedProvider())
        {
            //Make sure it is not an empty phone number
            if (validateProPhoneInformation(booking))
            {
                actionButtonTypes.add(BookingAction.ACTION_CONTACT_PHONE);
                actionButtonTypes.add(BookingAction.ACTION_CONTACT_TEXT);
            }
        }
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
            if (validateProPhoneInformation(booking))
            {
                callPhoneNumber(booking.getProvider().getPhone());
            }
            else
            {
                showToast(R.string.invalid_pro_phone_number);
            }
        }
    };

    private View.OnClickListener contactTextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            if (validateProPhoneInformation(booking))
            {
                textPhoneNumber(booking.getProvider().getPhone());
            }
            else
            {
                showToast(R.string.invalid_pro_phone_number);
            }
        }
    };

    private boolean validateProPhoneInformation(Booking booking)
    {
        boolean validPhoneNumber = false;

        if (booking.getProvider() != null &&
                booking.getProvider().getPhone() != null &&
                !booking.getProvider().getPhone().isEmpty())
        {
            validPhoneNumber = true;
        }

        return validPhoneNumber;
    }

    //use native functionality to trigger a phone call
    private void callPhoneNumber(final String phoneNumber)
    {
        if (phoneNumber == null || phoneNumber.isEmpty())
        {
            return;
        }

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
        if (phoneNumber == null || phoneNumber.isEmpty())
        {
            return;
        }

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
