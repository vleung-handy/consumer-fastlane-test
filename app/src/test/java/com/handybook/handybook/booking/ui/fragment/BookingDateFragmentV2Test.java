package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.ui.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

/**
 * BookingDateFragment and BookingDateFragmentV2 will be consolidated soon
 * so this will be consolidated with BookingDateFragmentTest
 */
public class BookingDateFragmentV2Test extends RobolectricGradleTestWrapper {

    private BookingDateFragmentV2 mFragment;
    @Mock
    private BookingRequest mMockRequest;
    @Mock
    private BookingQuote mMockBookingQuote;
    @Inject
    BookingManager mBookingManager;
    @Inject
    UserManager mUserManager;
    @Inject
    DataManager mDataManager;
    @Captor
    private ArgumentCaptor<DataManager.Callback> mCallbackCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        when(mMockRequest.getUniq()).thenReturn("home_cleaning");
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockBookingQuote);
        when(mMockBookingQuote.getAddress()).thenReturn(mock(BookingQuote.Address.class));

        mFragment = BookingDateFragmentV2.newInstance(new ArrayList<BookingOption>());
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldSetStartDate() throws Exception {
        mFragment.mNextButton.performClick();
        verify(mMockRequest, atLeastOnce()).setStartDate(any(Date.class));
    }

    @Test
    public void shouldLaunchLoginActivityIfUserIsNotLoggedIn() throws Exception {
        when(mUserManager.getCurrentUser()).thenReturn(null);
        mFragment.mNextButton.performClick();
        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(LoginActivity.class.getName())
        );
    }

    @Test
    public void shouldLaunchBookingRecurrenceActivityIfUserIsLoggedIn() throws Exception {
        when(mUserManager.getCurrentUser()).thenReturn(mock(User.class));
        mFragment.mNextButton.performClick();

        verify(mDataManager).createQuote(
                any(BookingRequest.class),
                mCallbackCaptor.capture()
        );

        mCallbackCaptor.getValue().onSuccess(mMockBookingQuote);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingRecurrenceActivity.class.getName())
        );
    }
}
