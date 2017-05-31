package com.handybook.handybook.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.reschedule.RescheduleUpcomingActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.ActivitySafeCallback;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.manager.SessionManager;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.core.ui.view.ProAvatarView;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamWrapper;
import com.handybook.handybook.proteam.ui.view.ConversationTipsView;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.shared.layer.ui.AttachmentItemView;
import com.handybook.shared.layer.ui.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static com.handybook.handybook.booking.model.Service.PREFIX_CLEAN_CONSTANT;
import static com.handybook.handybook.booking.ui.fragment.BookingFlowFragment.INCREMENT;

/**
 * This is a derivation of the MessagesListActivity that allows for a reschedule flow
 */
public class ProMessagesActivity extends MessagesListActivity {

    @Inject
    UserManager mUserManager;
    @Inject
    ProTeamManager mProTeamManager;
    @Inject
    DataManager mDataManager;
    @Inject
    ServicesManager mServiceManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    Bus mBus;

    private ProMessagesViewModel mProMessageViewModel;
    private User mUser;
    private Booking mBooking;

    private AttachmentItemView mRescheduleButton;
    private AttachmentItemView mNewBookingButton;
    private ProgressDialog mProgressDialog;
    private Service mCleaningService;
    private int mAttachmentViewItemHeight;

    private ProAvailabilityResponse mProAvailabilityResponse;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseApplication) getApplication()).inject(this);
        initMessageBar();

        mBooking = getIntent().getParcelableExtra(BundleKeys.BOOKING);

        mProMessageViewModel = (ProMessagesViewModel)
                getIntent().getSerializableExtra(BundleKeys.PRO_MESSAGES_VIEW_MODEL);

        if (getIntent().getBooleanExtra(BundleKeys.SHOW_TIPS, false)) {
            mTipsContainer.addView(new ConversationTipsView(this));
        }

        updateProAvatar();
        setAvatarAndNameClickedListeners();

        mAttachmentViewItemHeight
                = getResources().getDimensionPixelSize(R.dimen.attachment_item_height);

        setupCustomAttachmentMenus();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.rescheduling));

        mUser = mUserManager.getCurrentUser();
        if (mUser == null) {
            //we're in an invalid state, redirect to login.
            Toast.makeText(this, R.string.prompt_login, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ServiceCategoriesActivity.class));
            finish();
        }

        //default to not show reschedule by default.
        mRescheduleButton.setVisibility(View.GONE);

        refreshAttachmentMenu();
        initCleaningService();
    }

    /**
     * This message bar is the bottom bar where a user can type their messages, hit send, and tap the
     * action option on left side
     */
    private void initMessageBar() {
        //Change default text to "Send"
        Button button = (Button) findViewById(R.id.send_button);
        button.setText(R.string.send);
        //Update hint
        EditText messageEditText = (EditText) findViewById(R.id.message_edit_text);
        messageEditText.setHint(R.string.type_something);
    }

    /**
     * when the avatar or name display is clicked, launch the pro profile page
     * if pro profiles enabled for this pro
     */
    private void setAvatarAndNameClickedListeners() {
        if (mProMessageViewModel == null) { return; }

        if (mProMessageViewModel.isProProfileEnabled()) {
            View.OnClickListener onAvatarOrNameClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //launch pro profiles activity
                    startActivity(ProProfileActivity.buildIntent(
                            ProMessagesActivity.this,
                            mProMessageViewModel.getProviderId(),
                            SourcePage.MESSAGES
                    ));
                }
            };
            mAvatarContainer.setOnClickListener(onAvatarOrNameClickedListener);
            mTitle.setOnClickListener(onAvatarOrNameClickedListener);
        }
        else {
            mAvatarContainer.setOnClickListener(null);
            mTitle.setOnClickListener(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSessionManager.markActivity();
        mBus.register(this);

    }

    @Override
    protected void onPause() {
        mBus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
        }
    }

    private void updateProAvatar() {
        if (mProMessageViewModel != null) {
            int size = getResources().getDimensionPixelSize(R.dimen.chat_toolbar_icon_size);
            ProAvatarView avatar = new ProAvatarView(this, size);
            avatar.bindPro(mProMessageViewModel.isFavorite(), mProMessageViewModel.getImageUrl());
            avatar.setHeartContainerBackground(R.drawable.bg_circle_blue);
            mAvatarContainer.addView(avatar);
            mAvatarContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Figures out, from cache, what service represents the "home_cleaning" service
     */
    private void initCleaningService() {
        //we can safely assume that by this point, there is a valid cached services
        Service cachedHomeCleaningService = mServiceManager.getCachedService(Booking.SERVICE_HOME_CLEANING);
        if(cachedHomeCleaningService != null)
        {
            mCleaningService = cachedHomeCleaningService;
        }
    }

    @Override
    protected void setConversation(final Conversation conversation) {
        super.setConversation(conversation);
        refreshAttachmentMenu();
    }

    /**
     * Syncs up the attachment menu buttons. This should only be called after conversation has been
     * set
     */
    private void refreshAttachmentMenu() {
        boolean hasProModel = mProMessageViewModel != null;
        if (mRescheduleButton != null) {
            mRescheduleButton.setVisibility(hasProModel ? View.VISIBLE : View.GONE);
        }
        if (mNewBookingButton != null) {
            mNewBookingButton.setVisibility(hasProModel ? View.VISIBLE : View.GONE);
        }

        //likely coming from a deep link or something, we need to fetch it
        if (!hasProModel && mProTeamManager != null) {
            mProTeamManager.requestProTeam(new ProTeamCallback(this));
        }
    }

    public void onProTeamReceived(ProTeam proTeam) {
        if (proTeam != null && proTeam.getAllCategories() != null) {
            if (proTeam.getAllCategories().getPreferred() != null) {
                for (final Provider preferred : proTeam.getAllCategories().getPreferred()) {
                    if (containsId(mConversation.getParticipants(), preferred.getLayerUserId())) {

                        mProMessageViewModel = new ProMessagesViewModel(preferred);
                        updateProAvatar();
                        refreshAttachmentMenu();
                        return;
                    }
                }
            }

            if (proTeam.getAllCategories().getIndifferent() != null) {
                for (final Provider indifferent : proTeam.getAllCategories().getIndifferent()) {
                    if (containsId(mConversation.getParticipants(), indifferent.getLayerUserId())) {
                        mProMessageViewModel = new ProMessagesViewModel(indifferent);
                        updateProAvatar();
                        refreshAttachmentMenu();
                        return;
                    }
                }
            }
        }

        //by the time we get here, if we didn't find any matching pro, that means we're not supposed
        //to be in this conversation to begin with. Exit. Most of the time this should not happen.
        if (mProMessageViewModel == null) {
            Toast.makeText(this, R.string.conversation_cannot_load, Toast.LENGTH_SHORT);
            startActivity(new Intent(this, ProTeamActivity.class));
            finish();
        }

    }

    private boolean containsId(Set<Identity> participants, String id) {
        for (final Identity participant : participants) {
            if (participant.getUserId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private void setupCustomAttachmentMenus() {
        getAttachmentButton().setImageResource(R.drawable.ic_calendar_plus);

        mRescheduleButton = getRescheduleView();
        mNewBookingButton = getNewBookingView();

        addAttachmentMenuItem(mRescheduleButton);
        addAttachmentMenuItem(mNewBookingButton);
    }

    private AttachmentItemView getNewBookingView() {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_make_booking);
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.make_a_booking));
        attachmentItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getAttachmentMenu().dismiss();
                if (mCleaningService != null
                    && mProMessageViewModel != null
                    && mProMessageViewModel.getCategoryType()
                                           .toString()
                                           .toLowerCase()
                                           .contains(PREFIX_CLEAN_CONSTANT)
                        ) {

                    //for cleaning, we want to start the booking flow right away.
                    mBus.post(new LogEvent.AddLogEvent(
                            new ChatLog.MakeBookingSelectedLog(
                                    mProMessageViewModel.getProviderId(),
                                    mConversation.getId().toString(),
                                    String.valueOf(mCleaningService.getId())
                            )));
                    startBookingFlow();
                }
                else {
                    mBus.post(new LogEvent.AddLogEvent(new ChatLog.MakeBookingSelectedLog(
                            mProMessageViewModel.getProviderId(),
                            mConversation.getId().toString(),
                            null
                    )));
                    startActivity(new Intent(
                            ProMessagesActivity.this,
                            ServiceCategoriesActivity.class
                    ));
                }
            }
        });

        return attachmentItemView;
    }

    /**
     * The logic here is borrowed from BookingFlowFragment.startBookingFlow(); -- basically, it does
     * all the things needed to prepare for the booking flow
     */
    private void startBookingFlow() {
        final BookingRequest request = new BookingRequest();
        request.setServiceId(mCleaningService.getId());
        request.setUniq(mCleaningService.getUniq());
        request.setCoupon(mBookingManager.getPromoTabCoupon());
        request.setProviderId(mProMessageViewModel.getProviderId());
        if (mUser != null) {
            request.setEmail(mUser.getEmail());
        }
        mBookingManager.clear();
        mBookingManager.setCurrentRequest(request);
        final Intent intent = new Intent(this, BookingLocationActivity.class);
        intent.putExtra(BundleKeys.SHOW_PROGRESS, true);
        intent.putExtra(BundleKeys.BOOKING_FLOW_STARTED, true);
        intent.putExtra(BundleKeys.PROGRESS, INCREMENT);
        startActivity(intent);
    }

    private AttachmentItemView getRescheduleView() {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_reschedule);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.reschedule));
        attachmentItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getAttachmentMenu().dismiss();
                mBus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleSelectedLog(
                        String.valueOf(
                                mProMessageViewModel.getProviderId()),
                        mConversation.getId()
                                     .toString()
                )));

                //before we send the mUser to the reschedule bookings page, we check to make sure
                //there are upcoming bookings to reschedule

                mProgressDialog.show();
                if (mBooking == null) {
                    mDataManager.getBookingsForReschedule(
                            mProMessageViewModel.getProviderId(),
                            new BookingsCallback(ProMessagesActivity.this)
                    );
                }
                else {
                    mBookingManager.rescheduleBookingWithProAvailability(
                            mProMessageViewModel.getProviderId(),
                            mBooking,
                            null
                    );

                }
            }
        });

        return attachmentItemView;
    }

    public void onBookingReceived(final List<Booking> bookings) {
        mProgressDialog.dismiss();

        if (bookings == null || bookings.isEmpty()) {
            mBus.post(new LogEvent.AddLogEvent(new ChatLog.NoUpcomingBookingsLog()));
            Toast.makeText(
                    this,
                    getString(
                            R.string.reschedule_no_bookings_formatted,
                            mProMessageViewModel.getProviderName()
                    ),
                    Toast.LENGTH_LONG
            ).show();
        }
        else {
            Intent intent = new Intent(this, RescheduleUpcomingActivity.class);

            intent.putExtra(BundleKeys.PROVIDER_ID, mProMessageViewModel.getProviderId());
            intent.putExtra(BundleKeys.BOOKINGS, (Serializable) bookings);
            startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
        }
    }

    public void onBookingsRequestError() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    public void onReceivedAvailabilitySuccess(ProAvailabilityResponse proAvailabilityResponse) {
        mProAvailabilityResponse = proAvailabilityResponse;
        mDataManager.getPreRescheduleInfo(
                mBooking.getId(), new PreRescheduleCallback(ProMessagesActivity.this));
    }

    public void onReceivedAvailabilityError() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    public void onReceivePreRescheduleInfoSuccess(String notice) {
        mProgressDialog.dismiss();

        mBus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleBookingSelectedLog(
                mProMessageViewModel.getProviderId(),
                mBooking.getId(),
                String.valueOf(mBooking.getRecurringId())
        )));

        final Intent intent = new Intent(this, BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.FROM_CHAT);
        intent.putExtra(BundleKeys.PROVIDER_ID, mProMessageViewModel.getProviderId());
        intent.putExtra(BundleKeys.PROVIDER_NAME, mProMessageViewModel.getProviderFirstName());
        intent.putExtra(BundleKeys.PRO_AVAILABILITY, mProAvailabilityResponse);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    public void onRescheduleRequestError() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.reschedule_try_again, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onRescheduleWithAvailabilitySuccess(BookingEvent.RescheduleBookingWithProAvailabilitySuccess success) {
        mProAvailabilityResponse = success.getProAvailability();
        onReceivePreRescheduleInfoSuccess(success.getNotice());
    }

    @Subscribe
    public void onRescheduleWithAvailabilityError(BookingEvent.RescheduleBookingWithProAvailabilityError error) {
        onRescheduleRequestError();
    }

    public static class ProTeamCallback implements DataManager.Callback<ProTeamWrapper> {

        private WeakReference<ProMessagesActivity> mActivityRef;

        public ProTeamCallback(ProMessagesActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final ProTeamWrapper response) {
            if (mActivityRef.get() != null) {
                mActivityRef.get().onProTeamReceived(response.getProTeam());
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            //we don't need to do anything here, by default the reschedule stuff doesn't show
        }
    }


    private static class BookingsCallback
            extends ActivitySafeCallback<UserBookingsWrapper, ProMessagesActivity> {

        public BookingsCallback(ProMessagesActivity activity) {
            super(activity);
        }

        @Override
        public void onCallbackSuccess(final UserBookingsWrapper response) {
            mActivityWeakReference.get().onBookingReceived(response.getBookings());
        }

        @Override
        public void onCallbackError(final DataManager.DataManagerError error) {
            mActivityWeakReference.get().onBookingsRequestError();
        }
    }


    private static class PreRescheduleCallback
            extends ActivitySafeCallback<String, ProMessagesActivity> {

        public PreRescheduleCallback(ProMessagesActivity activity) {
            super(activity);
        }

        @Override
        public void onCallbackSuccess(final String response) {
            mActivityWeakReference.get().onReceivePreRescheduleInfoSuccess(response);
        }

        @Override
        public void onCallbackError(final DataManager.DataManagerError error) {
            mActivityWeakReference.get().onRescheduleRequestError();
        }
    }
}

