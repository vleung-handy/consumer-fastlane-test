package com.handybook.handybook.library.ui.fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertEquals;

public class WebViewFragmentTest extends RobolectricGradleTestWrapper {

    private WebViewFragment mFragment;

    @Before
    public void setUp() throws Exception {
        mFragment = WebViewFragment.newInstance("https://handy.com", "Handy");
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldDisplayTitle() {
        Toolbar toolbar = (Toolbar) mFragment.getView().findViewById(R.id.toolbar);
        assertEquals("Handy", toolbar.getTitle().toString());
    }

    @Test
    public void shouldShowAndHideProgressBar() throws Exception {
        ProgressBar progressBar =
                (ProgressBar) mFragment.getView().findViewById(R.id.horizontal_progress_bar);
        mFragment.showProgressBar();
        assertEquals(View.VISIBLE, progressBar.getVisibility());
        mFragment.hideProgressBar();
        assertEquals(View.GONE, progressBar.getVisibility());
    }
}
