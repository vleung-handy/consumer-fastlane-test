package com.handybook.handybook.push.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.constant.BundleKeys;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushActionHandlerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private PackageManager mMockPackageManager;
    private Context mSpyContext;
    private ShadowApplication mShadowApplication;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mMockPackageManager.resolveActivity(any(Intent.class), eq(0)))
                .thenReturn(mock(ResolveInfo.class));
        mShadowApplication = ShadowApplication.getInstance();
        mSpyContext = spy(mShadowApplication.getApplicationContext());
        when(mSpyContext.getPackageManager()).thenReturn(mMockPackageManager);
    }

    @Test
    public void handleActionReturnsFalseForUnknownAction() throws Exception
    {
        assertFalse(PushActionHandler.handleAction(null, "weird_action", null));
    }

    @Test
    public void handleActionLaunchesCallIntentForCallAction() throws Exception
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_PHONE, "1112223333");
        assertTrue(PushActionHandler.handleAction(mSpyContext,
                PushActionConstants.ACTION_CONTACT_CALL, arguments));

        final Intent launchedIntent = mShadowApplication.getNextStartedActivity();
        assertThat(launchedIntent.getAction(), equalTo(Intent.ACTION_VIEW));
        assertThat(launchedIntent.getDataString(), equalTo("tel:1112223333"));
    }

    @Test
    public void handleActionLaunchesTextIntentForTextAction() throws Exception
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_PHONE, "1112223333");
        assertTrue(PushActionHandler.handleAction(mSpyContext,
                PushActionConstants.ACTION_CONTACT_TEXT, arguments));

        final Intent launchedIntent = mShadowApplication.getNextStartedActivity();
        assertThat(launchedIntent.getAction(), equalTo(Intent.ACTION_VIEW));
        assertThat(launchedIntent.getDataString(), equalTo("sms:1112223333"));
    }
}
