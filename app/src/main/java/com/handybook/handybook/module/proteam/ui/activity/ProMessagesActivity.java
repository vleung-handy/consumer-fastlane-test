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
import com.handybook.handybook.module.reschedule.RescheduleUpcomingActivity;
import com.handybook.shared.AttachmentItemView;
import com.handybook.shared.builtin.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.squareup.otto.Bus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

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
    private ProTeamPro mProTeamPro;
    private int mAttachmentViewItemHeight;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((BaseApplication) getApplication()).inject(this);

        mProTeamPro = (ProTeamPro) getIntent().getSerializableExtra(BundleKeys.PRO_TEAM_PRO);
        mAttachmentViewItemHeight = getResources().getDimensionPixelSize(R.dimen.attachment_item_height);

        setupCustomAttachmentMenus();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.rescheduling));

        User user = mUserManager.getCurrentUser();
        if (user == null)
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
        if (mProTeamPro != null)
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
        for (final ProTeamPro preferred : proTeam.getAllCategories().getPreferred())
        {
            if (preferred.isChatEnabled()
                    && containsId(mConversation.getParticipants(), preferred.getLayerUserId()))
            {
                mProTeamPro = preferred;
                refreshAttachmentMenu();
                return;
            }
        }

        for (final ProTeamPro indifferent : proTeam.getAllCategories().getIndifferent())
        {
            if (indifferent.isChatEnabled()
                    && containsId(mConversation.getParticipants(), indifferent.getLayerUserId()))
            {
                mProTeamPro = indifferent;
                refreshAttachmentMenu();
                return;
            }
        }

        //by the time we get here, if we didn't find any matching pro, that means we're not supposed
        //to be in this conversation to begin with. Exit. Most of the time this should not happen.
        if (mProTeamPro == null)
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
                          .setText(getResources().getString(R.string.new_booking));
        attachmentItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getAttachmentMenu().dismiss();
                if (mCleaningService != null && mProTeamPro != null && mProTeamPro.getCategoryType()
                                                                                  .toString()
                                                                                  .toLowerCase()
                                                                                  .contains("clean"))
                {

                    //for cleaning, we want to start the booking flow right away.
                    mBus.post(new LogEvent.AddLogEvent(new ChatLog.MakeBookingSelectedLog(String.valueOf(
                            mProTeamPro.getId()),
                                                                                          mConversation
                                                                                                  .getId()
                                                                                                  .toString(),
                                                                                          String.valueOf(
                                                                                                  mCleaningService
                                                                                                          .getId())
                    )));
                    startBookingFlow();
                }
                else
                {
                    mBus.post(new LogEvent.AddLogEvent(new ChatLog.MakeBookingSelectedLog(String.valueOf(
                            mProTeamPro.getId()), mConversation.getId().toString(), null)));
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
        final User user = mUserManager.getCurrentUser();
        if (user != null)
        {
            request.setEmail(user.getEmail());
        }
        mBookingManager.clear();
        mBookingManager.setCurrentRequest(request);
        final Intent intent = new Intent(this, BookingLocationActivity.class);
        startActivity(intent);
    }

    private AttachmentItemView getRescheduleView()
    {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_reschedule_booking);
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
                        mProTeamPro.getId()), mConversation.getId().toString())));

                //before we send the user to the reschedule bookings page, we check to make sure
                //there are upcoming bookings to reschedule

                mProgressDialog.show();
                mDataManager.getBookings(
                        mUserManager.getCurrentUser(),
                        Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING,
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
            Toast.makeText(this, "You have no bookings to reschedule.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(
                    ProMessagesActivity.this,
                    RescheduleUpcomingActivity.class
            );

            intent.putExtra(BundleKeys.PROVIDER_ID, String.valueOf(mProTeamPro.getId()));
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

