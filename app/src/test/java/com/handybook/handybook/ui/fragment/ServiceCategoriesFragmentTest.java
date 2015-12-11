package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.common.collect.Lists;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingLocationActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.activity.ServicesActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
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

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        mFragment = ServiceCategoriesFragment.newInstance();

        when(mMockService.getUniq()).thenReturn("service");
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(
                ShadowApplication.getInstance().getApplicationContext());
        sharedPreferences.edit().putBoolean("APP_ONBOARD_SHOWN", true).commit();

        SupportFragmentTestUtil.startFragment(mFragment, ServiceCategoriesActivity.class);
    }

    @Test
    public void shouldStartBookingFlowIfServiceHasNoNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Collections.<Service>emptyList());

        verify(mFragment.dataManager, atLeastOnce())
                .getServices(any(DataManager.CacheResponse.class), mCallbackCaptor.capture());
        mCallbackCaptor.getValue().onSuccess(Lists.newArrayList(mMockService));
        mFragment.mCategoryLayout.getChildAt(0).performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingLocationActivity.class.getName()));
    }

    @Test
    public void shouldLaunchServiceActivityFlowIfServiceHasNestedCategories() throws Exception
    {
        when(mMockService.getServices()).thenReturn(Lists.newArrayList(mMockService));

        verify(mFragment.dataManager, atLeastOnce())
                .getServices(any(DataManager.CacheResponse.class), mCallbackCaptor.capture());
        mCallbackCaptor.getValue().onSuccess(Lists.newArrayList(mMockService));
        mFragment.mCategoryLayout.getChildAt(0).performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(ServicesActivity.class.getName()));
    }
}
