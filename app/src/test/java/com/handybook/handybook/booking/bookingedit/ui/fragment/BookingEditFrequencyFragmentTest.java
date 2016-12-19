package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.constant.BookingRecurrence;
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
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingEditFrequencyFragmentTest extends RobolectricGradleTestWrapper
{
    //TODO: add a test to verify that the request reflects the options selected
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

        when(mBooking.getId()).thenReturn("12345");
        mFragment = BookingEditFrequencyFragment.newInstance(mBooking, null);

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldRequestEditFrequencyViewModelOnCreateView() throws Exception
    {
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEditEvent.RequestGetEditFrequencyViewModel.class));
    }

    @Test
    public void shouldRequestEditFrequencyWhenSubmitButtonPressed() throws Exception
    {
        when(mBookingEditFrequencyInfoResponse.getCurrentFrequency()).thenReturn(BookingRecurrence.WEEKLY);
        BookingEditFrequencyViewModel editFrequencyViewModel =
                BookingEditFrequencyViewModel.from(mBookingEditFrequencyInfoResponse);

        when(mBooking.getServiceMachineName()).thenReturn(Booking.SERVICE_CLEANING);

        mFragment.onReceiveEditFrequencyViewModelSuccess(new BookingEditEvent.ReceiveGetEditFrequencyViewModelSuccess(editFrequencyViewModel));
        mFragment.mSaveButton.performClick();
        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor, instanceOf(BookingEditEvent.RequestEditBookingFrequency.class));
    }

    @Test
    public void shouldShowErrorToastWhenServerError() throws Exception
    {
        String errorMessage = mFragment.getString(R.string
                .default_error_string);
        mFragment.onReceiveUpdateBookingFrequencyError(new BookingEditEvent.ReceiveEditBookingFrequencyError(new DataManager.DataManagerError(DataManager
                .Type.SERVER)));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(errorMessage));
    }
}
