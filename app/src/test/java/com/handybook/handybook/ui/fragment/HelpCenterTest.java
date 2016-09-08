package com.handybook.handybook.ui.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.configuration.model.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.ActivityController;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HelpCenterTest extends RobolectricGradleTestWrapper
{
    @Inject
    ConfigurationManager mConfigurationManager;
    @Mock
    Configuration mConfiguration;

    private HelpFragment mFragment;
    private ActivityController<HelpActivity> mActivityController;

    private static final String HANDY_TEST_URL = "https://www.handy.com";

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mConfigurationManager.getCachedConfiguration()).thenReturn(mConfiguration);
    }

    @Test
    public void shouldShowHelpCenterWebViewOption() throws Exception
    {
        mFragment = HelpFragment.newInstance(HANDY_TEST_URL);
        SupportFragmentTestUtil.startFragment(mFragment, HelpActivity.class);

        assertNotNull(mFragment.getView());
        assertNotNull(mFragment.getView().findViewById(R.id.help_center_layout));
        mFragment.getView().findViewById(R.id.help_center_layout);
    }

    @Test
    public void shouldLaunchHelpWebviewWhenOptionClicked()
    {
        mFragment = HelpFragment.newInstance(HANDY_TEST_URL);
        SupportFragmentTestUtil.startFragment(mFragment, HelpActivity.class);

        assertNotNull(mFragment.getView());
        mFragment.getView().findViewById(R.id.help_center_layout).performClick();
        Fragment currentFragment = mFragment.getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (!(currentFragment instanceof HelpWebViewFragment))
        { fail(); }
    }

    @Test
    public void shouldLaunchHelpWebviewWhenConfigParamIsOff()
    {
        when(mConfiguration.isNativeHelpCenterEnabled()).thenReturn(false);

        mActivityController = Robolectric.buildActivity(HelpActivity.class, new Intent());
        mActivityController.create().resume();
        HelpActivity helpActivity = mActivityController.get();
        Fragment currentFragment =
                helpActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(currentFragment instanceof HelpWebViewFragment))
        { fail(); }
    }

    @Test
    public void shouldLaunchNativeHelpCenterWhenConfigParamIsOn()
    {
        when(mConfiguration.isNativeHelpCenterEnabled()).thenReturn(true);

        mActivityController = Robolectric.buildActivity(HelpActivity.class, new Intent());
        mActivityController.create().resume();
        HelpActivity helpActivity = mActivityController.get();
        Fragment currentFragment =
                helpActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(currentFragment instanceof HelpFragment))
        { fail(); }
    }
}
