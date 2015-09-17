package com.handybook.handybook.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handybook.handybook.R;

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
    public void onBackPressed()
    {
        //Do nothing, by design (block the back button)
    }
}
