package com.handybook.handybook.ui.fragment;

import android.content.Intent;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.ui.activity.BookingPaymentActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingAddressFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingAddressFragment mFragment;

    @Mock
    private BookingTransaction mMockTransaction;
    @Mock
    private BookingQuote mMockQuote;
    @Inject
    BookingManager mBookingManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mMockQuote.getPricing(anyFloat(), anyInt())).thenReturn(new float[]{0.0f, 0.0f});
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        mFragment = BookingAddressFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void shouldLaunchBookingPaymentActivity() throws Exception
    {
        mFragment.fullNameText.setText("John Doe");
        mFragment.streetAddrText.setText("123 Handy St");
        mFragment.phoneText.setText("1111111111");

        mFragment.nextButton.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingPaymentActivity.class.getName()));
    }
}
