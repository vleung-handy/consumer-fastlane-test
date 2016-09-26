package com.handybook.handybook;


import android.content.ComponentName;
import android.view.View;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.module.referral.ui.ReferralActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NavMenuTest extends RobolectricGradleTestWrapper
{
    @Inject
    UserManager mUserManager;
    @Mock
    private User mMockUser;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
    }

    @Test
    public void freeCleaningsCTAShouldLaunchCorrectFragment() throws Exception
    {
        when(mUserManager.getCurrentUser()).thenReturn(mMockUser);

        ServiceCategoriesActivity initialActivity =
                Robolectric.setupActivity(ServiceCategoriesActivity.class);
        ShadowActivity shadowActivity = (ShadowActivity) ShadowExtractor.extract(initialActivity);
        assertNotNull(initialActivity);
        View drawerLayout = initialActivity.findViewById(R.id.drawer_layout);
        assertNotNull(drawerLayout);
        drawerLayout.performClick();

        View freeCleaningsHeader = initialActivity.findViewById(R.id.free_cleanings_nav_header);
        assertNotNull(freeCleaningsHeader);
        freeCleaningsHeader.performClick();
        assertEquals(
                shadowActivity.peekNextStartedActivityForResult().intent.getComponent(),
                new ComponentName(initialActivity, ReferralActivity.class)
        );
    }
}
