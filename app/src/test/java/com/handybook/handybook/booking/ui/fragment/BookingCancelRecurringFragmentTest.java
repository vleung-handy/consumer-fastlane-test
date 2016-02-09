package com.handybook.handybook.booking.ui.fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.testutil.AppAssertionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingCancelRecurringFragmentTest extends RobolectricGradleTestWrapper
{
    private CancelRecurringBookingFragment mFragment;

    @Mock
    private RecurringBooking mRecurringBooking1;
    @Mock
    private RecurringBooking mRecurringBooking2;
    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        mFragment = CancelRecurringBookingFragment.newInstance();

        Date mockDate = new Date();
        when(mRecurringBooking1.getRecurringId()).thenReturn(1);
        when(mRecurringBooking1.getNextRecurrenceDate()).thenReturn(mockDate);
        when(mRecurringBooking2.getRecurringId()).thenReturn(2);
        when(mRecurringBooking2.getNextRecurrenceDate()).thenReturn(mockDate);
        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldRequestRecurringBookingsOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestRecurringBookings.class));
    }

    @Test
    public void shouldRequestSendCancelEmailWhenSubmitButtonPressed() throws Exception
    {
        List<RecurringBooking> recurringBookingList = new ArrayList<>();
        recurringBookingList.add(mRecurringBooking1);
        recurringBookingList.add(mRecurringBooking2);

        mFragment.onReceiveRecurringBookingsSuccess(new BookingEvent.ReceiveRecurringBookingsSuccess(recurringBookingList));
        mFragment.onNextButtonClicked();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestSendCancelRecurringBookingEmail.class));
    }

    @Test
    public void shouldRequestSendCancelEmailWhenOnlyOneRecurringBooking() throws Exception
    {
        List<RecurringBooking> recurringBookingList = new ArrayList<>();
        recurringBookingList.add(mRecurringBooking1);
        mFragment.onReceiveRecurringBookingsSuccess(new BookingEvent.ReceiveRecurringBookingsSuccess(recurringBookingList));
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestSendCancelRecurringBookingEmail.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveSendCancelRecurringBookingEmailError(new BookingEvent.ReceiveSendCancelRecurringBookingEmailError(new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
