package com.handybook.handybook.module.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

/**
 * Created by jtse on 10/26/16.
 */
public abstract class BaseLayerRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements RecyclerViewController.Callback
{
    private Context mContext;
    protected final LayerClient mLayerClient;
    private final RecyclerViewController<Conversation> mQueryController;
    private long mInitialHistory = 20;

    protected BaseLayerRecyclerAdapter(
            @NonNull Context context,
            @NonNull final LayerClient layerClient
    )
    {
        mContext = context;

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

    //==============================================================================================
    // UI update callbacks
    //==============================================================================================

    @Override
    public void onQueryDataSetChanged(RecyclerViewController controller)
    {
        syncInitialMessages(0, getItemCount());
        notifyDataSetChanged();
    }

    @Override
    public void onQueryItemChanged(RecyclerViewController controller, int position)
    {
        notifyItemChanged(position);
    }

    @Override
    public void onQueryItemRangeChanged(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onQueryItemInserted(RecyclerViewController controller, int position)
    {
        syncInitialMessages(position, 1);
        notifyItemInserted(position);
    }

    @Override
    public void onQueryItemRangeInserted(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        syncInitialMessages(positionStart, itemCount);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onQueryItemRemoved(RecyclerViewController controller, int position)
    {
        notifyItemRemoved(position);
    }

    @Override
    public void onQueryItemRangeRemoved(
            RecyclerViewController controller,
            int positionStart,
            int itemCount
    )
    {
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onQueryItemMoved(
            RecyclerViewController controller,
            int fromPosition,
            int toPosition
    )
    {
        notifyItemMoved(fromPosition, toPosition);
    }
}
