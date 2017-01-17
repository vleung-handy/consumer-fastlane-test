package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.core.TestBaseApplication;

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
    private BookingRequest mMockBookingRequest;
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
        when(mMockBookingRequest.getTimeZone()).thenReturn("America/Los_Angeles");
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockBookingRequest);
        when(mMockQuote.getPricing(
                anyFloat(),
                anyInt(),
                anyInt()
        )).thenReturn(new float[]{0.0f, 0.0f});
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        mFragment = BookingAddressFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldLaunchBookingPaymentActivity() throws Exception
    {
        mFragment.mTextFullName.setText("John Doe");
        mFragment.mAutoCompleteFragment.mStreet.setText("123 Handy St");
        mFragment.mTextPhone.setText("1111111111");

        mFragment.mButtonNext.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingPaymentActivity.class.getName()));
    }
}
