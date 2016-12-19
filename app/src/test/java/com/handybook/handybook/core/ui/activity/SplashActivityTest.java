package com.handybook.handybook.core.ui.activity;

import android.content.Intent;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ActivityController;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by sng on 11/10/16.
 */
public class SplashActivityTest extends RobolectricGradleTestWrapper
{
    @Inject
    UserManager mUserManager;
    @Mock
    private User mUserWithBooking;
    @Mock
    private User.Analytics mAnalytics;

    TestBaseApplication mTestBaseApplication;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        mTestBaseApplication = ((TestBaseApplication) ShadowApplication.getInstance()
                                                                       .getApplicationContext());
        mTestBaseApplication.inject(this);
    }

    /**
     * Tests first launch goes to OnBoarding Activity
     */
    @Test
    public void testFirstLaunch()
    {
        mTestBaseApplication.setNewlyLaunched(true);
        when(mUserManager.getCurrentUser()).thenReturn(null);

        ActivityController<SplashActivity> activityController = Robolectric.buildActivity(SplashActivity.class);
        activityController.create();
        Intent nextStartedActivity = shadowOf(activityController.get()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                   equalTo(OnboardActivity.class.getName()));
    }

    /**
     * Tests that a logged in user with bookings needs to go to the bookings page
     */
    @Test
    public void testLoggedInWithBookings()
    {
        when(mAnalytics.getUpcomingBookings()).thenReturn(3);
        when(mUserWithBooking.getAnalytics()).thenReturn(mAnalytics);
        when(mUserManager.getCurrentUser()).thenReturn(mUserWithBooking);

        mTestBaseApplication.setNewlyLaunched(true);

        ActivityController<SplashActivity> activityController = Robolectric.buildActivity(SplashActivity.class);
        activityController.create();
        Intent nextStartedActivity = shadowOf(activityController.get()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                   equalTo(BookingsActivity.class.getName()));
    }
}
