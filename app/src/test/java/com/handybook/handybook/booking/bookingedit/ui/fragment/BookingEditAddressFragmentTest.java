package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.data.DataManager;
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

public class BookingEditAddressFragmentTest extends RobolectricGradleTestWrapper {

    private static final String BOOKING_ID = "12345";

    private static final String ADDRESS_STREET = "1 Test Drive";
    private static final String ADDRESS_ZIP = "10001";

    private BookingEditAddressFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;
    @Mock
    private Booking.Address mBookingAddress;
    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mBookingAddress.getAddress1()).thenReturn(ADDRESS_STREET);
        when(mBookingAddress.getZip()).thenReturn(ADDRESS_ZIP);
        when(mBooking.getId()).thenReturn(BOOKING_ID);
        when(mBooking.getAddress()).thenReturn(mBookingAddress);
        mFragment = BookingEditAddressFragment.newInstance(mBooking);
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldNotRequestEditAddressWhenInputInvalidAndSubmitButtonPressed() throws
            Exception {
        reset(mFragment.bus);
        mFragment.mAutoCompleteFragment.clear();
        mFragment.onNextButtonClick();
        verifyZeroInteractions(mFragment.bus);
    }

    @Test
    public void shouldRequestEditAddressWhenSubmitButtonPressed() throws Exception {
        mFragment.mAutoCompleteFragment.setStreet(ADDRESS_STREET);
        mFragment.onNextButtonClick();
        AppAssertionUtils.assertBusPost(
                mFragment.bus,
                mCaptor,
                instanceOf(BookingEditEvent.RequestEditBookingAddress.class)
        );
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception {
        String errorMessage = mFragment.getString(R.string
                                                          .default_error_string);
        mFragment.onReceiveEditBookingAddressError(new BookingEditEvent.ReceiveEditBookingAddressError(
                new DataManager.DataManagerError(DataManager
                                                         .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
