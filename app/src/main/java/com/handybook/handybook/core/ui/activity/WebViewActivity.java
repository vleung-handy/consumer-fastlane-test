package com.handybook.handybook.core.ui.activity;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;

public class WebViewActivity extends MenuDrawerActivity {

    @Override
    protected Fragment createFragment() {
        String url = getIntent().getStringExtra(BundleKeys.TARGET_URL);
        if (url == null) {
            throw new IllegalArgumentException("Url not found in WebViewActivity.");
        }
        return WebViewFragment.newInstance(url);
    }

    @Override
    protected String getNavItemTitle() { return null; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
