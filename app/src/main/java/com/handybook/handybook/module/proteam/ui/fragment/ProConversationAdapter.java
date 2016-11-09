package com.handybook.handybook.module.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.LayerHelper;
import com.handybook.shared.LayerRecyclerAdapter;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import java.util.ArrayList;
import java.util.List;

public class ProConversationAdapter extends LayerRecyclerAdapter<ConversationHolder>
{
    private static final String TAG = ProConversationAdapter.class.getName();

    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeam.ProTeamCategory mProTeamCategory;
    private final LayerHelper mLayerHelper;
    private final View.OnClickListener mOnClickListener;
    private List<String> mChatEligibleMemberIds;

    public ProConversationAdapter(
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final LayerHelper layerHelper,
            @NonNull final View.OnClickListener onClickListener
    )
    {
        super(layerHelper);
        mProTeamCategory = proTeamCategory;
        mLayerHelper = layerHelper;
        mOnClickListener = onClickListener;
        initProTeamProViewModels();
    }

    private void initProTeamProViewModels()
    {
        mProTeamProViewModels = new ArrayList<>();
        mChatEligibleMemberIds = new ArrayList<>();

        if (mProTeamCategory != null)
        {
            final List<ProTeamPro> preferredPros = mProTeamCategory.getPreferred();
            if (preferredPros != null)
            {
                for (ProTeamPro eachPro : preferredPros)
                {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.PREFERRED,
                            false
                    ));

                    if (eachPro.isChatEnabled())
                    {
                        mChatEligibleMemberIds.add(eachPro.getLayerUserId());
                    }
                }
            }
            final List<ProTeamPro> indifferentPros = mProTeamCategory.getIndifferent();
            if (indifferentPros != null)
            {
                for (ProTeamPro eachPro : indifferentPros)
                {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.INDIFFERENT,
                            false
                    ));
                }
            }
        }
    }


    /**
     * conversations could've changed. See if we need to update the screen.
     * <p>
     * //TODO: JIA: finish this once we figure out how to create users (with own user id) on the
     * client side directly with the Layer API
     */
    @Override
    protected void onConversationUpdated()
    {
        //get a list of all conversations and see if we can match them with the pro teams
        List<Conversation> conversations = mLayerHelper.getAllConversationsWith(
                mChatEligibleMemberIds);

        //update each pro team model with the correct conversation
        for (final ProTeamProViewModel model : mProTeamProViewModels)
        {
            if (model.getConversation() == null)
            {
                //if there isn't a conversation tied to the pro, check to see if there is one
                //that just got created.

                String proId = model.getProTeamPro().getLayerUserId();
                for (final Conversation convo : conversations)
                {
                    boolean conversationSet = false;
                    for (final Identity participant : convo.getParticipants())
                    {
                        //TODO: JIA: this is a hardcoded criteria, remove this
                        if (participant.getUserId().equals(proId))
                        {
                            //this the conversation with DanH
                            model.setConversation(convo);
                            conversationSet = true;
                            break;
                        }
                    }
                    if (conversationSet)
                    {
                        break;
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_pro_team_conversation_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);

        return new ConversationHolder(
                itemView,
                mLayerHelper.getLayerClient().getAuthenticatedUser()
        );
    }

    @Override
    public void onBindViewHolder(final ConversationHolder holder, int position)
    {
        super.onBindViewHolder(holder, position);
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount()
    {
        return mProTeamProViewModels.size();
    }

    public ProTeamProViewModel getItem(final int position)
    {
        return mProTeamProViewModels.get(position);
    }
}
