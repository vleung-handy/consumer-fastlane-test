package com.handybook.handybook.ui.fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.response.BookingEditExtrasInfoResponse;
import com.handybook.handybook.model.response.OptionPrice;
import com.handybook.handybook.model.response.PaidStatus;
import com.handybook.handybook.model.response.PriceInfo;
import com.handybook.handybook.testutil.AppAssertionUtils;
import com.handybook.handybook.viewmodel.BookingEditExtrasViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditExtrasFragmentTest extends RobolectricGradleTestWrapper
{
    //TODO: add a test to verify that the request reflects the options selected
    private BookingEditExtrasFragment mFragment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;

    @Mock
    private Booking.ExtraInfo mExtraInfo;

    @Mock
    private PaidStatus mPaidStatus;

    @Mock
    private PriceInfo mPriceInfo;

    @Mock
    BookingEditExtrasInfoResponse mBookingEditExtrasInfoResponse;

    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        when(mBooking.getId()).thenReturn("12345");
        mFragment = BookingEditExtrasFragment.newInstance(mBooking);

        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldRequestEditHoursViewModelOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor,
                instanceOf(HandyEvent.RequestEditBookingExtrasViewModel.class));
    }

    @Test
    public void shouldRequestEditHoursWhenSubmitButtonPressed() throws Exception
    {
        //mock the booking extras info
        ArrayList<Booking.ExtraInfo> extraInfos = new ArrayList<>();
        extraInfos.add(mExtraInfo);
        extraInfos.add(mExtraInfo);
        when(mBooking.getExtrasInfo()).thenReturn(extraInfos);

        //mock the price map for the edit extras info response
        when(mPriceInfo.getTotalDueFormatted()).thenReturn("$100");
        when(mBookingEditExtrasInfoResponse.getBaseHours()).thenReturn(2f);
        Map<String, PriceInfo> priceMap = new HashMap<>();
        priceMap.put("2", mPriceInfo);

        //mock the edit extras info response
        when(mBookingEditExtrasInfoResponse.getPriceTable()).thenReturn(priceMap);
        when(mBookingEditExtrasInfoResponse.getOptionsDisplayNames()).thenReturn(new String[]{
                "option1", "option2"
        });
        when(mBookingEditExtrasInfoResponse.getOptionsMachineNames()).thenReturn(new String[]{
                Booking.ExtrasMachineName.INSIDE_CABINETS, Booking.ExtrasMachineName.INSIDE_FRIDGE
        });
        when(mBookingEditExtrasInfoResponse.getOptionPrices()).thenReturn(new OptionPrice[]
                {new OptionPrice(), new OptionPrice()});
        when(mPaidStatus.getFutureBillDateFormatted()).thenReturn("Jan 1");
        when(mBookingEditExtrasInfoResponse.getPaidStatus()).thenReturn(mPaidStatus);

        BookingEditExtrasViewModel bookingEditExtrasViewModel = BookingEditExtrasViewModel.from
                (mBookingEditExtrasInfoResponse);
        //get the edit extras info response
        mFragment.onReceiveEditExtrasViewModelSuccess(
                new HandyEvent.ReceiveEditBookingExtrasViewModelSuccess(bookingEditExtrasViewModel));

        //press the save button
        mFragment.onSaveButtonPressed();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor,
                instanceOf(HandyEvent.RequestEditBookingExtras.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string.default_error_string);
        mFragment.onReceiveEditExtrasViewModelError(
                new HandyEvent.ReceiveEditBookingExtrasViewModelError(
                new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}