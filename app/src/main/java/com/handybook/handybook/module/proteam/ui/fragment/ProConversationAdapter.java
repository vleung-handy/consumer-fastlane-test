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
import java.util.Collections;
import java.util.List;

public class ProConversationAdapter extends LayerRecyclerAdapter<ConversationHolder>
{
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
                    if (eachPro.isChatEnabled())
                    {
                        //we only want to show on the screen where chat is enabled.
                        mProTeamProViewModels.add(ProTeamProViewModel.from(
                                eachPro,
                                ProviderMatchPreference.PREFERRED,
                                false
                        ));
                        mChatEligibleMemberIds.add(eachPro.getLayerUserId());
                    }
                }
            }
            final List<ProTeamPro> indifferentPros = mProTeamCategory.getIndifferent();
            if (indifferentPros != null)
            {
                for (ProTeamPro eachPro : indifferentPros)
                {
                    if (eachPro.isChatEnabled())
                    {
                        //we only want to show on the screen where chat is enabled.
                        mProTeamProViewModels.add(ProTeamProViewModel.from(
                                eachPro,
                                ProviderMatchPreference.INDIFFERENT,
                                false
                        ));
                        mChatEligibleMemberIds.add(eachPro.getLayerUserId());
                    }
                }
            }
        }
        onConversationUpdated();
        setHasStableIds(true);
    }


    /**
     * conversations could've changed. See if we need to update the screen.
     */
    @Override
    protected void onConversationUpdated()
    {
        //get a list of all conversations and see if we can match them with the pro teams
        List<Conversation> conversations = mLayerHelper.getAllConversationsWith(
                mChatEligibleMemberIds);

        if (conversations == null)
        {
            return;
        }

        //update each pro team model with the correct conversation
        for (final ProTeamProViewModel model : mProTeamProViewModels)
        {
            if (model.getConversation() == null)
            {
                //if there isn't a conversation tied to the pro, check to see if there is one
                //that just got created.

                String proLayerId = model.getProTeamPro().getLayerUserId();
                for (final Conversation convo : conversations)
                {
                    boolean conversationSet = false;
                    for (final Identity participant : convo.getParticipants())
                    {
                        if (participant.getUserId().equals(proLayerId))
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

        sortByMessageReadDate();
        notifyDataSetChanged();
    }

    private void sortByMessageReadDate()
    {
        Collections.sort(mProTeamProViewModels, new ProConversationComparator());
    }

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_pro_team_conversation_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);

        return new ConversationHolder(itemView);
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

    @Override
    public long getItemId(final int position)
    {
        return getItem(position).hashCode();
    }
}
