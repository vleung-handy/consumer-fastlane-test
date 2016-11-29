package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.module.proteam.manager.ProTeamManager;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProTeamWrapper;
import com.handybook.handybook.module.reschedule.RescheduleUpcomingActivity;
import com.handybook.shared.AttachmentItemView;
import com.handybook.shared.builtin.MessagesListActivity;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import java.lang.ref.WeakReference;
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

    private AttachmentItemView mRescheduleButton;
    private AttachmentItemView mNewBookingButton;

    private String mProviderId;
    private int mAttachmentViewItemHeight;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((BaseApplication) getApplication()).inject(this);

        mProviderId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);
        mAttachmentViewItemHeight = getResources().getDimensionPixelSize(R.dimen.attachment_item_height);

        setupCustomAttachmentMenus();

        User user = mUserManager.getCurrentUser();
        if (user == null)
        {
            //we're in an invalid state, redirect to login.
            Toast.makeText(this, R.string.prompt_login, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ServiceCategoriesActivity.class));
        }

        //default to not show reschedule by default.
        mRescheduleButton.setVisibility(View.GONE);

        refreshAttachmentMenu();
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
        if (!TextUtils.isEmpty(mProviderId))
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
                mProviderId = String.valueOf(preferred.getId());
                refreshAttachmentMenu();
                return;
            }
        }

        for (final ProTeamPro indifferent : proTeam.getAllCategories().getIndifferent())
        {
            if (indifferent.isChatEnabled()
                    && containsId(mConversation.getParticipants(), indifferent.getLayerUserId()))
            {
                mProviderId = String.valueOf(indifferent.getId());
                refreshAttachmentMenu();
                return;
            }
        }

        //by the time we get here, if we didn't find any matching pro, that means we're not supposed
        //to be in this conversation to begin with. Exit. Most of the time this should not happen.
        if (TextUtils.isEmpty(mProviderId))
        {
            Toast.makeText(this, R.string.conversation_cannot_load, Toast.LENGTH_SHORT);
            startActivity(new Intent(this, ServiceCategoriesActivity.class));
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
                startActivity(new Intent(
                        ProMessagesActivity.this,
                        ServiceCategoriesActivity.class
                ));
            }
        });

        return attachmentItemView;
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
                Intent intent = new Intent(
                        ProMessagesActivity.this,
                        RescheduleUpcomingActivity.class
                );

                intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
                startActivity(intent);
            }
        });

        return attachmentItemView;
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
}

