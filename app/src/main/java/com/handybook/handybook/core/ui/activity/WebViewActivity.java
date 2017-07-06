package com.handybook.handybook.core.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;

public class WebViewActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String url = getIntent().getStringExtra(BundleKeys.TARGET_URL);
        if (url == null) {
            throw new IllegalArgumentException("Url not found in WebViewActivity.");
        }
        return WebViewFragment.newInstance(url);
    }
}
