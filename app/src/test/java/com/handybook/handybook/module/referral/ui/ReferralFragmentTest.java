package com.handybook.handybook.module.referral.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.event.ReferralsEvent.RequestConfirmReferral;
import com.handybook.handybook.module.referral.model.ReferralChannels;
import com.handybook.handybook.module.referral.model.ReferralDescriptor;
import com.handybook.handybook.module.referral.model.ReferralInfo;
import com.handybook.handybook.module.referral.model.ReferralResponse;
import com.handybook.handybook.testutil.AppAssertionUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

/**
 * TODO: JIA: fixed this in the future when we finally get the toolbar out of the fragment.
 * Currently, the toolbars are in the fragments, therefore, to setup the toolbar, it needs to call
 * the parent activity. Calling the parent activity will cause this test suite to fail
 */
@Ignore
public class ReferralFragmentTest extends RobolectricGradleTestWrapper
{
    private ReferralFragment mFragment;

    @Mock
    private ReferralsEvent.ReceivePrepareReferralsSuccess mMockReceivePrepareReferralsSuccessEvent;
    @Mock
    private ReferralResponse mMockReferralResponse;
    @Mock
    private ReferralDescriptor mMockReferralDescriptor;
    @Mock
    private ReferralChannels mMockReferralChannels;
    @Mock
    private ReferralInfo mMockReferralInfo;
    @Inject
    UserManager mUserManager;

    @Before
    public void setUp() throws Exception
    {
        mFragment = (ReferralFragment) ReferralFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment);
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        preventAnimationStart(mFragment, "mImage");
        preventAnimationStart(mFragment, "mEnvelopeShadow");
        preventAnimationStart(mFragment, "mBling");

        mFragment = spy(mFragment);
        final FragmentActivity spyActivity = spy(mFragment.getActivity());
        PackageManager mockPackageManager = mock(PackageManager.class);
        when(mockPackageManager.resolveActivity(any(Intent.class), eq(0)))
                .thenReturn(mock(ResolveInfo.class));
        when(spyActivity.getPackageManager()).thenReturn(mockPackageManager);
        when(mFragment.getActivity()).thenReturn(spyActivity);

        when(mMockReceivePrepareReferralsSuccessEvent.getReferralResponse())
                .thenReturn(mMockReferralResponse);
        when(mMockReferralResponse.getReferralDescriptor()).thenReturn(mMockReferralDescriptor);
        when(mMockReferralDescriptor.getReferralChannelsForSource(
                ReferralDescriptor.SOURCE_REFERRAL_PAGE)).thenReturn(mMockReferralChannels);
        final User mockUser = mock(User.class);
        when(mockUser.getCurrencyChar()).thenReturn("$");
        when(mUserManager.getCurrentUser()).thenReturn(mockUser);
        when(mMockReferralDescriptor.getReceiverCouponAmount()).thenReturn(30);
        when(mMockReferralDescriptor.getSenderCreditAmount()).thenReturn(20);
        when(mMockReferralDescriptor.getCouponCode()).thenReturn("ABC123");
    }

    @Test
    public void shouldRequestPrepareReferrals() throws Exception
    {
        assertNotNull(AppAssertionUtils.getFirstMatchingBusEvent(mFragment.bus,
                ReferralsEvent.RequestPrepareReferrals.class));
    }

    @Test
    public void shouldDisplayReferralDataOnReceivePrepareReferralsSuccess() throws Exception
    {
        mFragment.onReceivePrepareReferralsSuccess(mMockReceivePrepareReferralsSuccessEvent);

        assertThat(mFragment.mReferralContent.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mTitle.getText().toString(), equalTo("Give $30, Get $20"));
        assertThat(mFragment.mSubtitle.getText().toString(),
                equalTo("Give your friends $30 off their first Handy booking, and you get $20!"));
        assertThat(mFragment.mShareUrl.getText().toString(), equalTo("ABC123"));
    }

    @Test
    public void shouldLaunchSmsIntentOnInviteFriendsClicked() throws Exception
    {
        when(mMockReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS))
                .thenReturn(mMockReferralInfo);
        when(mMockReferralInfo.getMessage()).thenReturn("share me!");
        mFragment.onReceivePrepareReferralsSuccess(mMockReceivePrepareReferralsSuccessEvent);

        mFragment.onSmsShareButtonClicked();
        final Intent intent = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(intent.getStringExtra("sms_body"), equalTo("share me!"));
        assertThat(intent.getAction(), equalTo(Intent.ACTION_VIEW));
    }

    @Test
    public void shouldLaunchActivityPickerOnShareClicked() throws Exception
    {
        mFragment.onOtherShareCtaClicked();
        final ShadowActivity.IntentForResult intent =
                shadowOf(mFragment.getActivity()).getNextStartedActivityForResult();
        assertThat(intent.intent.getAction(), equalTo(Intent.ACTION_PICK_ACTIVITY));
    }

    @Test
    public void shouldConfirmAndLaunchSelectedActivityOnActivityResult() throws Exception
    {
        // assume selected activity was Facebook
        mFragment.onReceivePrepareReferralsSuccess(mMockReceivePrepareReferralsSuccessEvent);
        final Intent mockIntent = mock(Intent.class, Answers.RETURNS_DEEP_STUBS.get());
        when(mockIntent.getComponent().getPackageName()).thenReturn("facebook");
        when(mMockReferralInfo.getGuid()).thenReturn("1234");
        when(mMockReferralInfo.getUrl()).thenReturn("referral/url");
        when(mMockReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_FACEBOOK))
                .thenReturn(mMockReferralInfo);

        mFragment.onActivityResult(ActivityResult.PICK_ACTIVITY, Activity.RESULT_OK, mockIntent);

        final RequestConfirmReferral event =
                AppAssertionUtils.getFirstMatchingBusEvent(mFragment.bus,
                        RequestConfirmReferral.class);
        assertNotNull(event);
        assertThat(event.getGuid(), equalTo("1234"));
        final Intent intent = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        verify(intent).putExtra(Intent.EXTRA_TEXT, "referral/url");
    }
}
