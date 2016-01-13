package com.handybook.handybook.booking.bookingedit.ui.fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.testutil.AppAssertionUtils;

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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditAddressFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingEditAddressFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;
    @Mock
    private Booking.Address mBookingAddress;
    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mBookingAddress.getAddress1()).thenReturn("1 Test Drive");
        when(mBookingAddress.getZip()).thenReturn("10001");
        when(mBooking.getId()).thenReturn("12345");
        when(mBooking.getAddress()).thenReturn(mBookingAddress);
        mFragment = BookingEditAddressFragment.newInstance(mBooking);
        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void shouldNotRequestEditAddressWhenInputInvalidAndSubmitButtonPressed() throws Exception
    {
        reset(mFragment.bus);
        mFragment.mStreetAddressInputTextView1.setText("");
        mFragment.onNextButtonClick();
        verifyZeroInteractions(mFragment.bus);
    }

    @Test
    public void shouldRequestEditAddressWhenSubmitButtonPressed() throws Exception
    {
        mFragment.mStreetAddressInputTextView1.setText("2 Test Drive");
        mFragment.onNextButtonClick();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEvent.RequestEditBookingAddress.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveEditBookingAddressError(new BookingEvent.ReceiveEditBookingAddressError(new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
