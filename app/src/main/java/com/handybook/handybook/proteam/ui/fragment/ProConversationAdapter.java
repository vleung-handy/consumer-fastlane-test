package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.library.ui.viewholder.SingleViewHolder;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.ui.view.ProTeamProItemView;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.layer.LayerHelper;
import com.handybook.shared.layer.ui.LayerRecyclerAdapter;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProConversationAdapter extends LayerRecyclerAdapter<RecyclerView.ViewHolder> {

    private static final int NORMAL = Integer.MIN_VALUE;

    private List<SingleViewHolder> mHeaders = new ArrayList<>();
    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeam.ProTeamCategory mProTeamCategory;
    private final LayerHelper mLayerHelper;
    private final View.OnClickListener mOnClickListener;
    private List<String> mChatEligibleMemberIds;
    private EventBus mBus;
    private boolean mHideConversation;

    /**
     * We're using this flag to denote the first time conversations became available
     */
    private boolean mConversationLoaded = false;

    public ProConversationAdapter(
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final LayerHelper layerHelper,
            @NonNull final View.OnClickListener onClickListener,
            @NonNull final EventBus bus
    ) {
        super(layerHelper);
        mProTeamCategory = proTeamCategory;
        mLayerHelper = layerHelper;
        mOnClickListener = onClickListener;
        mBus = bus;
        initProTeamProViewModels();
    }

    public void addHeader(@NonNull View header) {
        mHeaders.add(new SingleViewHolder(header));
    }

    public void setHideConversation(boolean hideConversation) {
        mHideConversation = hideConversation;
    }

    private void initProTeamProViewModels() {
        mProTeamProViewModels = new ArrayList<>();
        mChatEligibleMemberIds = new ArrayList<>();

        if (mProTeamCategory != null) {
            final List<Provider> preferredPros = mProTeamCategory.getPreferred();
            if (preferredPros != null) {
                for (Provider eachPro : preferredPros) {
                    //we only want to show on the screen where chat is enabled.
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.PREFERRED,
                            false
                    ));
                    mChatEligibleMemberIds.add(eachPro.getLayerUserId());
                }
            }
            final List<Provider> indifferentPros = mProTeamCategory.getIndifferent();
            if (indifferentPros != null) {
                for (Provider eachPro : indifferentPros) {
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
        onConversationUpdated();
        setHasStableIds(true);
    }

    /**
     * conversations could've changed. See if we need to update the screen.
     */
    @Override
    protected void onConversationUpdated() {
        //get a list of all conversations and see if we can match them with the pro teams
        List<Conversation> conversations = mLayerHelper.getAllConversationsWith(
                mChatEligibleMemberIds);

        if (conversations == null) {
            return;
        }

        if (!mConversationLoaded && mProTeamProViewModels != null) {
            //the first update we get is essentially the "loaded" part. Subsequent updates are for changes.
            mConversationLoaded = true;
            mBus.post(new LogEvent.AddLogEvent(
                    new ChatLog.ConversationsShownLog(
                            mChatEligibleMemberIds.size(),
                            conversations.size()
                    )
            ));
        }

        //update each pro team model with the correct conversation
        for (final ProTeamProViewModel model : mProTeamProViewModels) {
            if (model.getConversation() == null) {
                //if there isn't a conversation tied to the pro, check to see if there is one
                //that just got created.

                String proLayerId = model.getProTeamPro().getLayerUserId();
                for (final Conversation convo : conversations) {
                    boolean conversationSet = false;
                    for (final Identity participant : convo.getParticipants()) {
                        if (participant.getUserId().equals(proLayerId)) {
                            //this the conversation with DanH
                            model.setConversation(convo);
                            conversationSet = true;
                            break;
                        }
                    }
                    if (conversationSet) {
                        break;
                    }
                }
            }
        }

        sortByMessageReadDate();
        notifyDataSetChanged();
    }

    private void sortByMessageReadDate() {
        Collections.sort(mProTeamProViewModels, new ProConversationComparator());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL) {
            final ProTeamProItemView itemView =
                    new ProTeamProItemView(
                            parent.getContext(),
                            mHideConversation,
                            null
                    );
            itemView.setOnClickListener(mOnClickListener);

            return new ConversationHolder(itemView);
        }
        else // Header
        {
            return mHeaders.get(viewType);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position >= mHeaders.size() &&
            position < mProTeamProViewModels.size() + mHeaders.size()) {
            super.onBindViewHolder(holder, position - mHeaders.size());
            ((ConversationHolder) holder).bind(getItem(position - mHeaders.size()));
        }
    }

    @Override
    public int getItemCount() {
        return mProTeamProViewModels.size() + mHeaders.size();
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }

    public ProTeamProViewModel getItem(final int index) {
        return mProTeamProViewModels.get(index);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        // We return the position as type if it's header.
        return position >= mHeaders.size() ? NORMAL : position;
    }
}
