package com.handybook.handybook.booking.ui.fragment;

import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
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

public class CancelRecurringBookingSelectionFragmentTest extends RobolectricGradleTestWrapper
{
    private CancelRecurringBookingSelectionFragment mFragment;

    @Mock
    private RecurringBooking mRecurringBooking1;
    @Mock
    private RecurringBooking mRecurringBooking2;
    @Mock
    private Configuration mConfiguration;
    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        mFragment = CancelRecurringBookingSelectionFragment.newInstance();

        Date mockDate = new Date();
        when(mRecurringBooking1.getId()).thenReturn(1);
        when(mRecurringBooking1.getNextBookingDate()).thenReturn(mockDate);
        when(mRecurringBooking2.getId()).thenReturn(2);
        when(mRecurringBooking2.getNextBookingDate()).thenReturn(mockDate);
        when(mConfiguration.shouldUseCancelRecurringWebview()).thenReturn(false);
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldRequestRecurringBookingsOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestRecurringBookings.class));
    }

    @Test
    public void shouldRequestConfigurationOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(ConfigurationEvent.RequestConfiguration.class));
    }

    @Test
    public void shouldRequestSendCancelEmailWhenSubmitButtonPressed() throws Exception
    {
        List<RecurringBooking> recurringBookingList = new ArrayList<>();
        recurringBookingList.add(mRecurringBooking1);
        recurringBookingList.add(mRecurringBooking2);

        mFragment.onReceiveRecurringBookingsSuccess(new BookingEvent.ReceiveRecurringBookingsSuccess(recurringBookingList));
        mFragment.onReceiveConfigurationSuccess(new ConfigurationEvent.ReceiveConfigurationSuccess(mConfiguration));
        mFragment.onNextButtonClicked();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestSendCancelRecurringBookingEmail.class));
    }

    @Test
    public void shouldRequestSendCancelEmailWhenOnlyOneRecurringBooking() throws Exception
    {
        List<RecurringBooking> recurringBookingList = new ArrayList<>();
        recurringBookingList.add(mRecurringBooking1);
        mFragment.onReceiveRecurringBookingsSuccess(new BookingEvent.ReceiveRecurringBookingsSuccess(recurringBookingList));
        mFragment.onReceiveConfigurationSuccess(new ConfigurationEvent.ReceiveConfigurationSuccess(mConfiguration));
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestSendCancelRecurringBookingEmail.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveSendCancelRecurringBookingEmailError(new BookingEvent.ReceiveSendCancelRecurringBookingEmailError(new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        mFragment.onReceiveConfigurationSuccess(new ConfigurationEvent.ReceiveConfigurationSuccess(mConfiguration));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
