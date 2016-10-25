package com.handybook.handybook.module.chat.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.layer.atlas.adapters.AtlasBaseAdapter;
import com.layer.atlas.util.Util;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

import java.text.DateFormat;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jiatse on 10/22/16.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> implements AtlasBaseAdapter<Conversation>, RecyclerViewController.Callback {
    protected final LayerClient mLayerClient;
    private final RecyclerViewController<Conversation> mQueryController;
    private long mInitialHistory = 0;

    private View.OnClickListener mOnClickListener;

    public ConversationAdapter(Context context, LayerClient client, View.OnClickListener clickListener) {
        Query<Conversation> query = Query.builder(Conversation.class)
                /* Only show conversations we're still a member of */
                                         .predicate(new Predicate(Conversation.Property.PARTICIPANT_COUNT, Predicate.Operator.GREATER_THAN, 1))

                /* Sort by the last Message's receivedAt time */
                                         .sortDescriptor(new SortDescriptor(Conversation.Property.LAST_MESSAGE_RECEIVED_AT, SortDescriptor.Order.DESCENDING))
                                         .build();
        mQueryController = client.newRecyclerViewController(query, null, this);
        mOnClickListener = clickListener;
        mLayerClient = cli  ent;

        setHasStableIds(false);
    }

    /**
     * Refreshes this adapter by re-running the underlying Query.
     */
    public void refresh() {
        mQueryController.execute();
    }


    //==============================================================================================
    // Initial message history
    //==============================================================================================

    public ConversationAdapter setInitialHistoricMessagesToFetch(long initialHistory) {
        mInitialHistory = initialHistory;
        return this;
    }

    private void syncInitialMessages(final int start, final int length) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long desiredHistory = mInitialHistory;
                if (desiredHistory <= 0) return;
                for (int i = start; i < start + length; i++) {
                    try {
                        final Conversation conversation = getItem(i);
                        if (conversation == null || conversation.getHistoricSyncStatus() != Conversation.HistoricSyncStatus.MORE_AVAILABLE) {
                            continue;
                        }
                        Query<Message> localCountQuery = Query.builder(Message.class)
                                                              .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, conversation))
                                                              .build();
                        long delta = desiredHistory - mLayerClient.executeQueryForCount(localCountQuery);
                        if (delta > 0) conversation.syncMoreHistoricMessages((int) delta);
                    } catch (IndexOutOfBoundsException e) {
                        // Concurrent modification
                    }
                }
            }
        }).start();
    }


    //==============================================================================================
    // Adapter
    //==============================================================================================

    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent);
        view.setOnClickListener(mOnClickListener);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.ViewHolder viewHolder, int position) {
        mQueryController.updateBoundPosition(position);
        Conversation conversation = mQueryController.getItem(position);
        viewHolder.bind(conversation, mLayerClient.getAuthenticatedUser());
    }

    @Override
    public int getItemCount() {
        return mQueryController.getItemCount();
    }

    @Override
    public Integer getPosition(Conversation conversation) {
        return mQueryController.getPosition(conversation);
    }

    @Override
    public Integer getPosition(Conversation conversation, int lastPosition) {
        return mQueryController.getPosition(conversation, lastPosition);
    }

    @Override
    public Conversation getItem(int position) {
        return mQueryController.getItem(position);
    }

    @Override
    public Conversation getItem(RecyclerView.ViewHolder viewHolder) {
        return ((ConversationAdapter.ViewHolder) viewHolder).getConversation();
    }


    //==============================================================================================
    // UI update callbacks
    //==============================================================================================

    @Override
    public void onQueryDataSetChanged(RecyclerViewController controller) {
        syncInitialMessages(0, getItemCount());
        notifyDataSetChanged();
    }

    @Override
    public void onQueryItemChanged(RecyclerViewController controller, int position) {
        notifyItemChanged(position);
    }

    @Override
    public void onQueryItemRangeChanged(RecyclerViewController controller, int positionStart, int itemCount) {
        notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onQueryItemInserted(RecyclerViewController controller, int position) {
        syncInitialMessages(position, 1);
        notifyItemInserted(position);
    }

    @Override
    public void onQueryItemRangeInserted(RecyclerViewController controller, int positionStart, int itemCount) {
        syncInitialMessages(positionStart, itemCount);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onQueryItemRemoved(RecyclerViewController controller, int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void onQueryItemRangeRemoved(RecyclerViewController controller, int positionStart, int itemCount) {
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onQueryItemMoved(RecyclerViewController controller, int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }


    //==============================================================================================
    // Inner classes
    //==============================================================================================

    static class ViewHolder extends RecyclerView.ViewHolder  {

        @Bind(R.id.conversation_user_name)
        TextView mTextName;

        @Bind(R.id.conversation_id)
        TextView mTextConversationId;

        @Bind(R.id.conversation_last_message)
        TextView mTextLastMessage;

        private Conversation mConversation;
        private Identity mIdentity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Conversation conversation, Identity identity) {
            mConversation = conversation;
            mIdentity = identity;

            mTextConversationId.setText(mConversation.getId().toString());

            Message lastMessage = conversation.getLastMessage();
            if (lastMessage != null) {
                String message = Util.getLastMessageString(mTextName.getContext(), lastMessage);
                mTextLastMessage.setText(message);
            } else {
                mTextLastMessage.setText("Click to start a conversation!");
            }
            HashSet<Identity> participants = new HashSet<>(conversation.getParticipants());
            participants.remove(mIdentity);

            for (final Identity id : mConversation.getParticipants())
            {
                //TODO: JIA: make sure there won't be more than one participants here. In case Handy CS
                //wants to be a part of this too.
                mTextName.setText(id.getFirstName());
                break;
            }
        }

        public Conversation getConversation() {
            return mConversation;
        }
    }
}