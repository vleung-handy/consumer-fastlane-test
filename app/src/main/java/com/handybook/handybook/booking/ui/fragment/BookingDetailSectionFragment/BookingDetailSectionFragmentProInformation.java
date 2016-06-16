package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.constant.BookingAction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionProInfoView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailSectionFragmentProInformation extends
        BookingDetailSectionFragment<BookingDetailSectionProInfoView>
{
    private Configuration mConfiguration;

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
        return userCanLeaveTip(booking) ? R.string.leave_a_tip : R.string.blank_string;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return userCanLeaveTip(booking);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mConfiguration == null)
        {
            bus.post(new ConfigurationEvent.RequestConfiguration());
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        if (event != null)
        {
            mConfiguration = event.getConfiguration();
            if (event.getConfiguration() != null && event.getConfiguration().isMyProTeamEnabled())
            {
                if (booking != null && userManager.getCurrentUser() != null)
                {
                    updateDisplay(booking, userManager.getCurrentUser());
                }
            }
        }
    }


    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        final Provider pro = booking.getProvider();
        if (userCanLeaveTip(booking))
        {
            getSectionView().getEntryActionText().setVisibility(View.VISIBLE);
        }

        if (booking.hasAssignedProvider())
        {
            getSectionView().getEntryText().setText(pro.getFullName());
            getSectionView().getEntryText().setVisibility(View.VISIBLE);
            getSectionView().noProView.setVisibility(View.GONE);
        }
        else
        {
            //If the pro team stuff is enabled, show that, otherwise, fall back to showing
            //just the text
            if (mConfiguration != null && mConfiguration.isMyProTeamEnabled())
            {
                getSectionView().getEntryText().setVisibility(View.GONE);
                getSectionView().noProView.setVisibility(View.VISIBLE);
            }
            else
            {
                getSectionView().getEntryText().setVisibility(View.VISIBLE);
                getSectionView().getEntryText().setText(R.string.pro_assignment_pending);
            }
        }
    }

    @Override
    protected void setupClickListeners(final Booking booking)
    {
        view.getEntryActionText().setOnClickListener(actionClicked);
        getSectionView().buttonProTeam.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(getActivity(), ProTeamActivity.class));
            }
        });
    }

    @Override
    protected void onActionClick()
    {
        bus.post(new MixpanelEvent.TrackShowTipPrompt(
                MixpanelEvent.TipParentFlow.BOOKING_DETAILS_FLOW));

        TipDialogFragment tipDialogFragment = TipDialogFragment.newInstance(
                Integer.parseInt(booking.getId()),
                booking.getProvider().getFirstName());
        tipDialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    @Subscribe
    public void onReceiveTipProSuccess(BookingEvent.ReceiveTipProSuccess event)
    {
        view.getEntryActionText().setVisibility(View.GONE);
    }

    //Setup the contact booking action buttons

    @Override
    protected void clearBookingActionButtons()
    {
        getSectionView().actionButtonsLayoutSlot1.removeAllViews();
        getSectionView().actionButtonsLayoutSlot2.removeAllViews();
    }

    @Override
    protected ViewGroup getBookingActionButtonLayout()
    {
        return getSectionView().getActionButtonsLayout();
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
                return getSectionView().actionButtonsLayoutSlot1;
            case BookingAction.ACTION_CONTACT_TEXT:
                return getSectionView().actionButtonsLayoutSlot2;
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

    private boolean userCanLeaveTip(final Booking booking)
    {
        final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts =
                userManager.getCurrentUser().getDefaultTipAmounts();
        return booking.canLeaveTip() && defaultTipAmounts != null && !defaultTipAmounts.isEmpty();
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
