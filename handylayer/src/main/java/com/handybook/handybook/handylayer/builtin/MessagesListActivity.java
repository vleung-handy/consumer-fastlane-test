package com.handybook.handybook.handylayer.builtin;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.handybook.handybook.handylayer.PushNotificationReceiver;
import com.handybook.handybook.handylayer.R;
import com.handybook.handybook.handylayer.Util;
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
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

/**
 * TODO: JIA: setup fabric in this library
 */
public class MessagesListActivity extends BaseActivity
{
    private static final int MESSAGE_SYNC_AMOUNT = 20;
    private static final String TAG = MessagesListActivity.class.getName();

    private Conversation mConversation;

    private Toolbar mToolbar;
    private AtlasHistoricMessagesFetchLayout mHistoricFetchLayout;
    private AtlasMessagesRecyclerView mMessagesList;
    private AtlasTypingIndicator mTypingIndicator;
    private AtlasMessageComposer mMessageComposer;

    public MessagesListActivity()
    {
        super(R.layout.activity_messages_list, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (mLayerAuthProvider.routeLogin(mLayerClient, mLayerAppId))
        {
            throw new RuntimeException("Needs to route login, but not sure what to do here");
        }

        setSupportActionBar(mToolbar);

        mToolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        mHistoricFetchLayout = ((AtlasHistoricMessagesFetchLayout) findViewById(R.id.messages_historic_sync_layout))
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

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/CircularStd-Book.otf");
        mMessagesList.setTextTypeface(type, type);


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
                        Log.d(TAG, "onTypingActivityChange: ");
                        mMessagesList.setFooterView(active ? typingIndicator : null);
                    }
                });

        mMessageComposer = ((AtlasMessageComposer) findViewById(R.id.messages_composer))
                .init(getLayerClient())
                .setTextSender(new TextSender());


        // Get or create Conversation from Intent extras
        Conversation conversation;
        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY))
            {
                Uri conversationId = intent.getParcelableExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY);
                conversation = getLayerClient().getConversation(conversationId);
                setConversation(conversation);
            }
        }
        else
        {
            throw new RuntimeException("This should never happen");
        }

        //TODO: JIA: should test this method on a cold start, coming from a push notification
    }

    @Override
    protected void onResume()
    {
        // Clear any notifications for this conversation
        PushNotificationReceiver.getNotifications(this).clear(mConversation);
        super.onResume();
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


    private void setConversation(Conversation conversation)
    {
        mConversation = conversation;
        mHistoricFetchLayout.setConversation(conversation);
        mMessagesList.setConversation(conversation);
        mTypingIndicator.setConversation(conversation);
        mMessageComposer.setConversation(conversation);
        Identity id = Util.getOpposingParticipant(conversation);

        if (id != null)
        {
            mToolbar.setTitle(id.getDisplayName());
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

}
