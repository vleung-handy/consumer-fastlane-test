
package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.MaskedWallet;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.BookingCompleteTransaction;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.StripeEvent;
import com.handybook.handybook.testutil.AppAssertionUtils;
import com.handybook.handybook.booking.ui.activity.BookingConfirmationActivity;

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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
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
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookingCompleteTransaction mCompleteTransaction;
    @Mock
    private User mMockUser;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StripeEvent.ReceiveCreateTokenSuccess mMockReceiveCreateTokenSuccessEvent;
    @Inject
    BookingManager mBookingManager;
    @Inject
    UserManager mUserManager;
    @Inject
    DataManager mDataManager; //there is currently no bus event for applyPromo()
    @Captor
    private ArgumentCaptor<DataManager.Callback> mDataManagerCallbackCaptor;
    @Captor
    private ArgumentCaptor<Object> mCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mUserManager.getCurrentUser()).thenReturn(mMockUser);
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mMockQuote.getPricing(anyFloat(), anyInt())).thenReturn(new float[]{0.0f, 0.0f});
        when(mMockQuote.isAndroidPayEnabled()).thenReturn(true);
        when(mMockQuote.getAndroidPayCouponCode()).thenReturn("ANDROIDPAY");
        when(mMockQuote.getAndroidPayCouponValueFormatted()).thenReturn("$10");
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingPaymentFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void shouldGetStripeToken() throws Exception
    {
        mFragment.mCreditCardText.setText("4242424242424242");
        mFragment.mExpText.setText("01/30");
        mFragment.mCvcText.setText("123");

        mFragment.mNextButton.performClick();

        AppAssertionUtils.assertBusPost(mFragment.bus, mCaptor,
                instanceOf(StripeEvent.RequestCreateToken.class));
    }


    /**
     *  cannot mock out any Google classes since they are final
     *  and many of our methods depend on them
     *  so the AP tests are limited until we refactor the fragment code to be more modular
     */
    @Test
    public void shouldShowAndroidPayOptionWhenShouldShowAndroidPay()
    {
        BookingPaymentFragment fragmentSpy = spy(mFragment);
        when(mUserManager.getCurrentUser()).thenReturn(null);
        BooleanResult booleanResult = new BooleanResult(new Status(0), true);
        fragmentSpy.showPaymentMethodSelection(booleanResult);
        verify(fragmentSpy).showSelectPaymentLayout();
    }

    @Test
    public void shouldAutoApplyAndroidPayCouponWhenAndroidPayShown()
    {
        MaskedWallet maskedWallet = MaskedWallet.zzIl().
                setPaymentDescriptions(new String[]{"blah"}).
                build();

        mFragment.showMaskedWalletInfo(maskedWallet);

        //there is currently no bus event for this
        verify(mDataManager).applyPromo(eq(mMockQuote.getAndroidPayCouponCode()), anyInt(), anyString(),
                anyString(), anyString(), any(DataManager.Callback.class));
    }

    @Test
    public void shouldCompleteBookingAfterGettingStripeToken() throws Exception
    {
        when(mMockReceiveCreateTokenSuccessEvent.getToken().getId()).thenReturn("some_id");
        mFragment.onReceiveCreateTokenSuccess(mMockReceiveCreateTokenSuccessEvent);

        verify(mMockTransaction).setStripeToken(eq("some_id"));

        verify(mFragment.dataManager).createBooking(eq(mMockTransaction),
                mDataManagerCallbackCaptor.capture());

        mDataManagerCallbackCaptor.getValue().onSuccess(mCompleteTransaction);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingConfirmationActivity.class.getName()));
    }
}
