package com.handybook.handybook.account.ui;


import android.view.View;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.event.StripeEvent;
import com.handybook.handybook.testutil.AppAssertionUtils;
import com.stripe.android.model.Token;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdatePaymentFragmentTest extends RobolectricGradleTestWrapper
{
    private static final String TEST_CREDIT_CARD_NUMBER = "4242424242424242";
    private UpdatePaymentFragment mFragment;
    @Inject
    UserManager mUserManager;
    @Mock
    private User mMockUser;
    @Mock
    private Token mMockToken;
    @Mock
    private User.CreditCard mMockCreditCard;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mUserManager.getCurrentUser()).thenReturn(mMockUser);

        mFragment = UpdatePaymentFragment.newInstance();
    }

    @Test
    public void shouldShowCreditCardInfoFieldsByDefaultIfNoCard() throws Exception
    {
        when(mMockUser.getCreditCard()).thenReturn(null);

        //Activity is needed to be passed because the Fragment needs access to BaseActivity info
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);

        assertThat(mFragment.mChangeButton.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mCreditCardText.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCardExtrasLayout.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowCreditCardInfoFieldsOnChangeButtonClicked() throws Exception
    {
        when(mMockUser.getCreditCard()).thenReturn(mMockCreditCard);
        when(mMockCreditCard.getLast4()).thenReturn("1234");

        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);

        assertThat(mFragment.mChangeButton.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCreditCardText.getHint().toString(), equalTo("•••• 1234"));
        assertThat(mFragment.mCardExtrasLayout.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.GONE));

        mFragment.mChangeButton.performClick();

        assertThat(mFragment.mChangeButton.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mCreditCardText.getHint().toString(), equalTo("Credit Card Number"));
        assertThat(mFragment.mCardExtrasLayout.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.VISIBLE));
    }

    @Test
    public void shouldResetAndFreezeCreditCardInfoFieldsOnCancelButtonClicked() throws Exception
    {
        when(mMockUser.getCreditCard()).thenReturn(mMockCreditCard);
        when(mMockCreditCard.getLast4()).thenReturn("1234");

        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
        mFragment.mChangeButton.performClick();
        mFragment.mCreditCardText.setText(TEST_CREDIT_CARD_NUMBER);

        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.VISIBLE));
        mFragment.mCancelButton.performClick();

        assertThat(mFragment.mChangeButton.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mCreditCardText.getHint().toString(), equalTo("•••• 1234"));
        assertThat(mFragment.mCardExtrasLayout.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldRequestStripeTokenWithDataFromFields() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);

        mFragment.mCreditCardText.setText(TEST_CREDIT_CARD_NUMBER);
        mFragment.mExpText.setText("01/30");
        mFragment.mCvcText.setText("123");

        mFragment.mUpdateButton.performClick();

        final StripeEvent.RequestCreateToken event = AppAssertionUtils.getFirstMatchingBusEvent(
                mFragment.bus, StripeEvent.RequestCreateToken.class);

        assertNotNull(event);
        assertNotNull(event.getCard());
        assertThat(event.getCard().getNumber(), equalTo(TEST_CREDIT_CARD_NUMBER));
        assertThat(event.getCard().getExpMonth(), equalTo(1));
        assertThat(event.getCard().getExpYear(), equalTo(30));
        assertThat(event.getCard().getCVC(), equalTo("123"));
    }

    @Test
    public void shouldRequestUpdatePaymentInfoAfterObtainingStripeToken() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);

        StripeEvent.ReceiveCreateTokenSuccess event =
                new StripeEvent.ReceiveCreateTokenSuccess(mMockToken);
        mFragment.onReceiveCreateTokenSuccess(event);

        final HandyEvent.RequestUpdatePayment requestEvent =
                AppAssertionUtils.getFirstMatchingBusEvent(
                        mFragment.bus, HandyEvent.RequestUpdatePayment.class);

        assertNotNull(requestEvent);
        assertThat(requestEvent.token, equalTo(mMockToken));
    }

    @Test
    public void shouldDisplayNewPaymentInfoAfterSuccessfulUpdate() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
        mFragment.mCreditCardText.setText(TEST_CREDIT_CARD_NUMBER);
        mFragment.mCreditCardIcon = spy(mFragment.mCreditCardIcon);

        mFragment.onReceiveUpdatePaymentSuccess(null);

        verify(mFragment.mCreditCardIcon).setCardIcon(CreditCard.Type.VISA);
        assertThat(mFragment.mCreditCardText.getText().toString(), equalTo(""));
        assertThat(mFragment.mCreditCardText.getHint().toString(), equalTo("•••• 4242"));
        assertThat(mFragment.mCardExtrasLayout.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mCancelButton.getVisibility(), equalTo(View.GONE));
    }
}
