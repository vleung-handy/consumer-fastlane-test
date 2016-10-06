package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.constant.BookingAction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionProInfoView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.util.BookingUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailSectionFragmentProInformation extends
        BookingDetailSectionFragment<BookingDetailSectionProInfoView>
{
    private Configuration mConfiguration;

    public static BookingDetailSectionFragmentProInformation newInstance()
    {
        return new BookingDetailSectionFragmentProInformation();
    }

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_pro_info;
    }

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.booking_details_pro_info_title;
    }


    @Override
    protected void updateActionTextView(
            @NonNull final Booking booking, @NonNull final TextView actionTextView
    )
    {
        actionTextView.setVisibility(View.GONE);
        if (userCanLeaveTip(booking)) //note that tips can be made when booking.isPast() == true
        {
            actionTextView.setText(R.string.leave_a_tip);
            actionTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    onTipButtonClicked();
                }
            });
            actionTextView.setVisibility(View.VISIBLE);
        }
        else if (!booking.isPast())
        {
            if (mConfiguration != null
                    && mConfiguration.isMyProTeamEnabled()
                    && !booking.hasAssignedProvider())
            {
                actionTextView.setText(R.string.manage_pro_team);
                actionTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        onManageProTeamButtonClicked();
                    }
                });
                actionTextView.setVisibility(View.VISIBLE);
            }
            //handle more actions
        }
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

    /**
     * hides all the views managed by this class
     *
     * this does not hide the bottom action buttons (call/text)
     * and section action button (manage pro team, tip)
     * because those are managed/updates are triggered by the super class
     */
    private void hideAllClassManagedViews()
    {
        getSectionView().setLegacyNoProViewVisible(false);
        getSectionView().setAssignedProNameTextVisible(false);
        getSectionView().setAssignedProTeamMatchIndicatorVisible(false);
        getSectionView().getEntryText().setVisibility(View.GONE);
        getSectionView().getEntryTitle().setVisibility(View.GONE);
    }

    private void updateAndShowEntryText(@NonNull Booking.ProviderAssignmentInfo providerAssignmentInfo)
    {
        if (providerAssignmentInfo.getMainText() == null
                && providerAssignmentInfo.getSubText() == null) { return; }

        String htmlString = "";
        if (providerAssignmentInfo.getMainText() != null)
        {
            htmlString += "<b>" + providerAssignmentInfo.getMainText() + "</b> ";
        }
        htmlString += providerAssignmentInfo.getSubText();

        getSectionView().getEntryText().setText(Html.fromHtml(htmlString));
        getSectionView().getEntryText().setVisibility(View.VISIBLE);
    }

    //tODO cleanup
    private void showAssignedProviderInfo(
            String providerName,
            @Nullable Booking.ProviderAssignmentInfo providerAssignmentInfo
    )
    {
        getSectionView().getEntryTitle().setVisibility(View.VISIBLE);
        getSectionView().setAssignedProNameText(providerName);
        getSectionView().setAssignedProNameTextVisible(true);

        if (providerAssignmentInfo != null)
        {
            if (mConfiguration != null
                    && mConfiguration.isMyProTeamEnabled()
                    && providerAssignmentInfo.isProTeamMatch()
                    && !userCanLeaveTip(booking)//TODO must revert! this is temporary until can resolve tip and match indicator layout with design
                    )
            {
                getSectionView().setAssignedProTeamMatchIndicatorVisible(true);
            }
            updateAndShowEntryText(providerAssignmentInfo);
        }
    }

    //TODO cleanup
    private void showPendingProviderInfo(@Nullable Booking.ProviderAssignmentInfo providerAssignmentInfo)
    {
        //no assigned pro, so don't show the assigned pro layout
        if (providerAssignmentInfo != null)
        {
            getSectionView().getEntryTitle().setVisibility(View.VISIBLE);
            updateAndShowEntryText(providerAssignmentInfo);
        }
        else if (mConfiguration != null && mConfiguration.isMyProTeamEnabled())
        {
            //fallback view for when there's no provider assignment info object
            getSectionView().setLegacyNoProViewVisible(true);
        }
        else
        {
            //fallback view for when no provider assignment object and pro teams not enabled
            getSectionView().getEntryText().setVisibility(View.VISIBLE);
            getSectionView().getEntryTitle().setVisibility(View.VISIBLE);
            getSectionView().getEntryText().setText(R.string.pro_assignment_pending);
        }
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        hideAllClassManagedViews();

        final Provider pro = booking.getProvider();

        if (booking.hasAssignedProvider())
        {
            showAssignedProviderInfo(
                    pro.getFirstNameAndLastInitial(),
                    booking.getProviderAssignmentInfo()
            );
        }
        else
        {
            showPendingProviderInfo(booking.getProviderAssignmentInfo());
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getSectionView().setLegacyNoProViewProTeamButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onManageProTeamButtonClicked();
            }
        });
    }

    private void onTipButtonClicked()
    {
        TipDialogFragment tipDialogFragment = TipDialogFragment.newInstance(
                Integer.parseInt(booking.getId()),
                booking.getProvider().getFirstName()
        );
        tipDialogFragment.show(getActivity().getSupportFragmentManager(), TipDialogFragment.TAG);
    }

    /*
    TODO this is risky - could receive this event from another instance B of this fragment A
    when A's entry action is NOT "leave a tip"
     */
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

    //TODO this is confusing
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

    //TODO this is confusing
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

    private void onManageProTeamButtonClicked()
    {
        //start pro team activity
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.ProTeamOpenTapped()));
        startActivity(new Intent(getActivity(), ProTeamActivity.class));
    }

    private View.OnClickListener contactPhoneClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            if (validateProPhoneInformation(booking))
            {
                BookingUtil.callPhoneNumber(
                        booking.getProvider().getPhone(),
                        BookingDetailSectionFragmentProInformation.this.getActivity()
                );
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
                BookingUtil.textPhoneNumber(
                        booking.getProvider().getPhone(),
                        BookingDetailSectionFragmentProInformation.this.getActivity()
                );
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



}
