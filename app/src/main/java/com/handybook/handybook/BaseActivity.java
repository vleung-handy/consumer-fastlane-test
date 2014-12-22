package com.handybook.handybook;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

abstract class BaseActivity extends FragmentActivity {

    private OnBackPressedListener onBackPressedListener;

    @Inject Mixpanel mixpanel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseApplication)this.getApplication()).inject(this);

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)
                && !BuildConfig.BUILD_TYPE.equals("debug")) {
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

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    interface OnBackPressedListener {
        void onBack();
    }
}
