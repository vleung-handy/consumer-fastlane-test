package com.handybook.handybook;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.newrelic.agent.android.NewRelic;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

abstract class BaseActivity extends FragmentActivity {

    private OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b")
                    .start(this.getApplication());
        }
        else {
            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6")
                    .start(this.getApplication());
        }

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE) && !BuildConfig.BUILD_TYPE.equals("debug")) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected final void attachBaseContext(final Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) onBackPressedListener.onBack();
        else super.onBackPressed();
    }

    public void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    interface OnBackPressedListener {
        void onBack();
    }
}
