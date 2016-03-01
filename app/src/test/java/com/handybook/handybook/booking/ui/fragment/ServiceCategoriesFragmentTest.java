package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.google.common.collect.Lists;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.data.DataManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowPreferenceManager;
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
    DataManager mDataManager;
    
    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        mFragment = ServiceCategoriesFragment.newInstance();

        when(mMockService.getUniq()).thenReturn("service");
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(
                ShadowApplication.getInstance().getApplicationContext());
        sharedPreferences.edit().putBoolean("APP_ONBOARD_SHOWN", true).commit();

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldStartBookingFlowIfServiceHasNoNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Collections.<Service>emptyList());

        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));
        mFragment.mCategoryLayout.getChildAt(0).performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingLocationActivity.class.getName()));
    }

    @Test
    public void shouldLaunchServiceActivityFlowIfServiceHasNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Lists.newArrayList(mMockService));

        mFragment.onReceiveServicesSuccess(
                new BookingEvent.ReceiveServicesSuccess(Lists.newArrayList(mMockService)));
        mFragment.mCategoryLayout.getChildAt(0).performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(ServicesActivity.class.getName()));
    }
}
