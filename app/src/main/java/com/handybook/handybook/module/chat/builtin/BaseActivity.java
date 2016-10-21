package com.handybook.handybook.module.chat.builtin;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.module.chat.AuthenticationProvider;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Named;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity
{
    private final int mLayoutResId;
    private final boolean mMenuBackEnabled;

    @Inject
    LayerClient mLayerClient;

    @Inject
    AuthenticationProvider mLayerAuthProvider;

    @Inject
    Picasso mPicasso;

    @Inject
    @Named("layerAppId")
    String mLayerAppId;

    public BaseActivity(int layoutResId, boolean menuBackEnabled)
    {
        mLayoutResId = layoutResId;
        mMenuBackEnabled = menuBackEnabled;
    }

    public BaseActivity()
    {
        mLayoutResId = 0;
        mMenuBackEnabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (mLayoutResId != 0)
        {
            setContentView(mLayoutResId);

            ((BaseApplication) getApplication()).inject(this);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) { return; }
            if (mMenuBackEnabled) { actionBar.setDisplayHomeAsUpEnabled(true); }
        }
    }

    @Override
    public void setTitle(CharSequence title)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
        {
            super.setTitle(title);
        }
        else
        {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
        {
            super.setTitle(titleId);
        }
        else
        {
            actionBar.setTitle(titleId);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mLayerClient == null) { return; }
        if (mLayerClient.isAuthenticated())
        {
            mLayerClient.connect();
        }
        else
        {
            mLayerClient.authenticate();
        }
    }

    @Override
    protected final void attachBaseContext(final Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected LayerClient getLayerClient()
    {
        return mLayerClient;
    }

    public Picasso getPicasso()
    {
        return mPicasso;
    }

}
