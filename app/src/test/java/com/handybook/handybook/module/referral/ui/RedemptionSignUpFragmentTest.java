package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.ui.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class RedemptionSignUpFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock
    private RedemptionDetails mMockRedemptionDetails;
    @Mock
    private RedemptionDetails.Sender mMockSender;
    @Mock
    private RedemptionDetails.LocalizationData mMockLocalizationData;

    private RedemptionSignUpFragment mFragment;
    private View mFragmentView;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        when(mMockSender.getFirstName()).thenReturn("John");
        when(mMockLocalizationData.getCurrencySymbol()).thenReturn("$");
        when(mMockRedemptionDetails.getReceiverCouponAmount()).thenReturn(35);
        when(mMockRedemptionDetails.getSender()).thenReturn(mMockSender);
        when(mMockRedemptionDetails.getLocalizationData()).thenReturn(mMockLocalizationData);

        RedemptionFragment parentFragment = RedemptionFragment.newInstance("some_guid");
        SupportFragmentTestUtil.startVisibleFragment(parentFragment);
        parentFragment.onReceiveRedemptionDetailsSuccess(
                new ReferralsEvent.ReceiveRedemptionDetailsSuccess(mMockRedemptionDetails));

        mFragment = (RedemptionSignUpFragment) parentFragment.getFragmentManager()
                .findFragmentById(R.id.child_fragment_container);
        mFragmentView = mFragment.getView();
    }

    @Test
    public void shouldDisplayRedemptionDetails() throws Exception
    {
        final TextView title = (TextView) mFragmentView.findViewById(R.id.title);
        final TextView subtitle = (TextView) mFragmentView.findViewById(R.id.subtitle);
        assertThat(title.getText().toString(), equalTo("John sent you $35!"));
        assertThat(subtitle.getText().toString(),
                equalTo("Sign up to claim $35 credit towards your first booking."));
    }

    @Test
    public void shouldLaunchLoginActivityOnLoginButtonClicked() throws Exception
    {
        mFragmentView.findViewById(R.id.login_button).performClick();

        final Intent nextStartedActivity =
                shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(LoginActivity.class.getName()));
    }

    @Test
    public void shouldDisplayEmailSignUpFragmentOnRegisterWithEmailClicked() throws Exception
    {
        mFragmentView.findViewById(R.id.email_register_button).performClick();

        final Fragment fragment =
                mFragment.getFragmentManager().findFragmentById(R.id.child_fragment_container);
        assertThat(fragment, instanceOf(RedemptionEmailSignUpFragment.class));
    }
}
