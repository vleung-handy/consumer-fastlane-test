package com.handybook.handybook.handylayer;

import android.net.Uri;
import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;
import com.squareup.otto.Bus;

/**
 * Created by jtse on 9/22/16.
 */
public class LayerHelper
{

    private static final String TAG = LayerHelper.class.getName();

    private LayerClient mLayerClient;
    private AuthenticationProvider mLayerAuthProvider;
    private String mLayerAppId;
    private RecyclerViewController<Conversation> mQueryController;
    private boolean mConversationsInitialized = false;
    private Bus mBus;
    private long mCurrentUnreadMessages = 0;
    private HandyUser mUser;

    public LayerHelper(
            final LayerClient layerClient,
            final AuthenticationProvider layerAuthProvider,
            final Bus bus,
            final HandyUser user,
            final String appId
    )
    {
        mLayerClient = layerClient;
        mLayerAuthProvider = layerAuthProvider;
        mBus = bus;
        mLayerAppId = appId;
        mUser = mUser;
        mLayerClient.registerEventListener(new LayerChangeEventListener()
        {
            @Override
            public void onChangeEvent(final LayerChangeEvent layerChangeEvent)
            {
                if (getUnreadMessageCount() != mCurrentUnreadMessages)
                {
                    mCurrentUnreadMessages = getUnreadMessageCount();
                    mBus.post(new LayerEvent(mCurrentUnreadMessages));
                }
            }
        });
    }

    public LayerClient getLayerClient()
    {
        return mLayerClient;
    }

    /**
     * Authenticates with the AuthenticationProvider and Layer, returning asynchronous results to
     * the provided callback.
     *
     * @param credentials Credentials associated with the current AuthenticationProvider.
     * @param callback    Callback to receive authentication results.
     */
    @SuppressWarnings("unchecked")
    public void authenticate(Object credentials, AuthenticationProvider.Callback callback)
    {
        if (mLayerClient == null) { return; }
        if (mLayerAppId == null) { return; }
        mLayerAuthProvider
                .setCredentials(credentials)
                .setCallback(callback);

        Log.d(
                TAG,
                "authenticate: authenticating with client:" + mLayerClient.toString() + " auth provider:"
                        + mLayerAuthProvider.toString()
        );

        mLayerClient.authenticate();
    }

    /**
     * Deauthenticates with Layer and clears cached AuthenticationProvider credentials.
     *
     * @param callback Callback to receive deauthentication success and failure.
     */
    public void deauthenticate(final com.layer.atlas.util.Util.DeauthenticationCallback callback)
    {
        Log.d(TAG, "deauthenticate: with client:" + mLayerClient.toString() + "  authProvider: ");
        com.layer.atlas.util.Util.deauthenticate(
                mLayerClient,
                new com.layer.atlas.util.Util.DeauthenticationCallback()
                {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onDeauthenticationSuccess(
                            LayerClient client
                    )
                    {
                        Log.d(TAG, "onDeauthenticationSuccess: ");
                        mLayerAuthProvider.setCredentials(null);
                        callback.onDeauthenticationSuccess(client);
                    }

                    @Override
                    public void onDeauthenticationFailed(
                            LayerClient client,
                            String reason
                    )
                    {
                        Log.d(TAG, "onDeauthenticationFailed: ");
                        callback.onDeauthenticationFailed(
                                client,
                                reason
                        );
                    }
                }
        );
    }

    /**
     * intializes the logged in user's session and warms up the conversation caches. Do this only if
     * the user is logged into the handy app.
     * <p>
     * //TODO: JIA: potentially investigate the issue of a different user logging into the app, then
     * we need to de-auth layer and reauth?
     */
    public void initLayer()
    {
        if ((mLayerClient != null) && mLayerClient.isAuthenticated())
        {
            Log.d(TAG, "initLayer: Already logged in");
            initConversations();
        }
        else
        {
            Log.d(TAG, "initLayer: Not logged in");

            authenticate(
                    new LayerAuthenticationProvider.Credentials(
                            mLayerAppId,
                            mUser.getUserName(),
                            mUser.getId()
                    ),
                    new AuthenticationProvider.Callback()
                    {
                        @Override
                        public void onSuccess(AuthenticationProvider provider, String userId)
                        {
                            Log.d(TAG, "AUTH onSuccess: ");
                            initConversations();
                        }

                        @Override
                        public void onError(AuthenticationProvider provider, final String error)
                        {
                            Log.e(
                                    TAG,
                                    "Failed to authenticate as `" + mUser.getUserName() + "`: " + error
                            );
                            throw new RuntimeException(
                                    "Issue logging onto layer. This should never happen.");
                        }
                    }
            );
        }
    }

//    private void waitForContent()
//    {
//        Log.d(TAG, "waitForContent: ");
//        mLayerClient.waitForContent(
//                Uri.parse(mBooking.getConversationId()),
//                new LayerClient.ContentAvailableCallback()
//                {
//                    @Override
//                    public void onContentAvailable(
//                            final LayerClient layerClient, @NonNull final Queryable queryable
//                    )
//                    {
//                        Log.d(
//                                TAG,
//                                "onContentAvailable() called with: layerClient = [" + layerClient + "], queryable = [" + queryable + "]"
//                        );
//
//                        syncConversation();
//                    }
//
//                    @Override
//                    public void onContentFailed(
//                            final LayerClient layerClient,
//                            final Uri uri,
//                            final Exception e
//                    )
//                    {
//                        Log.d(
//                                TAG,
//                                "onContentFailed() called with: layerClient = [" + layerClient + "], uri = [" + uri + "], e = [" + e + "]"
//                        );
//                    }
//                }
//        );
//    }

    /**
     * We only come here if the layer auth is successful. Sync the conversation so that it's ready
     * to display on chat, if needed.
     */
    private void syncConversation(String conversationId)
    {
        Log.d(TAG, "syncConversation() called for conversation: " + conversationId);
        Conversation conversation = mLayerClient.getConversation(Uri.parse(conversationId));

        if (conversation != null)
        {
            Log.d(TAG, "syncConversation: conversation is not null");
            Conversation.HistoricSyncStatus status = conversation.getHistoricSyncStatus();

            if (status == Conversation.HistoricSyncStatus.MORE_AVAILABLE)
            {
                Log.d(TAG, "syncConversation: There is more messages available for synching");
                conversation.syncMoreHistoricMessages(20);
            }
        }
        else
        {
            //the conversations haven't been initialized yet. Initialize them.
            Log.d(TAG, "syncConversation: conversation is NULL");

            throw new RuntimeException("Conversation " + conversationId + " was not found, but it should've been initialized before getting to this point");
        }

    }

    /**
     * This initializes the layer client cache to have conversations. On a cold start, the cache is
     * empty, and LayerClient.getConversations can be null;
     */
    private void initConversations()
    {
        mConversationsInitialized = false;
        Log.d(TAG, "initConversations: ");

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

        mQueryController = mLayerClient.newRecyclerViewController(
                query,
                null,
                new SimpleRecyclerCallback()
                {
                    @Override
                    public void onQueryDataSetChanged(final RecyclerViewController recyclerViewController)
                    {
                        Log.d(
                                TAG,
                                "onQueryDataSetChanged() called with: recyclerViewController = [" + recyclerViewController + "]"
                        );
                        mConversationsInitialized = true;

                    }

                    @Override
                    public void onQueryItemChanged(
                            final RecyclerViewController recyclerViewController,
                            final int i
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
                        );
                        mConversationsInitialized = true;
                    }

                    @Override
                    public void onQueryItemRangeChanged(
                            final RecyclerViewController recyclerViewController,
                            final int i,
                            final int i1
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemRangeChanged() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
                        );
                        mConversationsInitialized = true;
                    }

                    @Override
                    public void onQueryItemInserted(
                            final RecyclerViewController recyclerViewController,
                            final int i
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
                        );
                        mConversationsInitialized = true;
                    }

                    @Override
                    public void onQueryItemRangeInserted(
                            final RecyclerViewController recyclerViewController,
                            final int i,
                            final int i1
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemRangeInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "], i1 = [" + i1 + "]"
                        );
                        mConversationsInitialized = true;
                    }
                }
        );

        Log.d(TAG, "initConversations: executing query, look out for events coming back");
        mQueryController.execute();
    }

    public boolean isConversationsInitialized()
    {
        return mConversationsInitialized;
    }

    /**
     * Fetches the count of all unread messages for the authenticated user
     *
     * @return
     */
    public long getUnreadMessageCount()
    {
        Query query = Query.builder(Message.class)
                           .predicate(new Predicate(
                                   Message.Property.IS_UNREAD,
                                   Predicate.Operator.EQUAL_TO,
                                   true
                           ))
                           .build();

        Long result = mLayerClient.executeQueryForCount(query);
        return result == null ? 0 : result;
    }
}
