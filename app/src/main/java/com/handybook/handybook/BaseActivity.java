package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.newrelic.agent.android.NewRelic;

abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6")
                .start(this.getApplication());
    }
}
