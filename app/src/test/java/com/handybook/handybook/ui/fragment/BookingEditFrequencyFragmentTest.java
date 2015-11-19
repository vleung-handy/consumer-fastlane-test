package com.handybook.handybook.ui.fragment;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.constant.BookingFrequency;
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
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditFrequencyFragmentTest extends RobolectricGradleTestWrapper
{

    private BookingEditFrequencyFragment mFragment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;

    @Mock
    BookingEditFrequencyInfoResponse mBookingEditFrequencyInfoResponse;

    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        Mixpanel mockMixpanel = mock(Mixpanel.class);
        doNothing().when(mockMixpanel).trackEvent(any());

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
    public void shouldRequestEditFrequencyWhenSubmitButtonPressed() throws Exception
    {
        when(mBookingEditFrequencyInfoResponse.getCurrentFrequency()).thenReturn(BookingFrequency.WEEKLY);
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
