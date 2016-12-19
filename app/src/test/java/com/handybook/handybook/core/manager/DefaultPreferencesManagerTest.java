package com.handybook.handybook.core.manager;

import android.text.TextUtils;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class DefaultPreferencesManagerTest extends RobolectricGradleTestWrapper
{
    DefaultPreferencesManager mPrefsManager;

    @Before
    public void setUp()
    {
        mPrefsManager = new DefaultPreferencesManager(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void installationIdShouldBeConsistent()
    {
        String installationId = mPrefsManager.getInstallationId();
        // the id should never be empty
        assertFalse(TextUtils.isEmpty(installationId));
        // the id should be the same once created
        assertEquals(installationId, mPrefsManager.getInstallationId());
    }
}
