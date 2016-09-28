package com.handybook.handybook.module.chat.builtin;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.module.chat.LayerLoginActivity;
import com.handybook.handybook.module.chat.PushNotificationReceiver;
import com.layer.atlas.AtlasHistoricMessagesFetchLayout;
import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.atlas.messagetypes.generic.GenericCellFactory;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.singlepartimage.SinglePartImageCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.typingindicators.BubbleTypingIndicatorFactory;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.LayerObject;

public class MessagesListActivity extends BaseActivity
{
    private static final int MESSAGE_SYNC_AMOUNT = 20;
    private static final String TAG = MessagesListActivity.class.getName();

    private UiState mState;
    private Conversation mConversation;

    private Toolbar mAddressBar;
    private AtlasHistoricMessagesFetchLayout mHistoricFetchLayout;
    private AtlasMessagesRecyclerView mMessagesList;
    private AtlasTypingIndicator mTypingIndicator;
    private AtlasMessageComposer mMessageComposer;

    public MessagesListActivity()
    {
        super(R.layout.activity_messages_list, true);
    }

    private void setUiState(UiState state)
    {
//        if (mState == state) return;
//        mState = state;
//        switch (state) {
//            case ADDRESS:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
//                mHistoricFetchLayout.setVisibility(View.GONE);
//                mMessageComposer.setVisibility(View.GONE);
//                break;
//
//            case ADDRESS_COMPOSER:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.VISIBLE);
//                mHistoricFetchLayout.setVisibility(View.GONE);
//                mMessageComposer.setVisibility(View.VISIBLE);
//                break;
//
//            case ADDRESS_CONVERSATION_COMPOSER:
//                mAddressBar.setVisibility(View.VISIBLE);
//                mAddressBar.setSuggestionsVisibility(View.GONE);
//                mHistoricFetchLayout.setVisibility(View.VISIBLE);
//                mMessageComposer.setVisibility(View.VISIBLE);
//                break;
//
//            case CONVERSATION_COMPOSER:
//                mAddressBar.setVisibility(View.GONE);
//                mAddressBar.setSuggestionsVisibility(View.GONE);
//                mHistoricFetchLayout.setVisibility(View.VISIBLE);
//                mMessageComposer.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        if (mLayerAuthProvider.routeLogin(mLayerClient, mLayerAppId))
        {
            startActivity(new Intent(this, LayerLoginActivity.class));

            if (!isFinishing()) { finish(); }
            return;
        }

        mAddressBar = ((Toolbar) findViewById(R.id.toolbar));
        mHistoricFetchLayout = ((AtlasHistoricMessagesFetchLayout) findViewById(R.id.historic_sync_layout))
                .init(getLayerClient())
                .setHistoricMessagesPerFetch(MESSAGE_SYNC_AMOUNT);

        mMessagesList = ((AtlasMessagesRecyclerView) findViewById(R.id.messages_list))
                .init(getLayerClient(), getPicasso())
                .addCellFactories(
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(this, getLayerClient(), getPicasso()),
                        new LocationCellFactory(this, getPicasso()),
                        new SinglePartImageCellFactory(this, getLayerClient(), getPicasso()),
                        new GenericCellFactory()
                );

        mTypingIndicator = new AtlasTypingIndicator(this)
                .init(getLayerClient())
                .setTypingIndicatorFactory(new BubbleTypingIndicatorFactory())
                .setTypingActivityListener(new AtlasTypingIndicator.TypingActivityListener()
                {
                    @Override
                    public void onTypingActivityChange(
                            AtlasTypingIndicator typingIndicator,
                            boolean active
                    )
                    {
                        mMessagesList.setFooterView(active ? typingIndicator : null);
                    }
                });

        mMessageComposer = ((AtlasMessageComposer) findViewById(R.id.message_composer))
                .init(getLayerClient())
                .setTextSender(new TextSender())
                .setOnMessageEditTextFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus)
                    {
                        if (hasFocus)
                        {
                            setUiState(UiState.CONVERSATION_COMPOSER);
                            setTitle(true);
                        }
                    }
                });

        // Get or create Conversation from Intent extras
        Conversation conversation;
        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY))
            {
                Uri conversationId = intent.getParcelableExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY);
                conversation = getLayerClient().getConversation(conversationId);
                setConversation(conversation, conversation != null);
            }
        }
        else
        {
            throw new RuntimeException("This should never happen");
        }
    }

    @Override
    protected void onResume()
    {
        // Clear any notifications for this conversation
        PushNotificationReceiver.getNotifications(this).clear(mConversation);
        super.onResume();
        setTitle(mConversation != null);
    }

    @Override
    protected void onPause()
    {
        // Update the notification position to the latest seen
        PushNotificationReceiver.getNotifications(this).clear(mConversation);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mMessagesList != null)
        {
            mMessagesList.onDestroy();
        }
    }

    public void setTitle(boolean useConversation)
    {
        setTitle("Conversation between 2 people");
    }

    private void setConversation(Conversation conversation, boolean hideLauncher)
    {
        mConversation = conversation;
        mHistoricFetchLayout.setConversation(conversation);
        mMessagesList.setConversation(conversation);
        mTypingIndicator.setConversation(conversation);
        mMessageComposer.setConversation(conversation);

        // UI state
        if (conversation == null)
        {
            setUiState(UiState.ADDRESS);
            return;
        }

        if (hideLauncher)
        {
            setUiState(UiState.CONVERSATION_COMPOSER);
            return;
        }

        if (conversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.INVALID)
        {
            // New "temporary" conversation
            setUiState(UiState.ADDRESS_COMPOSER);
        }
        else
        {
            setUiState(UiState.ADDRESS_CONVERSATION_COMPOSER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mMessageComposer.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    )
    {
        mMessageComposer.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private enum UiState
    {
        ADDRESS,
        ADDRESS_COMPOSER,
        ADDRESS_CONVERSATION_COMPOSER,
        CONVERSATION_COMPOSER
    }


    private class IdentityChangeListener implements LayerChangeEventListener.Weak
    {
        @Override
        public void onChangeEvent(LayerChangeEvent layerChangeEvent)
        {
            // Don't need to update title if there is no conversation
            if (mConversation == null)
            {
                return;
            }

            for (LayerChange change : layerChangeEvent.getChanges())
            {
                if (change.getObjectType().equals(LayerObject.Type.IDENTITY))
                {
                    Identity identity = (Identity) change.getObject();
                    if (mConversation.getParticipants().contains(identity))
                    {
                        setTitle(true);
                    }
                }
            }
        }
    }
}
