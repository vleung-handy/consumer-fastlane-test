package com.handybook.handybook.module.chat;

import android.app.Application;
import android.util.Log;

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
                LayerChatActivity.class,
                LayerConversationActivity.class,
                LayerLoginActivity.class,
                BaseActivity.class,
                MessagesListActivity.class
        })
public final class ChatModule
{

    private static final String TAG = ChatModule.class.getName();

    private Application mApplication;
    private static final String LAYER_APP_ID = "layer:///apps/staging/6178a72e-4e8d-11e6-aca9-940102005074";

    public ChatModule(Application application) {
        mApplication = application;
    }

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

    @Provides @Singleton
    public LayerClient providesLayerClient(AuthenticationProvider authProvider) {
        LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                    .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                    .autoDownloadMimeTypes(Arrays.asList(
                            TextCellFactory.MIME_TYPE,
                            ThreePartImageUtils.MIME_TYPE_INFO,
                            ThreePartImageUtils.MIME_TYPE_PREVIEW));

        LayerClient client = LayerClient.newInstance(mApplication, LAYER_APP_ID, options);

        if (client != null) {
            Log.d(TAG, "providesLayerClient: registering auth provider " + authProvider.toString());
            client.registerAuthenticationListener(authProvider);
        } else {
            Log.d(TAG, "providesLayerClient: client is null");
        }

        return client;
    }

    @Provides @Singleton
    public AuthenticationProvider providesAuthenticationProvider() {
        return new LayerAuthenticationProvider(mApplication);
    }

    @Provides @Singleton
    public LayerHelper providesLayerHelper(LayerClient layerClient, AuthenticationProvider authProvider) {
        return new LayerHelper(layerClient, authProvider, LAYER_APP_ID);
    }

    @Provides
    @Singleton
    public Picasso providesPicasso(LayerClient layerClient)
    {
        return new Picasso.Builder(mApplication)
                .addRequestHandler(new MessagePartRequestHandler(layerClient))
                .build();
    }
}
