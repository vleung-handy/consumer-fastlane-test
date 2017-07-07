package com.handybook.handybook.core;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public class TestActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new Fragment();
    }
}
