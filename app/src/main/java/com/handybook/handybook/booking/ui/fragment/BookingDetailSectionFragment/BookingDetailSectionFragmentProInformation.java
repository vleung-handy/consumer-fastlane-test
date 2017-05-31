package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.net.Uri;
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
import com.handybook.handybook.booking.ui.fragment.RescheduleDialogFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionProInfoView;
import com.handybook.handybook.booking.util.BookingUtil;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.ProContactedLog;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;
import com.handybook.handybook.proteam.callback.ConversationCallback;
import com.handybook.handybook.proteam.callback.ConversationCallbackWrapper;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BookingDetailSectionFragmentProInformation extends
        BookingDetailSectionFragment<BookingDetailSectionProInfoView>
        implements ConversationCallback {

    @Inject
    ProTeamManager mProTeamManager;

    private FragmentSafeCallback<ProTeam.ProTeamCategory> mCallback;

    {
        mCallback = new FragmentSafeCallback<ProTeam.ProTeamCategory>(this) {
            @Override
            public void onCallbackSuccess(final ProTeam.ProTeamCategory response) {
                onReceiveBookingProTeamSuccess(response);
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {}
        };
    }

    public static BookingDetailSectionFragmentProInformation newInstance() {
        return new BookingDetailSectionFragmentProInformation();
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_booking_detail_section_pro_info;
    }

    @Override
    protected int getEntryTitleTextResourceId(Booking booking) {
        return R.string.booking_details_pro_info_title;
    }

    @Override
    protected void updateActionTextView(
            @NonNull final Booking booking,
            @NonNull final TextView actionTextView
    ) {
        //this logic is ugly, can we make it more server-driven?

        actionTextView.setVisibility(View.GONE);
        if (userCanLeaveTip(booking)) //note that tips can be made when booking.isPast() == true
        {
            //action text should be "leave a tip"
            actionTextView.setText(R.string.leave_a_tip);
            actionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onTipButtonClicked();
                }
            });
            actionTextView.setVisibility(View.VISIBLE);
        }
        else if (!booking.isPast()) {
            if (!booking.hasAssignedProvider()
                //don't want to show team management button when booking already has provider
                && booking.getProviderAssignmentInfo() != null
                    /*
                    when provider assignment info is missing, we fall back to a
                    legacy view that has this same action button in a different location,
                    so don't want to show two of them
                     */
                    ) {
                //action text should be "manage pro team"
                actionTextView.setText(R.string.manage_pro_team);
                actionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onManageProTeamButtonClicked();
                    }
                });
                actionTextView.setVisibility(View.VISIBLE);
            }
            //handle more actions
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mConfigurationManager.getPersistentConfiguration().isProTeamRescheduleCTAEnabled()) {
            mProTeamManager.requestBookingProTeam(booking.getId(), mCallback);
        }
    }

    private void onReceiveBookingProTeamSuccess(final ProTeam.ProTeamCategory category) {
        if (!mConfigurationManager.getPersistentConfiguration().isProTeamRescheduleCTAEnabled()
            || category == null
            || category.getPreferred() == null
            || category.getPreferred().isEmpty()
            || booking.getProvider() == null
            || booking.getProvider().getId() == null
            || booking.isPast()
            || Utils.containsProvider(category.getPreferred(), booking.getProvider().getId())) {
            getSectionView().showPreferDifferentProLayout(false);
        }
        else {
            getSectionView().showPreferDifferentProLayout(true);
            getSectionView().setPreferDifferentProOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    RescheduleDialogFragment fragment =
                            RescheduleDialogFragment.newInstance(category, booking);
                    fragment.show(getFragmentManager(), null);
                }
            });
        }
    }

    /**
     * hides all the views managed by this class
     *
     * this does not hide the bottom action buttons (call/text)
     * and section action button (manage pro team, tip)
     * because those are managed/updates are triggered by the super class
     */
    private void hideAllClassManagedViews() {
        getSectionView().setLegacyNoProViewVisible(false);
        getSectionView().setProProfileVisible(false);
        getSectionView().setAssignedProTeamMatchIndicatorVisible(false);
        getSectionView().getEntryText().setVisibility(View.GONE);
        getSectionView().getEntryTitle().setVisibility(View.GONE);
        getSectionView().showPreferDifferentProLayout(false);
    }

    /**
     * updates the entry text with provider assignment text from the server
     *
     * @param providerAssignmentInfo
     */
    private void updateAndShowEntryText(@NonNull Booking.ProviderAssignmentInfo providerAssignmentInfo) {
        if (providerAssignmentInfo.getMainText() == null
            && providerAssignmentInfo.getSubText() == null) { return; }

        String htmlString = "";
        if (providerAssignmentInfo.getMainText() != null) {
            htmlString += "<b>" + providerAssignmentInfo.getMainText() + "</b> ";
        }
        htmlString += providerAssignmentInfo.getSubText();

        getSectionView().getEntryText().setText(Html.fromHtml(htmlString));
        getSectionView().getEntryText().setVisibility(View.VISIBLE);
    }

    /**
     * shown when there a provider is assigned to the booking
     * @param provider
     * @param providerAssignmentInfo
     */
    private void showAssignedProviderInfo(
            Provider provider,
            @Nullable Booking.ProviderAssignmentInfo providerAssignmentInfo
    ) {
        getSectionView().getEntryTitle().setVisibility(View.VISIBLE);
        getSectionView().setProProfileVisible(true);

        if (providerAssignmentInfo != null) {
            getSectionView().setAssignedProInfo(provider, providerAssignmentInfo);
            if (providerAssignmentInfo.isProTeamMatch()) {
                //indicate that this pro is on the user's pro team
                getSectionView().setAssignedProTeamMatchIndicatorVisible(true);
            }

            //update entry text with provider assignment text from the server
            updateAndShowEntryText(providerAssignmentInfo);
        }
    }

    /**
     * shown when there is no assigned provider yet
     * @param providerAssignmentInfo
     */
    private void showPendingProviderInfo(@Nullable Booking.ProviderAssignmentInfo providerAssignmentInfo) {
        if (providerAssignmentInfo != null) {
            //update entry text with provider assignment text from the server
            getSectionView().getEntryTitle().setVisibility(View.VISIBLE);
            updateAndShowEntryText(providerAssignmentInfo);
        }
        else {
            //fallback view for when there's no provider assignment info object and pro teams are enabled
            getSectionView().setLegacyNoProViewVisible(true);
        }
    }

    @Override
    public void updateDisplay(@NonNull final Booking booking, @NonNull User user) {
        super.updateDisplay(booking, user);

        hideAllClassManagedViews();

        final Provider pro = booking.getProvider();

        if (booking.hasAssignedProvider()) {
            //show view for assigned provider
            showAssignedProviderInfo(pro, booking.getProviderAssignmentInfo());
        }
        else {
            //show view for pending provider
            showPendingProviderInfo(booking.getProviderAssignmentInfo());
        }

        //if pro profile enabled, launch pro profile page on profile image click
        if (pro.getIsProProfileEnabled())
        {
            getSectionView().setProProfileClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //launch pro profiles activity
                    startActivity(ProProfileActivity.buildIntent(
                            getContext(),
                            pro.getId(),
                            SourcePage.BOOKING_DETAILS
                    ));
                }
            });
        }
        else
        {
            getSectionView().setProProfileClickListener(null);
        }

    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDisplay(booking, userManager.getCurrentUser());
        getSectionView().setLegacyNoProViewProTeamButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onManageProTeamButtonClicked();
            }
        });
    }

    private void onTipButtonClicked() {
        TipDialogFragment tipDialogFragment = TipDialogFragment.newInstance(
                Integer.parseInt(booking.getId()),
                booking.getProvider().getFirstName()
        );
        tipDialogFragment.show(getActivity().getSupportFragmentManager(), TipDialogFragment.TAG);
    }

    private void onManageProTeamButtonClicked() {
        //start pro team activity
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.ProTeamOpenTapped()));
        startActivity(new Intent(getActivity(), ProTeamEditActivity.class));
    }

    /*
    TODO this is risky - could receive this event from another instance B of this fragment A
    when A's entry action is NOT "leave a tip"
     */
    @Subscribe
    public void onReceiveTipProSuccess(BookingEvent.ReceiveTipProSuccess event) {
        view.getEntryActionText().setVisibility(View.GONE);
    }

    //Setup the contact booking action buttons

    @Override
    protected void clearBookingActionButtons() {
        getSectionView().actionButtonsLayoutSlot1.removeAllViews();
        getSectionView().actionButtonsLayoutSlot2.removeAllViews();
    }

    @Override
    protected ViewGroup getBookingActionButtonLayout() {
        return getSectionView().getActionButtonsLayout();
    }

    @Override
    protected List<String> getActionButtonTypeList(Booking booking) {
        List<String> actionButtonTypes = new ArrayList<>();

        if (booking.getChatOptions() != null && booking.getChatOptions().shouldAllowChat()) {
            actionButtonTypes.add(BookingAction.ACTION_CONTACT_PHONE);
            actionButtonTypes.add(BookingAction.ACTION_CONTACT_TEXT);
        }

        return actionButtonTypes;
    }

    //TODO this is confusing
    @Override
    protected ViewGroup getParentForActionButtonType(String actionButtonType) {
        switch (actionButtonType) {
            case BookingAction.ACTION_CONTACT_PHONE:
                return getSectionView().actionButtonsLayoutSlot1;
            case BookingAction.ACTION_CONTACT_TEXT:
                return getSectionView().actionButtonsLayoutSlot2;
        }
        return null;
    }

    //TODO this is confusing
    @Override
    protected View.OnClickListener getOnClickListenerForAction(String actionButtonType) {
        switch (actionButtonType) {
            case BookingAction.ACTION_CONTACT_PHONE:
                return contactPhoneClicked;
            case BookingAction.ACTION_CONTACT_TEXT:
                return contactTextClicked;
        }
        return null;
    }

    private boolean userCanLeaveTip(final Booking booking) {
        final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts =
                userManager.getCurrentUser().getDefaultTipAmounts();
        return booking.canLeaveTip() && defaultTipAmounts != null && !defaultTipAmounts.isEmpty();
    }

    private View.OnClickListener contactPhoneClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (validateProPhoneInformation(booking)) {
                bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                        EventContext.BOOKING_DETAILS, booking.getId(), ProContactedLog.PHONE)));

                BookingUtil.callPhoneNumber(
                        booking.getProvider().getPhone(),
                        BookingDetailSectionFragmentProInformation.this.getActivity()
                );
            }
            else {
                showToast(R.string.invalid_pro_phone_number);
            }
        }
    };

    private View.OnClickListener contactTextClicked = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            if (booking.getChatOptions() != null &&
                booking.getChatOptions().shouldDirectToInAppChat()) {
                showBlockingProgressSpinner();
                bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                        EventContext.BOOKING_DETAILS, booking.getId(), ProContactedLog.CHAT)));
                HandyLibrary.getInstance()
                            .getHandyService()
                            .createConversation(
                                    booking.getProvider().getId(),
                                    userManager.getCurrentUser().getAuthToken(),
                                    "",
                                    new ConversationCallbackWrapper(
                                            BookingDetailSectionFragmentProInformation.this)
                            );
            }
            else if (validateProPhoneInformation(booking)) {
                bus.post(new LogEvent.AddLogEvent(new ProContactedLog(
                        EventContext.BOOKING_DETAILS, booking.getId(), ProContactedLog.SMS)));
                BookingUtil.textPhoneNumber(
                        booking.getProvider().getPhone(),
                        BookingDetailSectionFragmentProInformation.this.getActivity()
                );
            }
            else {
                showToast(R.string.invalid_pro_phone_number);
            }
        }
    };

    private boolean validateProPhoneInformation(Booking booking) {
        boolean validPhoneNumber = false;

        if (booking.getProvider() != null &&
            booking.getProvider().getPhone() != null &&
            !booking.getProvider().getPhone().isEmpty()) {
            validPhoneNumber = true;
        }

        return validPhoneNumber;
    }

    @Override
    public void onCreateConversationSuccess(@Nullable final String conversationId) {
        hideProgressSpinner();
        Intent intent = new Intent(getContext(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, Uri.parse(conversationId));
        intent.putExtra(
                BundleKeys.PRO_MESSAGES_VIEW_MODEL,
                new ProMessagesViewModel(booking.getProvider())
        );
        startActivity(intent);
    }

    @Override
    public void onCreateConversationError() {
        hideProgressSpinner();
        showToast(R.string.an_error_has_occurred);
    }
}
