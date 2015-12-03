package com.handybook.handybook.ui.fragment;

import android.content.Intent;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.ui.activity.BookingExtrasActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
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

public class BookingRecurrenceFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingRecurrenceFragment mFragment;

    @Mock
    private BookingTransaction mMockTransaction;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookingQuote mMockQuote;
    @Mock
    private BookingRequest mMockRequest;
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
        when(mMockQuote.getPeakPriceTable()).thenReturn(null);
        when(mMockRequest.getUniq()).thenReturn("home_cleaning");
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingRecurrenceFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void shouldLaunchBookingExtrasActivity() throws Exception
    {
        mFragment.nextButton.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingExtrasActivity.class.getName()));
    }
}
