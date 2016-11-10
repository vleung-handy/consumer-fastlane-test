package com.handybook.handybook.module.proteam.ui.fragment;

import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.LayerUtil;
import com.layer.atlas.util.picasso.transformations.CircleTransform;
import com.layer.sdk.messaging.Message;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConversationHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.conversation_image)
    ImageView mImageView;

    @Bind(R.id.conversation_unread_indicator)
    ImageView mUnreadIndicator;

    @Bind(R.id.conversation_title)
    TextView mTextTitle;

    @Bind(R.id.conversation_message)
    TextView mTextMessage;

    @Bind(R.id.conversation_timestamp)
    TextView mTextTimestamp;

    private ProTeamProViewModel mProTeamProViewModel;

    @ColorInt
    private int mHandyTertiaryGray;
    @ColorInt
    private int mHandyTextBlack;

    private Typeface mBoldTypeFace;
    private Typeface mNormalTypeFace;
    private String mNewConversationMessage;

    public ConversationHolder(
            final View itemView,
            @ColorInt final int handyTertiaryGray,
            @ColorInt final int handyTextBlack,
            @NonNull final Typeface boldTypeFace,
            @NonNull final Typeface normalTypeFace,
            @NonNull final String newConversationMessage
    )
    {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mHandyTertiaryGray = handyTertiaryGray;
        mHandyTextBlack = handyTextBlack;
        mBoldTypeFace = boldTypeFace;
        mNormalTypeFace = normalTypeFace;
        mNewConversationMessage = newConversationMessage;
    }

    public void bind(@NonNull final ProTeamProViewModel proTeamProViewModel)
    {
        mProTeamProViewModel = proTeamProViewModel;

        if (!TextUtils.isEmpty(mProTeamProViewModel.getImageUrl()))
        {
            Picasso.with(mImageView.getContext())
                   .load(mProTeamProViewModel.getImageUrl())
                   .placeholder(R.drawable.img_pro_placeholder)
                   .noFade()
                   .transform(new CircleTransform(mProTeamProViewModel.getImageUrl()))
                   .into(mImageView);
        }
        else
        {
            mImageView.setImageResource(R.drawable.img_pro_placeholder);
        }

        mTextTitle.setText(mProTeamProViewModel.getTitle());
        mTextMessage.setText(mNewConversationMessage);
        mTextMessage.setTextColor(mHandyTertiaryGray);
        mTextMessage.setTypeface(mNormalTypeFace);
        mTextTitle.setTypeface(mNormalTypeFace);
        mUnreadIndicator.setVisibility(View.GONE);

        bindWithLayer();
    }

    private void bindWithLayer()
    {
        if (mProTeamProViewModel.getConversation() == null)
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

        if (mProTeamProViewModel.getConversation().getLastMessage() != null) {
            mTextTimestamp.setText(
                    DateTimeUtils.getTime(
                            mProTeamProViewModel
                                    .getConversation()
                                    .getLastMessage()
                                    .getSentAt()
                        )
                );

            mTextTimestamp.setVisibility(View.VISIBLE);
        } else {
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
