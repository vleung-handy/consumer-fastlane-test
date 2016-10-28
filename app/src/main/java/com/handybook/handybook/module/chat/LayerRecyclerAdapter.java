package com.handybook.handybook.module.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

import java.util.List;

/**
 * Created by jtse on 10/26/16.
 */
public abstract class LayerRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements RecyclerViewController.Callback
{
    protected final LayerClient mLayerClient;
    private final RecyclerViewController<Conversation> mQueryController;
    private long mInitialHistory = 20;

    protected LayerRecyclerAdapter(@NonNull final LayerClient layerClient)
    {
        Query<Conversation> query = Query.builder(Conversation.class)
                /* Only show conversations we're still a member of */
                                         .predicate(new Predicate(
                                                 Conversation.Property.PARTICIPANT_COUNT,
                                                 Predicate.Operator.GREATER_THAN,
                                                 1
                                         ))

                /* Sort by the last Message's receivedAt time */
                                         .sortDescriptor(new SortDescriptor(
                                                 Conversation.Property.LAST_MESSAGE_RECEIVED_AT,
                                                 SortDescriptor.Order.DESCENDING
                                         ))
                                         .build();

        mQueryController = layerClient.newRecyclerViewController(query, null, this);
        mLayerClient = layerClient;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position)
    {
        mQueryController.updateBoundPosition(position);
    }

    /**
     * Refreshes conversations by re-running the underlying Query.
     */
    public void refreshLayer()
    {
        mQueryController.execute();
    }

//    /**
//     * Initial Message History
//     * @param initialHistory
//     * @return
//     */
//    public ProTeamCategoryAdapter setInitialHistoricMessagesToFetch(long initialHistory) {
//        mInitialHistory = initialHistory;
//        return this;
//    }

    public List<Conversation> getAllConversations()
    {
//TODO: IF this doesn't work, use the commented code below.
        return mLayerClient.getConversations();

//        List<Conversation> conversations = new ArrayList<>();
//
//        for (int i = 0; i < mQueryController.getItemCount(); i++)
//        {
//            conversations.add(getConversationItem(i));
//        }
//
//        return conversations;
    }

    public Conversation getConversationItem(int position)
    {
        return mQueryController.getItem(position);
    }

    private void syncInitialMessages(final int start, final int length)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                long desiredHistory = mInitialHistory;
                if (desiredHistory <= 0) { return; }
                for (int i = start; i < start + length; i++)
                {
                    try
                    {
                        final Conversation conversation = getConversationItem(i);
                        if (conversation == null || conversation.getHistoricSyncStatus() != Conversation.HistoricSyncStatus.MORE_AVAILABLE)
                        {
                            continue;
                        }
                        Query<Message> localCountQuery = Query.builder(Message.class)
                                                              .predicate(new Predicate(
                                                                      Message.Property.CONVERSATION,
                                                                      Predicate.Operator.EQUAL_TO,
                                                                      conversation
                                                              ))
                                                              .build();
                        long delta = desiredHistory - mLayerClient.executeQueryForCount(
                                localCountQuery);
                        if (delta > 0) { conversation.syncMoreHistoricMessages((int) delta); }
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        // Concurrent modification
                    }
                }
            }
        }).start();
    }

    protected abstract void onConversationUpdated();

    //==============================================================================================
    // UI update callbacks
    //==============================================================================================

    @Override
    public void onQueryDataSetChanged(RecyclerViewController controller)
    {
        syncInitialMessages(0, getItemCount());
        onConversationUpdated();
//        notifyDataSetChanged();
    }

    @Override
    public void onQueryItemChanged(RecyclerViewController controller, int position)
    {
        onConversationUpdated();
//        notifyItemChanged(position);
    }

    @Override
    public void onQueryItemRangeChanged(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        onConversationUpdated();
//        notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onQueryItemInserted(RecyclerViewController controller, int position)
    {
        syncInitialMessages(position, 1);
        onConversationUpdated();
//        notifyItemInserted(position);
    }

    @Override
    public void onQueryItemRangeInserted(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        syncInitialMessages(positionStart, itemCount);
        onConversationUpdated();
//        notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onQueryItemRemoved(RecyclerViewController controller, int position)
    {
        onConversationUpdated();
//        notifyItemRemoved(position);
    }

    @Override
    public void onQueryItemRangeRemoved(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        onConversationUpdated();
//        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onQueryItemMoved(
            RecyclerViewController controller,
            int fromPosition,
            int toPosition
    )
    {
        onConversationUpdated();
//        notifyItemMoved(fromPosition, toPosition);
    }
}
