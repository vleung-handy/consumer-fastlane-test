package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.data.DataManager;

import org.junit.Before;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingLocationFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingLocationFragment mFragment;
    @Mock
    private BookingRequest mMockRequest;
    @Mock
    private BookingOptionsWrapper mBookingOptionsWrapper;
    @Inject
    BookingManager mBookingManager;
    @Inject
    DataManager mDataManager;
    @Captor
    private ArgumentCaptor<DataManager.Callback> mCallbackCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingLocationFragment.newInstance();

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldUpdateBookingRequestWithZipAndLaunchBookingOptionsActivity() throws Exception
    {
        mFragment.mZipCodeInputTextView.setText("10001");
        mFragment.mNextButton.performClick();
        verify(mDataManager).validateBookingZip(
                anyInt(),
                eq("10001"),
                anyString(),
                anyString(),
                mCallbackCaptor.capture()
        );

        ZipValidationResponse responseObject = new ZipValidationResponse();
        responseObject.setTimeZone("America/Los_Angeles");

        mCallbackCaptor.getValue().onSuccess(responseObject);
        verify(mMockRequest, atLeast(1)).setZipCode("10001");

        verify(mDataManager, atLeast(1)).getQuoteOptions(
                anyInt(),
                anyString(),
                mCallbackCaptor.capture()
        );
        when(mBookingOptionsWrapper.getBookingOptions())
                .thenReturn(Collections.<BookingOption>emptyList());
        mCallbackCaptor.getValue().onSuccess(mBookingOptionsWrapper);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingOptionsActivity.class.getName()));
    }
}
