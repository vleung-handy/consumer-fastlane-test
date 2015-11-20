package com.handybook.handybook.ui.fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.response.BookingEditHoursInfoResponse;
import com.handybook.handybook.testutil.AppAssertionUtils;
import com.handybook.handybook.viewmodel.BookingEditHoursViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditHoursFragmentTest extends RobolectricGradleTestWrapper
{
    //TODO: add a test to verify that the request reflects the options selected
    private BookingEditHoursFragment mFragment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;

    @Mock
    BookingEditHoursViewModel mBookingEditHoursViewModel;

    @Mock
    BookingEditHoursInfoResponse mBookingEditHoursInfoResponse;

    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        Mixpanel mockMixpanel = mock(Mixpanel.class);
        doNothing().when(mockMixpanel).trackEvent(any());

        when(mBooking.getId()).thenReturn("12345");
        mFragment = BookingEditHoursFragment.newInstance(mBooking);

        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldRequestEditHoursViewModelOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(HandyEvent.RequestEditHoursInfoViewModel.class));
    }

    @Test
    public void shouldRequestEditHoursWhenSubmitButtonPressed() throws Exception
    {
        when(mBookingEditHoursViewModel.getSelectableHoursArray()).thenReturn(new String[]{
                "1.0", "2.0", "3.0"
        });
        when(mBookingEditHoursViewModel.getBaseHours()).thenReturn(1f);

        mFragment.onReceiveEditHoursInfoSuccess(new HandyEvent
                .ReceiveEditHoursInfoViewModelSuccess(mBookingEditHoursViewModel));
        mFragment.onSaveButtonPressed();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(HandyEvent.RequestEditHours.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveEditHoursInfoError(new HandyEvent
                .ReceiveEditHoursInfoViewModelError(new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
