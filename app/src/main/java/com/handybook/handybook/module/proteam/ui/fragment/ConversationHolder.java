package com.handybook.handybook.module.proteam.ui.fragment;

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
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.squareup.picasso.Picasso;

import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConversationHolder extends RecyclerView.ViewHolder
{
    @Bind(R.id.conversation_image)
    ImageView mImageView;

    @Bind(R.id.conversation_title)
    TextView mTextTitle;

    @Bind(R.id.conversation_message)
    TextView mTextMessage;

    @Bind(R.id.conversation_timestamp)
    TextView mTextTimestamp;

    private Identity mLayerIdentity;
    private ProTeamProViewModel mProTeamProViewModel;

    public ConversationHolder(final View itemView, @NonNull final Identity identity)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mLayerIdentity = identity;
    }

    public void bind(
            @NonNull final ProTeamProViewModel proTeamProViewModel
    )
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
        bindWithLayer();
    }


    private void bindWithLayer()
    {
        if (mProTeamProViewModel.getConversation() == null)
        {
            return;
        }

        Message lastMessage = mProTeamProViewModel.getConversation().getLastMessage();
        if (lastMessage != null)
        {
            String message = LayerUtil.getLastMessageString(mTextMessage.getContext(), lastMessage);
            mTextMessage.setText(message);
        }
        else
        {
            mTextMessage.setText("Click to start a conversation!");
        }
        HashSet<Identity> participants = new HashSet<>(mProTeamProViewModel.getConversation()
                                                                           .getParticipants());
        participants.remove(mLayerIdentity);

        for (final Identity id : participants)
        {
            //TODO: JIA: make sure there won't be more than one participants here. In case Handy CS
            //wants to be a part of this too.
            mTextTitle.setText(id.getDisplayName());
            break;
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
    }
}
