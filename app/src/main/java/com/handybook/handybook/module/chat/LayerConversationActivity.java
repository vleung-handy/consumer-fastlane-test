package com.handybook.handybook.module.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.module.chat.builtin.MessagesListActivity;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LayerConversationActivity extends LayerBaseActivity implements RecyclerViewController.Callback
{

    private static final String TAG = LayerConversationActivity.class.getName();
    private static final int MESSAGE_SYNC_AMOUNT = 20;

    @Inject
    LayerClient mLayerClient;

    @Bind(R.id.layer_conversation_main_layout)
    LinearLayout mMainLayout;

    @Inject
    AuthenticationProvider mLayerAuthProvider;

    @Inject
    LayerHelper mLayerHelper;

    @Inject
    @Named("layerAppId")
    String mLayerAppId;

    private RecyclerViewController<Conversation> mQueryController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((BaseApplication) this.getApplication()).inject(this);
        String name;

        //if the user is not logged in, then we need to redirect to login
        if (mLayerAuthProvider.routeLogin(mLayerClient, mLayerAppId))
        {
            startActivity(new Intent(this, LayerLoginActivity.class));

            if (!isFinishing()) { finish(); }
            return;
        }
        else
        {
            name = mLayerClient.getAuthenticatedUser().getDisplayName();
        }

        setContentView(R.layout.activity_layer_conversation);
        ButterKnife.bind(this);

        if (name == null)
        {
            name = getIntent().getStringExtra("user");
        }

        setTitle("Welcome, " + name);
        setupLayer();
    }

    private void setupLayer()
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

        if (mQueryController == null)
        {
            Log.d(TAG, "setupLayer: newRecyclerViewController");
            mQueryController = mLayerClient.newRecyclerViewController(query, null, this);
        }

        refresh();
    }

    private void refresh()
    {
        Log.d(TAG, "refreshing data with recycler " + mQueryController.toString());
        mQueryController.execute();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mQueryController = null;
    }

    @Override
    public void onQueryDataSetChanged(RecyclerViewController recyclerViewController)
    {
        Log.d(TAG, "onQueryDataSetChanged: " + mQueryController.getItemCount() + " conversations");

        if (mQueryController.getItemCount() == 0)
        {
            Snackbar.make(mMainLayout, "There are NO existing conversations", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        //remove everything except the title
        while (mMainLayout.getChildCount() > 1)
        {
            mMainLayout.removeViewAt(mMainLayout.getChildCount() - 1);
        }

        for (int i = 0; i < mQueryController.getItemCount(); i++)
        {
            addConversationToList(mQueryController.getItem(i));
        }
    }

    @Override
    public void onQueryItemChanged(RecyclerViewController recyclerViewController, int i)
    {
        Log.d(
                TAG,
                "onQueryItemChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
        );
    }

    @Override
    public void onQueryItemRangeChanged(
            RecyclerViewController recyclerViewController,
            int i,
            int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemInserted(RecyclerViewController recyclerViewController, int position)
    {
        Log.d(
                TAG,
                "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + position + "]"
        );

        if (mQueryController == null)
        {
            return;
        }

        Conversation conversation = mQueryController.getItem(position);

        if (mMainLayout.getChildCount() > 1)
        {
            for (int i = 0; i < mMainLayout.getChildCount(); i++)
            {
                View view = mMainLayout.getChildAt(i);
                if (conversation.getId().equals(view.getTag()))
                {
                    return;
                }
            }
        }

        //this conversation doesn't exist, so put it in;
        addConversationToList(conversation);
    }

    private void addConversationToList(Conversation conversation)
    {

        if (conversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.MORE_AVAILABLE)
        {
            conversation.syncMoreHistoricMessages(MESSAGE_SYNC_AMOUNT);
        }


        //instead of adding to the view, immediately redirect to the chat screen. There should only be one conversation going on
        Intent intent = new Intent(LayerConversationActivity.this, MessagesListActivity.class);
        intent.putExtra("conversation_id", conversation.getId());
        startActivity(intent);

//        TextView tv = new TextView(this);
//        tv.setTag(conversation.getId());
//        tv.setText("Conversation: " + conversation.getId());
//        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: " + v.getTag());
//                Intent intent = new Intent(LayerConversationActivity.this, LayerChatActivity.class);
//                intent.putExtra("conversation_id", (Uri)v.getTag());
//                startActivity(intent);
//            }
//        });
//        mMainLayout.addView(tv);
    }

    @Override
    public void onQueryItemRangeInserted(
            RecyclerViewController recyclerViewController,
            int i,
            int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemRemoved(RecyclerViewController recyclerViewController, int i)
    {
        Log.d(
                TAG,
                "onQueryItemRemoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
        );
    }

    @Override
    public void onQueryItemRangeRemoved(
            RecyclerViewController recyclerViewController,
            int i,
            int i1
    )
    {
        Log.d(
                TAG,
                "onQueryItemRangeRemoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }

    @Override
    public void onQueryItemMoved(RecyclerViewController recyclerViewController, int i, int i1)
    {
        Log.d(
                TAG,
                "onQueryItemMoved() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
        );
    }
}
