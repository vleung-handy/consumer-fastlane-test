package com.handybook.handybook.module.chat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * https://developer.layer.com/projects
 * user name: jam0cam@yahoo.com
 */
public class LayerChatActivity extends AppCompatActivity implements RecyclerViewController.Callback {
    private static final String TAG = "LayerActivity";

    @Inject
    LayerClient mLayerClient;

    @Inject
    AuthenticationProvider mLayerAuthProvider;

    @Inject @Named("layerAppId")
    String mLayerAppId;

    @Bind(R.id.main_container)
    CoordinatorLayout mMainContainer;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.edit_message)
    EditText mEditMessage;

    @Bind(R.id.empty_view)
    View mEmptyView;

    @BindColor(R.color.handy_white)
    int mMyTextColor;

    @BindColor(R.color.handy_text_black)
    int mOpponentTextColor;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @BindColor(R.color.handy_blue)
    int mMyBgColor;

    @BindColor(R.color.white)
    int mOppoenentBgColor;

    private RecyclerViewController<Message> mQueryController;
    private LinearLayoutManager mLayoutManager;
    private ChatRecyclerAdapter mAdapter;
    private List<ChatItem> mChatItems = new ArrayList<>();
    private Conversation mConversation;
    private ProgressDialog mProgressDialog;
    private int mSyncAmount = 25;       //amount of messages to sync at once.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseApplication) this.getApplication()).inject(this);

        //if the user is not logged in, then we need to redirect to login
        if (mLayerAuthProvider.routeLogin(mLayerClient, mLayerAppId)) {
            startActivity(new Intent(this, LayerLoginActivity.class));

            if (!isFinishing()) finish();
            return;
        }

        setContentView(R.layout.activity_layer_chat);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);

        Uri conversationId = getIntent().getParcelableExtra("conversation_id");
        mConversation = mLayerClient.getConversation(conversationId);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(conversationId.toString());

        mEditMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL) {
                    //the enter key
                    sendClicked();
                    return true;
                }
                return false;
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ChatRecyclerAdapter(
                mChatItems,
                getResources().getDimensionPixelSize(R.dimen.default_margin)
        );

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        Query<Message> query = Query.builder(Message.class)
                                    .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, mConversation))
                                    .sortDescriptor(new SortDescriptor(Message.Property.POSITION, SortDescriptor.Order.ASCENDING))
                                    .build();

        mQueryController = mLayerClient.newRecyclerViewController(query, null, this);
        mQueryController.execute();

        addHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLayerClient == null) return;
        if (mLayerClient.isAuthenticated()) {
            mLayerClient.connect();
        } else {
            mLayerClient.authenticate();
        }
        refresh();
    }

    /**
     * Refreshes the state of this `AtlasMessagesSwipeSyncLayout`.
     *
     * @return This `AtlasMessagesSwipeSyncLayout`.
     */
    private void refresh() {
        if (mConversation == null) {
            Log.d(TAG, "refresh: no conversation to refresh");
            return;
        }
        Conversation.HistoricSyncStatus status = mConversation.getHistoricSyncStatus();

        if (status == Conversation.HistoricSyncStatus.SYNC_PENDING) {
            Log.d(TAG, "refresh: Sync is pending");
            mProgressDialog.setMessage(getResources().getString(R.string.synching));
            mProgressDialog.show();
        } else if (status == Conversation.HistoricSyncStatus.MORE_AVAILABLE) {
            Log.d(TAG, "refresh: There is more messages available for synching");
            mConversation.syncMoreHistoricMessages(mSyncAmount);
        }
    }

    private ChatItem getChatItem(Message msg)
    {

        String message = getMessage(msg);
        ChatMessage chatMessage = new ChatMessage(msg.getId().toString(), message, msg.getSender().getUserId(), msg.getSentAt(), msg.isSent());
        if (!message.toLowerCase().startsWith("http://"))
        {
            return new ChatItem(chatMessage, ChatItem.Type.MESSAGE);
        }
        else
        {
            return new ChatItem(chatMessage, ChatItem.Type.IMAGE);
        }
    }

    private void updateRecyclerWithMessage(Message msg) {
        //Make sure the message is valid
        if (msg == null)
        {
            Log.d(TAG, "updateRecyclerWithMessage: exiting, msg = null");
            return;
        } else if (msg.getSender() == null || msg.getSender().getUserId() == null) {
            Log.d(TAG, "updateRecyclerWithMessage: exiting, message contains no user");
            return;
        } else if (TextUtils.isEmpty(mLayerClient.getAuthenticatedUser().getUserId())) {
            Log.d(TAG, "updateRecyclerWithMessage: exiting, Logged in user has no ID");
            return;
        }

        ChatItem item = getChatItem(msg);

        if (msg.getSender().getUserId().equals(mLayerClient.getAuthenticatedUser().getUserId()))
        {
            item.setBgColor(mMyBgColor);
            item.setTextColor(mMyTextColor);
            item.setGravity(Gravity.RIGHT);
        } else {
            item.setBgColor(mOppoenentBgColor);
            item.setTextColor(mOpponentTextColor);
            item.setGravity(Gravity.LEFT);
        }

        mChatItems.add(item);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            updateViewStatus();
        }
    }

    private void addHeader()
    {
        if (mChatItems.isEmpty()) {
            //construct header.
            ChatMessage message = new ChatMessage();
            message.setMessage("Meet your pro, Test Provider001. Let her know of any preferences, or just say 'hi'!");
            mChatItems.add(new ChatItem(message, ChatItem.Type.TITLE));
        }
    }

    private String getMessage(Message message) {
        List<MessagePart> parts = message.getMessageParts();
        for (MessagePart part : parts)
        {
            switch (part.getMimeType()) {

                case "text/plain":
                    String textMsg = new String(part.getData());
                    return textMsg;
                case "image/jpeg":
                    Bitmap imageMsg = BitmapFactory.decodeByteArray(part.getData(), 0, part.getData().length);
                    return "image/jpeg";
            }
        }

        return "Can't decode message";
    }

    @OnClick(R.id.image_send)
    public void sendClicked() {
        String text = mEditMessage.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            MessagePart messagePart = mLayerClient.newMessagePart(text);
            Message msg = mLayerClient.newMessage(messagePart);
            mConversation.send(msg);
            mEditMessage.setText("");
        }
    }

    private void updateViewStatus() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onQueryDataSetChanged(RecyclerViewController recyclerViewController) {
        Log.d(TAG, "onQueryDataSetChanged() called with: recyclerViewController = [" + recyclerViewController + "]");

        if (mQueryController.getItemCount() == 0) {
            Snackbar.make(mMainContainer, "There are NO existing messages", Snackbar.LENGTH_SHORT).show();
            return;
        }

        for (int i=0; i<mQueryController.getItemCount(); i++) {
            updateRecyclerWithMessage(mQueryController.getItem(i));
        }
    }

    @Override
    public void onQueryItemChanged(RecyclerViewController recyclerViewController, int i) {

    }

    @Override
    public void onQueryItemRangeChanged(RecyclerViewController recyclerViewController, int i, int i1) {

    }

    @Override
    public void onQueryItemInserted(RecyclerViewController recyclerViewController, int i) {
        Log.d(TAG, "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]");


        if (mQueryController == null) {
            return;
        }

        updateRecyclerWithMessage(mQueryController.getItem(i));
    }

    @Override
    public void onQueryItemRangeInserted(RecyclerViewController recyclerViewController, int i, int i1) {

    }

    @Override
    public void onQueryItemRemoved(RecyclerViewController recyclerViewController, int i) {

    }

    @Override
    public void onQueryItemRangeRemoved(RecyclerViewController recyclerViewController, int i, int i1) {

    }

    @Override
    public void onQueryItemMoved(RecyclerViewController recyclerViewController, int i, int i1) {

    }
}
