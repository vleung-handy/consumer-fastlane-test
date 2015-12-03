package com.handybook.handybook.ui.fragment;

import android.content.Intent;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingPaymentFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingPaymentFragment mFragment;

    @Mock
    private BookingTransaction mMockTransaction;
    @Mock
    private BookingQuote mMockQuote;
    @Mock
    private BookingRequest mMockRequest;
    @Mock
    private Token mMockToken;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookingCompleteTransaction mCompleteTransaction;
    @Mock
    private User mUser;
    @Inject
    BookingManager mBookingManager;
    @Inject
    UserManager mUserManager;
    @Captor
    private ArgumentCaptor<TokenCallback> mTokenCallbackCaptor;
    @Captor
    private ArgumentCaptor<DataManager.Callback> mDataManagerCallbackCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mUserManager.getCurrentUser()).thenReturn(mUser);
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mMockQuote.getPricing(anyFloat(), anyInt())).thenReturn(new float[]{0.0f, 0.0f});
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingPaymentFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void shouldGetStripeTokenAndCompleteBooking() throws Exception
    {
        mFragment.mCreditCardText.setText("4242424242424242");
        mFragment.mExpText.setText("01/30");
        mFragment.mCvcText.setText("123");

        mFragment.mNextButton.performClick();

        verify(mFragment.mStripe).createToken(any(Card.class), anyString(),
                mTokenCallbackCaptor.capture());

        when(mMockToken.getId()).thenReturn("some_id");
        mTokenCallbackCaptor.getValue().onSuccess(mMockToken);

        verify(mMockTransaction).setStripeToken(eq("some_id"));

        verify(mFragment.dataManager).createBooking(eq(mMockTransaction),
                mDataManagerCallbackCaptor.capture());

        mDataManagerCallbackCaptor.getValue().onSuccess(mCompleteTransaction);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingConfirmationActivity.class.getName()));
    }
}
