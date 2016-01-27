package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.testutil.AppAssertionUtils;
import com.handybook.handybook.ui.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

public class RedemptionEmailSignUpFragmentTest extends RobolectricGradleTestWrapper
{
    private View mFragmentView;
    private RedemptionEmailSignUpFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        mFragment = RedemptionEmailSignUpFragment.newInstance("some_guid");
        SupportFragmentTestUtil.startVisibleFragment(mFragment);
        mFragmentView = mFragment.getView();
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
    public void shouldRequestCreateUserOnSignUpClicked() throws Exception
    {
        mFragment.mEmailInput.setText("john@email.com");
        mFragment.mPasswordInput.setText("12341234");
        mFragmentView.findViewById(R.id.sign_up_button).performClick();

        final HandyEvent.RequestCreateUser event = AppAssertionUtils
                .getFirstMatchingBusEvent(mFragment.bus, HandyEvent.RequestCreateUser.class);
        assertThat(event.getEmail(), equalTo("john@email.com"));
        assertThat(event.getPassword(), equalTo("12341234"));
        assertThat(event.getReferralGuid(), equalTo("some_guid"));
    }

    @Test
    public void shouldNotRequestCreateUserIfEmailIsInvalid() throws Exception
    {
        mFragment.mEmailInput.setText("john");
        mFragment.mPasswordInput.setText("12341234");
        mFragmentView.findViewById(R.id.sign_up_button).performClick();

        final HandyEvent.RequestCreateUser event = AppAssertionUtils
                .getFirstMatchingBusEvent(mFragment.bus, HandyEvent.RequestCreateUser.class);
        assertNull(event);
    }

    @Test
    public void shouldNotRequestCreateUserIfPasswordIsInvalid() throws Exception
    {
        mFragment.mEmailInput.setText("john@email.com");
        mFragment.mPasswordInput.setText("123");
        mFragmentView.findViewById(R.id.sign_up_button).performClick();

        final HandyEvent.RequestCreateUser event = AppAssertionUtils
                .getFirstMatchingBusEvent(mFragment.bus, HandyEvent.RequestCreateUser.class);
        assertNull(event);
    }
}
