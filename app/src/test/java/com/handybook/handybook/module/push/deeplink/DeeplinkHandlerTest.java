package com.handybook.handybook.module.push.deeplink;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SplashActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeeplinkHandlerTest extends RobolectricGradleTestWrapper
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
    public void handleDeeplinkReturnsFalseForNullDeeplink() throws Exception
    {
        Bundle arguments = new Bundle();
        assertFalse(DeeplinkHandler.handleDeeplink(null, arguments));
    }

    @Test
    public void handleDeeplinkReturnsFalseForUnknownDeeplink() throws Exception
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.DEEPLINK, "weird_deeplink");
        assertFalse(DeeplinkHandler.handleDeeplink(null, arguments));
    }

    @Test
    public void handleDeeplinkLaunchesBookingDetailActivityForBookingDetailDeeplink() throws Exception
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.DEEPLINK, DeeplinkConstants.DEEPLINK_DETAIL_BOOKING_MODAL);
        arguments.putString(BundleKeys.BOOKING_ID, "1234");
        assertTrue(DeeplinkHandler.handleDeeplink(mSpyContext, arguments));

        final Intent launchedIntent = mShadowApplication.getNextStartedActivity();
        assertThat(launchedIntent.getComponent().getClassName(),
                equalTo(BookingDetailActivity.class.getName()));
        assertThat(launchedIntent.getStringExtra(BundleKeys.BOOKING_ID), equalTo("1234"));
    }

    @Test
    public void handleDeeplinkLaunchesServiceCategoriesActivityForProRateModalDeeplink() throws Exception
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.DEEPLINK, DeeplinkConstants.DEEPLINK_PRO_RATE_MODAL);
        arguments.putString(BundleKeys.BOOKING_ID, "1234");
        arguments.putString(BundleKeys.BOOKING_RATE_PRO_NAME, "John");
        assertTrue(DeeplinkHandler.handleDeeplink(mSpyContext, arguments));

        final Intent launchedIntent = mShadowApplication.getNextStartedActivity();
        assertThat(launchedIntent.getComponent().getClassName(),
                equalTo(SplashActivity.class.getName()));
        assertThat(launchedIntent.getStringExtra(BundleKeys.BOOKING_ID), equalTo("1234"));
        assertThat(launchedIntent.getStringExtra(BundleKeys.BOOKING_RATE_PRO_NAME),
                equalTo("John"));
    }
}
