package com.handybook.handybook.module.proteam.ui.fragment;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.handylayer.LayerRecyclerAdapter;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtse on 11/2/16.
 */
public class ProConversationAdapter extends LayerRecyclerAdapter<ConversationHolder>
{
    private static final String TAG = ProConversationAdapter.class.getName();

    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeam.ProTeamCategory mProTeamCategory;
    private final LayerClient mLayerClient;
    private final View.OnClickListener mOnClickListener;

    //TODO: JIA: remove this after friday demo
    private boolean mIsDemo;

    public ProConversationAdapter(
            boolean isDemo,
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final LayerClient layerClient,
            @NonNull final View.OnClickListener onClickListener
    )
    {
        super(layerClient);
        mProTeamCategory = proTeamCategory;
        mLayerClient = layerClient;
        mOnClickListener = onClickListener;
        mIsDemo = isDemo;
        initProTeamProViewModels();
    }

    private void initProTeamProViewModels()
    {
        mProTeamProViewModels = new ArrayList<>();
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
        if (!mIsDemo) {
            if (mProTeamProViewModels == null || mProTeamProViewModels.isEmpty())
            {
                Log.d(TAG, "onConversationUpdated: Pro team not initialized yet, don't do anything.");
                return;
            }
        }

        //get a list of all conversations and see if we can match them with the pro teams
        List<Conversation> allConversations = getAllConversations();


        if (!mIsDemo)
        {
            //update each pro team model with the correct conversation
            for (final ProTeamProViewModel model : mProTeamProViewModels)
            {
                if (model.getConversation() == null)
                {
                    //if there isn't a conversation tied to the pro, check to see if there is one
                    //that just got created.

                    int proId = model.getProTeamPro().getId();
                    for (final Conversation convo : allConversations)
                    {
                        boolean conversationSet = false;
                        for (final Identity participant : convo.getParticipants())
                        {
                            //TODO: JIA: this is a hardcoded criteria, remove this
                            if (proId == 27698 && participant.getUserId().equals("19"))
                            {
                                //this the conversation with DanH
                                model.setConversation(convo);
                                conversationSet = true;
                                break;
                            }
                            else if (proId == 27685 && participant.getUserId().equals("17"))
                            {
                                //27685 is mark S in staging, and 17 is "MarkyS" (case sensitive) in the sample app
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
        } else {
            //if this is for demo, we just show conversations verbatim.
            mProTeamProViewModels = new ArrayList<>();
            for (final Conversation convo : allConversations) {
                ProTeamProViewModel model = new ProTeamProViewModel();
                model.setConversation(convo);
                mProTeamProViewModels.add(model);
            }
        }

        //TODO: JIA: if we ever need to sort this, the code below will help, but it gets a little inconsistent
//because the monolith returns unsorted things, and we can't display anything until we get the
//layer stuff to return conversations before we can sort.

//        //now sort the pro team models, by the order of the conversation it should be showing
//        //the top N conversations should match the top N pro team members
//        //this assumes there are no conversations that does not belong to a pro
//        for (int i = 0; i < allConversations.size(); i++)
//        {
//            Conversation conversation = mProTeamProViewModels.get(i).getConversation();
//            if (conversation != null && !conversation.getId()
//                                                     .equals(allConversations.get(i).getId()))
//            {
//                swapProIndexes(i, allConversations.get(i).getId());
//            }
//
//            //TODO: JIA: this is really just testing code. Remove this later.
//            conversation = mProTeamProViewModels.get(i).getConversation();
//            if (conversation != null && !conversation.getId()
//                                                     .equals(allConversations.get(i).getId()))
//            {
//                throw new RuntimeException("There was an issue sorting the list of pro team");
//            }
//        }

        notifyDataSetChanged();
    }

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_pro_team_conversation_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);

        return new ConversationHolder(itemView, mLayerClient.getAuthenticatedUser());
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


    /**
     * Loops through the list of pro team members, starting at the currentProIndex until it finds a
     * member with the given conversationId. That member will now be inserted at the
     * currentProIndex
     *
     * @param currentProIndex
     * @param conversationId
     */
    private void swapProIndexes(int currentProIndex, Uri conversationId)
    {
        for (int i = currentProIndex; i < mProTeamProViewModels.size(); i++)
        {
            Conversation conversation = mProTeamProViewModels.get(i).getConversation();
            if (conversation != null && conversation.getId().equals(conversationId))
            {
                ProTeamProViewModel removed = mProTeamProViewModels.remove(i);
                mProTeamProViewModels.add(currentProIndex, removed);
                break;
            }
        }
    }

}
