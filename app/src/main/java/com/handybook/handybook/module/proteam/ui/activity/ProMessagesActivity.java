package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.module.proteam.manager.ProTeamManager;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProTeamWrapper;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.handybook.module.reschedule.RescheduleUpcomingActivity;
import com.handybook.handybook.ui.view.ProAvatarView;
import com.handybook.shared.layer.ui.AttachmentItemView;
import com.handybook.shared.layer.ui.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.squareup.otto.Bus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static com.handybook.handybook.booking.model.Service.PREFIX_CLEAN_CONSTANT;
import static com.handybook.handybook.constant.BundleKeys.PRO_TEAM_PRO;
import static com.handybook.handybook.constant.BundleKeys.PRO_TEAM_PRO_PREFERENCE;
import static com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel.from;

/**
 * This is a derivation of the MessagesListActivity that allows for a reschedule flow
 */
public class ProMessagesActivity extends MessagesListActivity
{
    @Inject
    UserManager mUserManager;

    @Inject
    ProTeamManager mProTeamManager;

    @Inject
    DataManager mDataManager;

    @Inject
    BookingManager mBookingManager;

    @Inject
    Bus mBus;

    private AttachmentItemView mRescheduleButton;
    private AttachmentItemView mNewBookingButton;
    private ProgressDialog mProgressDialog;
    private Service mCleaningService;
    private ProTeamProViewModel mProTeamProViewModel;
    private int mAttachmentViewItemHeight;
    private User mUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((BaseApplication) getApplication()).inject(this);

        ProTeamPro pro = (ProTeamPro) getIntent().getSerializableExtra(PRO_TEAM_PRO);
        ProviderMatchPreference matchPreference = (ProviderMatchPreference) getIntent().getSerializableExtra(
                PRO_TEAM_PRO_PREFERENCE);

        if (pro != null && matchPreference != null)
        {
            mProTeamProViewModel = from(pro, matchPreference, false);
        }

        updateProAvatar();
        mAttachmentViewItemHeight = getResources().getDimensionPixelSize(R.dimen.attachment_item_height);

        setupCustomAttachmentMenus();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.rescheduling));

        mUser = mUserManager.getCurrentUser();
        if (mUser == null)
        {
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

    private void updateProAvatar()
    {
        if (mProTeamProViewModel != null)
        {
            int size = getResources().getDimensionPixelSize(R.dimen.chat_toolbar_icon_size);
            ProAvatarView avatar = new ProAvatarView(this, size);
            avatar.bindPro(mProTeamProViewModel);
            mAvatarContainer.addView(avatar);
            mAvatarContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Figures out, from cache, what service represents the "home_cleaning" service
     */
    private void initCleaningService()
    {
        //we can safely assume that by this point, there is a valid cached services
        List<Service> cachedServices = mDataManager.getCachedServices();
        if (cachedServices != null)
        {
            for (final Service service : cachedServices)
            {
                if (service.getUniq().equalsIgnoreCase("home_cleaning"))
                {
                    mCleaningService = service;
                    return;
                }
            }
        }
    }

    @Override
    protected void setConversation(final Conversation conversation)
    {
        super.setConversation(conversation);
        refreshAttachmentMenu();
    }

    /**
     * Syncs up the attachment menu buttons. This should only be called after conversation has been
     * set
     */
    private void refreshAttachmentMenu()
    {
        if (mRescheduleButton == null)
        {
            return;
        }
        if (mProTeamProViewModel != null)
        {
            mRescheduleButton.setVisibility(View.VISIBLE);
        }
        else
        {
            //likely coming from a deep link or something, we need to fetch it
            mRescheduleButton.setVisibility(View.GONE);
            mProTeamManager.requestProTeam(new ProTeamCallback(this));
        }
    }

    public void onProTeamReceived(ProTeam proTeam)
    {
        if (proTeam != null && proTeam.getAllCategories() != null)
        {
            if (proTeam.getAllCategories().getPreferred() != null)
            {
                for (final ProTeamPro preferred : proTeam.getAllCategories().getPreferred())
                {
                    if (preferred.isChatEnabled()
                            && containsId(
                            mConversation.getParticipants(),
                            preferred.getLayerUserId()
                    ))
                    {

                        mProTeamProViewModel = from(
                                preferred,
                                ProviderMatchPreference.PREFERRED,
                                false
                        );
                        updateProAvatar();
                        refreshAttachmentMenu();
                        return;
                    }
                }
            }

            if (proTeam.getAllCategories().getIndifferent() != null)
            {
                for (final ProTeamPro indifferent : proTeam.getAllCategories().getIndifferent())
                {
                    if (indifferent.isChatEnabled()
                            && containsId(
                            mConversation.getParticipants(),
                            indifferent.getLayerUserId()
                    ))
                    {
                        mProTeamProViewModel = from(
                                indifferent,
                                ProviderMatchPreference.INDIFFERENT,
                                false
                        );
                        updateProAvatar();
                        refreshAttachmentMenu();
                        return;
                    }
                }
            }
        }

        //by the time we get here, if we didn't find any matching pro, that means we're not supposed
        //to be in this conversation to begin with. Exit. Most of the time this should not happen.
        if (mProTeamProViewModel == null)
        {
            Toast.makeText(this, R.string.conversation_cannot_load, Toast.LENGTH_SHORT);
            startActivity(new Intent(this, ProTeamActivity.class));
            finish();
        }

    }

    private boolean containsId(Set<Identity> participants, String id)
    {
        for (final Identity participant : participants)
        {
            if (participant.getUserId().equals(id))
            {
                return true;
            }
        }

        return false;
    }

    private void setupCustomAttachmentMenus()
    {
        getAttachmentButton().setImageResource(R.drawable.ic_calendar_plus);

        mRescheduleButton = getRescheduleView();
        mNewBookingButton = getNewBookingView();

        addAttachmentMenuItem(mRescheduleButton);
        addAttachmentMenuItem(mNewBookingButton);
    }

    private AttachmentItemView getNewBookingView()
    {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_make_booking);
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.make_a_booking));
        attachmentItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getAttachmentMenu().dismiss();
                if (mCleaningService != null
                        && mProTeamProViewModel != null
                        && mProTeamProViewModel.getProTeamPro().getCategoryType()
                                               .toString()
                                               .toLowerCase()
                                               .contains(PREFIX_CLEAN_CONSTANT)
                        )
                {

                    //for cleaning, we want to start the booking flow right away.
                    mBus.post(new LogEvent.AddLogEvent(
                            new ChatLog.MakeBookingSelectedLog(
                                    String.valueOf(mProTeamProViewModel.getProTeamPro().getId()),
                                    mConversation.getId().toString(),
                                    String.valueOf(mCleaningService.getId())
                    )));
                    startBookingFlow();
                }
                else
                {
                    mBus.post(new LogEvent.AddLogEvent(new ChatLog.MakeBookingSelectedLog(String.valueOf(
                            mProTeamProViewModel.getProTeamPro().getId()),
                                                                                          mConversation
                                                                                                  .getId()
                                                                                                  .toString(),
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
    private void startBookingFlow()
    {
        final BookingRequest request = new BookingRequest();
        request.setServiceId(mCleaningService.getId());
        request.setUniq(mCleaningService.getUniq());
        request.setCoupon(mBookingManager.getPromoTabCoupon());
        if (mUser != null)
        {
            request.setEmail(mUser.getEmail());
        }
        mBookingManager.clear();
        mBookingManager.setCurrentRequest(request);
        final Intent intent = new Intent(this, BookingLocationActivity.class);
        startActivity(intent);
    }

    private AttachmentItemView getRescheduleView()
    {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_reschedule);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.reschedule));
        attachmentItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getAttachmentMenu().dismiss();
                mBus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleSelectedLog(String.valueOf(
                        mProTeamProViewModel.getProTeamPro().getId()),
                                                                                     mConversation.getId()
                                                                                                  .toString()
                )));

                //before we send the mUser to the reschedule bookings page, we check to make sure
                //there are upcoming bookings to reschedule

                mProgressDialog.show();
                mDataManager.getBookingsForReschedule(
                        String.valueOf(mProTeamProViewModel.getProTeamPro().getId()),
                        new BookingsCallback(ProMessagesActivity.this)
                );
            }
        });

        return attachmentItemView;
    }

    public void onBookingReceived(final List<Booking> bookings)
    {
        mProgressDialog.dismiss();

        if (bookings == null || bookings.isEmpty())
        {
            mBus.post(new LogEvent.AddLogEvent(new ChatLog.NoUpcomingBookingsLog()));
            Toast.makeText(
                    this,
                    getString(
                            R.string.reschedule_no_bookings_formatted,
                            mProTeamProViewModel.getProTeamPro().getName()
                    ),
                    Toast.LENGTH_LONG
            ).show();
        }
        else
        {
            Intent intent = new Intent(
                    ProMessagesActivity.this,
                    RescheduleUpcomingActivity.class
            );

            intent.putExtra(
                    BundleKeys.PROVIDER_ID,
                    String.valueOf(mProTeamProViewModel.getProTeamPro().getId())
            );
            intent.putExtra(BundleKeys.BOOKINGS, (Serializable) bookings);
            startActivity(intent);
        }
    }

    public void onBookingsRequestError()
    {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    public static class ProTeamCallback implements DataManager.Callback<ProTeamWrapper>
    {
        private WeakReference<ProMessagesActivity> mActivityRef;

        public ProTeamCallback(ProMessagesActivity activity)
        {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final ProTeamWrapper response)
        {
            if (mActivityRef.get() != null)
            {
                mActivityRef.get().onProTeamReceived(response.getProTeam());
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            //we don't need to do anything here, by default the reschedule stuff doesn't show
        }
    }


    private static class BookingsCallback implements DataManager.Callback<UserBookingsWrapper>
    {

        private final WeakReference<ProMessagesActivity> mActivity;

        public BookingsCallback(ProMessagesActivity activity)
        {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final UserBookingsWrapper response)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onBookingReceived(response.getBookings());
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onBookingsRequestError();
            }
        }
    }
}

