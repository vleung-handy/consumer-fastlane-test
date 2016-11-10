package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.common.collect.Lists;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.manager.DefaultPreferencesManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Collections;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class ServiceCategoriesFragmentTest extends RobolectricGradleTestWrapper
{
    private ServiceCategoriesFragment mFragment;
    @Mock
    private Service mMockService;
    @Captor
    private ArgumentCaptor<DataManager.Callback> mCallbackCaptor;

    @Inject
    UserManager mUserManager;
    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;

    TestBaseApplication mTestBaseApplication;

    @Mock
    private User mUserWithBooking;

    @Mock
    private User.Analytics mAnalytics;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        mTestBaseApplication = ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext());
        mTestBaseApplication.inject(this);
        mFragment = ServiceCategoriesFragment.newInstance();

        when(mMockService.getUniq()).thenReturn("service");

        mDefaultPreferencesManager.setBoolean(PrefsKey.APP_LAUNDRY_INFO_SHOWN, true);
    }

    /**
     * The user should only be redirected to the bookings page if he has more bookings, and
     * the app was newly launched. In this case, we set the app to not be "newly launched"
     * The user should remain in the services page
     */
    @Test
    public void testLoggedInOldLaunch()
    {
        when(mAnalytics.getUpcomingBookings()).thenReturn(0);
        when(mUserWithBooking.getAnalytics()).thenReturn(mAnalytics);
        when(mUserManager.getCurrentUser()).thenReturn(mUserWithBooking);
        mTestBaseApplication.setNewlyLaunched(false);

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        //should have a list of one item.
        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));

        assertThat("There should only be one item", mFragment.mAdapter.getItemCount() == 1);
    }

    /**
     * The user is logged in, but has no bookings. Should stay on the services page
     */
    @Test
    public void testLoggedInNoBookings()
    {
        when(mAnalytics.getUpcomingBookings()).thenReturn(0);
        when(mUserWithBooking.getAnalytics()).thenReturn(mAnalytics);
        when(mUserManager.getCurrentUser()).thenReturn(mUserWithBooking);
        mTestBaseApplication.setNewlyLaunched(true);

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        //should have a list of one item.
        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));

        assertThat("There should only be one item", mFragment.mAdapter.getItemCount() == 1);
    }

    /**
     * A user not logged in is supposed to see the main services screen onload
     */
    @Test
    public void testNotLoggedInUser()
    {
        when(mUserManager.getCurrentUser()).thenReturn(null);
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        //should have a list of one item.
        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));

        assertThat("There should only be one item", mFragment.mAdapter.getItemCount() == 1);
    }

    @Test
    @Ignore
    public void shouldStartBookingFlowIfServiceHasNoNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Collections.<Service>emptyList());

        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));
        mFragment.mRecyclerView.getChildAt(0).performClick();
        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingLocationActivity.class.getName()));
    }

    @Test
    @Ignore
    public void shouldLaunchServiceActivityFlowIfServiceHasNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Lists.newArrayList(mMockService));

        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));

        mFragment.mRecyclerView.getChildAt(0).performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(ServicesActivity.class.getName()));
    }
}
