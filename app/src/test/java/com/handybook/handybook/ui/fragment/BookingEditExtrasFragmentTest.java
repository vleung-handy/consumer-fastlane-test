package com.handybook.handybook.ui.fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.response.BookingEditExtrasInfoResponse;
import com.handybook.handybook.testutil.AppAssertionUtils;

import org.junit.Before;
import org.junit.Ignore;
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

public class BookingEditExtrasFragmentTest extends RobolectricGradleTestWrapper
{
    //TODO: add a test to verify that the request reflects the options selected
    private BookingEditExtrasFragment mFragment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;

    @Mock
    BookingEditExtrasInfoResponse mBookingEditExtrasInfoResponse;

    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        Mixpanel mockMixpanel = mock(Mixpanel.class);
        doNothing().when(mockMixpanel).trackEvent(any());

        when(mBooking.getId()).thenReturn("12345");
        mFragment = BookingEditExtrasFragment.newInstance(mBooking);

        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldRequestEditHoursViewModelOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(HandyEvent.RequestEditExtrasInfo
                .class));
    }

    @Ignore
    @Test
    public void shouldRequestEditHoursWhenSubmitButtonPressed() throws Exception
    {
        //TODO: fix
//        when(mBookingEditExtrasInfoResponse.getOptionPrices()).thenReturn(new OptionPrice[]
//                {new OptionPrice()});
        mFragment.onReceiveServicesExtrasOptionsSuccess(new HandyEvent.ReceiveEditExtrasInfoSuccess(mBookingEditExtrasInfoResponse));
        mFragment.onSaveButtonPressed();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(HandyEvent.RequestEditExtras
                .class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveServicesExtrasOptionsError(new HandyEvent.ReceiveEditExtrasInfoError(
                new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
