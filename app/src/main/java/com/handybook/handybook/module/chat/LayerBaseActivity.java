package com.handybook.handybook.module.chat;


import android.support.v7.app.AppCompatActivity;

import com.layer.sdk.LayerClient;

import javax.inject.Inject;

/**
 * Created by jtse on 9/23/16.
 */
public class LayerBaseActivity extends AppCompatActivity {

    @Inject
    protected LayerClient mLayerClient;

    @Override
    protected void onResume() {
        super.onResume();
        if (mLayerClient == null) return;
        if (mLayerClient.isAuthenticated()) {
            mLayerClient.connect();
        } else {
            mLayerClient.authenticate();
        }
    }

}
