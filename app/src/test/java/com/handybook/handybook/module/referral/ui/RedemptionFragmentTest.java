package com.handybook.handybook.module.referral.ui;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.testutil.AppAssertionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class RedemptionFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock
    private DataManager.DataManagerError mMockError;

    private RedemptionFragment mFragment;
    private String mTestGuid;
    private String mTestErrorMessage;
    private ReferralsEvent.ReceiveRedemptionDetailsError mTestReceiveRedemptionDetailsError;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        mTestGuid = "test_guid";
        mTestErrorMessage = "Something bad happened!";
        mFragment = RedemptionFragment.newInstance(mTestGuid);
        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        when(mMockError.getMessage()).thenReturn(mTestErrorMessage);
        mTestReceiveRedemptionDetailsError =
                new ReferralsEvent.ReceiveRedemptionDetailsError(mMockError);
    }

    @Test
    public void shouldRequestRedemptionDetails() throws Exception
    {
        final ReferralsEvent.RequestRedemptionDetails event =
                AppAssertionUtils.getFirstMatchingBusEvent(mFragment.bus,
                        ReferralsEvent.RequestRedemptionDetails.class);
        assertNotNull(event);
        assertThat(event.getGuid(), equalTo(mTestGuid));
    }

    @Test
    public void shouldDisplaySignUpFragmentOnReceiveRedemptionDetailsSuccess() throws Exception
    {
        final ReferralsEvent.ReceiveRedemptionDetailsSuccess mockEvent =
                mock(ReferralsEvent.ReceiveRedemptionDetailsSuccess.class,
                        Answers.RETURNS_DEEP_STUBS.get());

        mFragment.onReceiveRedemptionDetailsSuccess(mockEvent);

        final Fragment fragment =
                mFragment.getFragmentManager().findFragmentById(R.id.child_fragment_container);
        assertNotNull(fragment);
        assertThat(fragment, instanceOf(RedemptionSignUpFragment.class));
    }

    @Test
    public void shouldDisplayDialogOnReceiveRedemptionDetailsError() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        mFragment.onReceiveRedemptionDetailsError(mTestReceiveRedemptionDetailsError);

        final Dialog dialog = ShadowAlertDialog.getLatestDialog();
        final TextView title = (TextView) dialog.findViewById(R.id.alertTitle);
        assertThat(title.getText().toString(), equalTo(mTestErrorMessage));
    }

    @Test
    public void shouldNavigateToHomeScreenOnErrorDialogRetry() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        mFragment.onReceiveRedemptionDetailsError(mTestReceiveRedemptionDetailsError);
        reset(mFragment.bus);

        final Dialog dialog = ShadowAlertDialog.getLatestDialog();
        dialog.findViewById(android.R.id.button1).performClick();

        final ReferralsEvent.RequestRedemptionDetails event =
                AppAssertionUtils.getFirstMatchingBusEvent(mFragment.bus,
                        ReferralsEvent.RequestRedemptionDetails.class);
        assertNotNull(event);
        assertThat(event.getGuid(), equalTo(mTestGuid));
    }

    @Test
    public void shouldNavigateToHomeScreenOnErrorDialogCancel() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        mFragment.onReceiveRedemptionDetailsError(mTestReceiveRedemptionDetailsError);

        final Dialog dialog = ShadowAlertDialog.getLatestDialog();
        dialog.findViewById(android.R.id.button2).performClick();

        final Intent nextStartedActivity =
                shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(ServiceCategoriesActivity.class.getName()));
    }

    @Test
    public void shouldDisplayToastOnReceiveAuthUserError() throws Exception
    {
        final HandyEvent.ReceiveAuthUserError mockEvent =
                new HandyEvent.ReceiveAuthUserError(mMockError, UserDataManager.AuthType.FACEBOOK);
        mFragment.onReceiveAuthUserError(mockEvent);

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(mTestErrorMessage));
    }

    @Test
    public void shouldNavigateToHomeScreenOnReceiveAuthUserSuccess() throws Exception
    {
        mFragment.onReceiveAuthUserSuccess(mock(HandyEvent.ReceiveAuthUserSuccess.class,
                Answers.RETURNS_DEEP_STUBS.get()));

        final Intent nextStartedActivity =
                shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(ServiceCategoriesActivity.class.getName()));
    }
}
