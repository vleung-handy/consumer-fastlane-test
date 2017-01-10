package com.handybook.handybook.proteam.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.ProAvatarView;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.layer.LayerUtil;
import com.layer.sdk.messaging.Message;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;

import static com.handybook.handybook.booking.model.Service.PREFIX_CLEAN_CONSTANT;

public class ConversationHolder extends RecyclerView.ViewHolder
{

    @Bind(R.id.conversation_avatar)
    ProAvatarView mProAvatarView;

    @Bind(R.id.conversation_unread_indicator)
    ImageView mUnreadIndicator;

    @Bind(R.id.conversation_title)
    TextView mTextTitle;

    @Bind(R.id.conversation_message)
    TextView mTextMessage;

    @Bind(R.id.conversation_timestamp)
    TextView mTextTimestamp;

    @Bind(R.id.conversation_currently_assigned)
    TextView mTextCurrent;

    @Bind(R.id.conversation_type_indicator)
    ImageView mServiceTypeIndicator;

    private ProTeamProViewModel mProTeamProViewModel;

    @BindColor(R.color.handy_tertiary_gray)
    @ColorInt
    int mHandyTertiaryGray;

    @BindColor(R.color.handy_text_black)
    @ColorInt
    int mHandyTextBlack;

    @BindString(R.string.new_conversation_text)
    String mNewConversationMessage;

    private Typeface mBoldTypeFace;
    private Typeface mNormalTypeFace;
    private boolean mHideConversation;
    private String mProviderId;

    public ConversationHolder(
            final View itemView,
            final boolean hideConversation,
            @Nullable final String providerId
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Context context = itemView.getContext();

        mBoldTypeFace = com.handybook.handybook.library.util.TextUtils.get(
                context,
                com.handybook.handybook.library.util.TextUtils.Fonts.CIRCULAR_BOLD
        );
        mNormalTypeFace = com.handybook.handybook.library.util.TextUtils.get(
                context,
                com.handybook.handybook.library.util.TextUtils.Fonts.CIRCULAR_BOOK
        );

        mHideConversation = hideConversation;
        mProviderId = providerId;
    }

    public void bind(@NonNull final ProTeamProViewModel proTeamProViewModel)
    {
        mProTeamProViewModel = proTeamProViewModel;
        mProAvatarView.bindPro(proTeamProViewModel);
        mTextTitle.setText(mProTeamProViewModel.getTitle());
        if (mHideConversation)
        {
            mTextMessage.setVisibility(View.GONE);
            mTextTimestamp.setVisibility(View.GONE);
        }
        else
        {
            mTextMessage.setText(mNewConversationMessage);
            mTextMessage.setTextColor(mHandyTertiaryGray);
            mTextMessage.setTypeface(mNormalTypeFace);
        }
        mTextTitle.setTypeface(mNormalTypeFace);
        mUnreadIndicator.setVisibility(View.INVISIBLE);

        if (!mProTeamProViewModel.getProTeamPro().getCategoryType()
                                 .toString()
                                 .toLowerCase()
                                 .contains(PREFIX_CLEAN_CONSTANT))
        {
            mServiceTypeIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            mServiceTypeIndicator.setVisibility(View.GONE);
        }

        if (Integer.toString(proTeamProViewModel.getProTeamPro().getId()).equals(mProviderId))
        {
            mTextCurrent.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextCurrent.setVisibility(View.GONE);
        }

        bindWithLayer();
    }

    private void bindWithLayer()
    {
        if (mProTeamProViewModel.getConversation() == null || mHideConversation)
        {
            //there is no conversation to bind, just don't do anything.
            return;
        }

        Message lastMessage = mProTeamProViewModel.getConversation().getLastMessage();
        if (lastMessage != null)
        {
            String message = LayerUtil.getLastMessageString(mTextMessage.getContext(), lastMessage);
            mTextMessage.setText(message);
            mTextMessage.setTextColor(mHandyTextBlack);
        }

        if (mProTeamProViewModel.getConversation().getLastMessage() != null)
        {
            mTextTimestamp.setText(
                    DateTimeUtils.formatDateToRelativeAccuracy(
                            mProTeamProViewModel
                                    .getConversation()
                                    .getLastMessage()
                                    .getReceivedAt()
                    )
            );

            mTextTimestamp.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextTimestamp.setVisibility(View.GONE);
        }

        //if there are unreads, make the entire thing bold
        Integer unreadMessages = mProTeamProViewModel.getConversation()
                                                     .getTotalUnreadMessageCount();
        if (unreadMessages != null && unreadMessages > 0)
        {
            mTextMessage.setTypeface(mBoldTypeFace);
            mTextTitle.setTypeface(mBoldTypeFace);
            mUnreadIndicator.setVisibility(View.VISIBLE);
        }
    }
}
