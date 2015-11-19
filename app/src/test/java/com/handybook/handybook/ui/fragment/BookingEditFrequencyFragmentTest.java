package com.handybook.handybook.ui.fragment;

import android.content.Context;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.response.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.viewmodel.BookingEditFrequencyViewModel;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditFrequencyFragmentTest extends RobolectricGradleTestWrapper
{

    private BookingEditFrequencyFragment mFragment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;

    @Mock
    BookingEditFrequencyViewModel mBookingEditFrequencyViewModel;

    @Mock
    BookingEditFrequencyInfoResponse mBookingEditFrequencyInfoResponse;

    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        Context applicationSpy = spy(RuntimeEnvironment.application);

        Mixpanel mockMixpanel = mock(Mixpanel.class);
        doNothing().when(mockMixpanel).trackEvent(any());

//        when(ShadowToast.makeText(any(Context.class), null, any(Integer.class))).thenReturn(
//                ShadowToast.makeText(applicationSpy, "", Toast.LENGTH_SHORT)
//        );

        when(mBooking.getId()).thenReturn("12345");
        mFragment = BookingEditFrequencyFragment.newInstance(mBooking);

        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldRequestEditFrequencyViewModelOnCreateView() throws Exception
    {
        assertBusPost(instanceOf(HandyEvent.RequestGetEditFrequencyViewModel.class));
    }

    @Test
    public void shouldRequestEditFrequencyWhenUpdateButtonPressed() throws Exception
    {
        when(mBookingEditFrequencyInfoResponse.getCurrentFrequency()).thenReturn(1);
        BookingEditFrequencyViewModel editFrequencyViewModel =
                BookingEditFrequencyViewModel.from(mBookingEditFrequencyInfoResponse);

        when(mBooking.getServiceMachineName()).thenReturn(Booking.SERVICE_CLEANING);

        mFragment.onReceiveBookingPricesForFrequenciesSuccess(new HandyEvent
                .ReceiveGetEditFrequencyViewModelSuccess(editFrequencyViewModel));
        mFragment.mSaveButton.performClick();
        assertBusPost(instanceOf(HandyEvent.RequestEditBookingFrequency.class));
    }

    //TODO: put in util
    private void assertBusPost(Matcher matcher)
    {
        verify(mFragment.bus, atLeastOnce()).post(mCaptor.capture());
        assertThat(mCaptor.getAllValues(), hasItem(matcher));
    }
}
