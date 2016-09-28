package com.handybook.handybook.module.chat;

import android.content.Context;
import android.util.Log;

import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.module.chat.builtin.BaseActivity;
import com.handybook.handybook.module.chat.builtin.MessagesListActivity;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                LayerLoginActivity.class,
                BaseActivity.class,
                MessagesListActivity.class,
                PushNotificationReceiver.class,
                PushNotificationReceiver.Notifications.class,
                InjectedFragment.class
        })
public final class ChatModule
{

    private static final String TAG = ChatModule.class.getName();

    //JIA's project number
    private static final String LAYER_APP_ID = "layer:///apps/staging/6178a72e-4e8d-11e6-aca9-940102005074";

    //JIA's GCM number
    private static final String GCM_SENDER_ID = "606008763304";

    @Provides
    @Singleton
    @Named("baseUrl")
    public String baseUrl() {
        return "https://s-handy.handy-internal.com/api/v3/";
    }

    @Provides @Singleton @Named("layerAppId")
    public String getLayerAppId() {
        return LAYER_APP_ID;
    }

    @Provides
    @Singleton
    @Named("layerGcmId")
    public String getLayerGcmId()
    {
        return GCM_SENDER_ID;
    }

    @Provides @Singleton
    public LayerClient providesLayerClient(AuthenticationProvider authProvider, Context context)
    {
        Log.d(TAG, "providesLayerClient() called with: authProvider = [" + authProvider + "]");
        LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                    .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                    .autoDownloadMimeTypes(Arrays.asList(
                            TextCellFactory.MIME_TYPE,
                            ThreePartImageUtils.MIME_TYPE_INFO,
                            ThreePartImageUtils.MIME_TYPE_PREVIEW));

        options.googleCloudMessagingSenderId(GCM_SENDER_ID);
        LayerClient client = LayerClient.newInstance(context, LAYER_APP_ID, options);

        if (client != null) {
            Log.d(TAG, "providesLayerClient: registering auth provider " + authProvider.toString());
            client.registerAuthenticationListener(authProvider);
        } else {
            Log.d(TAG, "providesLayerClient: client is null");
        }

        return client;
    }

    @Provides @Singleton
    public AuthenticationProvider providesAuthenticationProvider(Context context)
    {
        Log.d(TAG, "providesAuthenticationProvider() called");
        return new LayerAuthenticationProvider(context);
    }

    @Provides @Singleton
    public LayerHelper providesLayerHelper(LayerClient layerClient, AuthenticationProvider authProvider) {
        Log.d(
                TAG,
                "providesLayerHelper() called with: layerClient = [" + layerClient + "], authProvider = [" + authProvider + "]"
        );
        return new LayerHelper(layerClient, authProvider, LAYER_APP_ID);
    }

    @Provides
    @Singleton
    public Picasso providesPicasso(LayerClient layerClient, Context context)
    {
        Log.d(TAG, "providesPicasso() called with: layerClient = [" + layerClient + "]");
        return new Picasso.Builder(context)
                .addRequestHandler(new MessagePartRequestHandler(layerClient))
                .build();
    }
}
