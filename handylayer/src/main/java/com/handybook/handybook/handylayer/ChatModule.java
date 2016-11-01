package com.handybook.handybook.handylayer;

import android.content.Context;
import android.util.Log;

import com.handybook.handybook.handylayer.builtin.BaseActivity;
import com.handybook.handybook.handylayer.builtin.MessagesListActivity;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
                BaseActivity.class,
                MessagesListActivity.class,
                PushNotificationReceiver.class,
                PushNotificationReceiver.Notifications.class,
                HandyLayer.class
        })
public final class ChatModule
{

    private static final String TAG = ChatModule.class.getName();

    //JIA's project number
//    private static final String LAYER_APP_ID = "layer:///apps/staging/6178a72e-4e8d-11e6-aca9-940102005074";

//    //Handy's project number
    private static final String LAYER_APP_ID = "layer:///apps/staging/4ad25df6-9ab6-11e6-ae69-9937161a279e";

    HandyUser mUser;
    RestAdapter mRestAdapter;
    Bus mBus;
    Context mContext;

    public ChatModule(
            final RestAdapter restAdapter,
            final HandyUser user,
            final Bus bus,
            final Context context
    )
    {
        mUser = user;
        mRestAdapter = restAdapter;
        mBus = bus;
        mContext = context;
    }

    @Provides
    @Singleton
    @Named("layerAppId")
    public String getLayerAppId()
    {
        return LAYER_APP_ID;
    }

    @Provides
    @Singleton
    public LayerClient providesLayerClient(AuthenticationProvider authProvider)
    {
        Log.d(TAG, "providesLayerClient() called with: authProvider = [" + authProvider + "]");
        LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                    .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                    .autoDownloadMimeTypes(Arrays.asList(
                            TextCellFactory.MIME_TYPE,
                            ThreePartImageUtils.MIME_TYPE_INFO,
                            ThreePartImageUtils.MIME_TYPE_PREVIEW
                    ));

        options.useFirebaseCloudMessaging(true);
        LayerClient client = LayerClient.newInstance(mContext, LAYER_APP_ID, options);

        if (client != null)
        {
            Log.d(TAG, "providesLayerClient: registering auth provider " + authProvider.toString());
            client.registerAuthenticationListener(authProvider);
        }
        else
        {
            Log.d(TAG, "providesLayerClient: client is null");
        }

        return client;
    }

    @Provides
    @Singleton
    public AuthenticationProvider providesAuthenticationProvider(
            HandyService dataManager
    )
    {
        Log.d(TAG, "providesAuthenticationProvider() called");
        return new LayerAuthenticationProvider(mContext, dataManager);
    }

    @Provides
    @Singleton
    public LayerHelper providesLayerHelper(
            LayerClient layerClient,
            AuthenticationProvider authProvider
    )
    {
        Log.d(
                TAG,
                "providesLayerHelper() called with: layerClient = [" + layerClient + "], authProvider = [" + authProvider + "]"
        );
        return new LayerHelper(
                layerClient,
                authProvider,
                mBus,
                mUser,
                LAYER_APP_ID
        );
    }

    @Provides
    @Singleton
    public HandyService providesHandyService()
    {
        return mRestAdapter.create(HandyService.class);
    }

    @Provides
    @Singleton
    public Picasso providesPicasso(LayerClient layerClient)
    {
        Log.d(TAG, "providesPicasso() called with: layerClient = [" + layerClient + "]");
        return new Picasso.Builder(mContext)
                .addRequestHandler(new MessagePartRequestHandler(layerClient))
                .build();
    }
}
