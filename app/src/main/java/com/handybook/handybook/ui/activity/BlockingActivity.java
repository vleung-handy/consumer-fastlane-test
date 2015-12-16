package com.handybook.handybook.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Subscribe;

/**
 * Wrapper for the blocking fragment
 */
public class BlockingActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocking);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onBackPressed()
    {
        //Do nothing, by design (block the back button)
    }

    @Subscribe
    public void onStopBlockingEvent(final HandyEvent.StopBlockingAppEvent aVoid)
    {
        Intent restartAppIntent = new Intent(this, ServiceCategoriesActivity.class);
        restartAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        restartAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(restartAppIntent);
        finish();
    }
}
