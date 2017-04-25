package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.core.TestBaseApplication;

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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingRecurrenceFragmentTest extends RobolectricGradleTestWrapper {

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
    public void setUp() throws Exception {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mMockQuote.getPricing(anyFloat(), anyInt(), anyInt())).thenReturn(new float[]{
                0.0f,
                0.0f
        });
        when(mMockQuote.getPricing(anyString(), anyFloat(), anyInt(), anyInt()))
                .thenReturn(new float[]{0.0f, 0.0f});
        when(mMockQuote.getPeakPriceTable()).thenReturn(null);
        when(mMockRequest.getUniq()).thenReturn("home_cleaning");
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingRecurrenceFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldLaunchBookingExtrasActivity() throws Exception {
        mFragment.nextButton.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingExtrasActivity.class.getName())
        );
    }
}
