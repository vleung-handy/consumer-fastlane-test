package com.handybook.handybook.referral.ui;

import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.CancellableCallback;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This tests the handling of {@link ReferralV2Fragment}
 */
public class ReferralV2FragmentTest extends RobolectricGradleTestWrapper {

    private ReferralV2Fragment mFragment;

    @Inject
    DataManager mDataManager;

    @Captor
    private ArgumentCaptor<CancellableCallback> mCallbackCaptor;

    @Mock
    ReferralResponse mMockReferralResponse;

    @Mock
    ReferralDescriptor mMockReferralDescriptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        mFragment = ReferralV2Fragment.newInstance(null);
    }

    /**
     * If there are pro referral information, then we should be going to the
     * {@link ProReferralFragment}
     *
     */
    @Test
    public void testRedirectionToProReferralFragment() {
        //sets up the data such that there are pro referrals being returned from the service call
        List<ProReferral> referrals = new ArrayList<>();
        referrals.add(new ProReferral());

        when(mMockReferralResponse.getReferralDescriptor()).thenReturn(mMockReferralDescriptor);
        when(mMockReferralDescriptor.getProReferralInfo()).thenReturn(referrals);

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        verify(mDataManager).requestPrepareReferrals(anyBoolean(), mCallbackCaptor.capture());

        mCallbackCaptor.getValue().onCallbackSuccess(mMockReferralResponse);

        //check that the pro referral carousel is being shown on the screen.
        Assert.assertNotNull(mFragment.getChildFragmentManager()
                                      .findFragmentByTag(ProReferralFragment.class.getName()));
    }

    /**
     * If there are no pro team information then it should go to the {@link ReferralFragment}
     */
    @Test
    public void testRedirectionToReferralFragment() {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        when(mMockReferralResponse.getReferralDescriptor()).thenReturn(mMockReferralDescriptor);
        when(mMockReferralDescriptor.getProReferralInfo()).thenReturn(null);

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        verify(mDataManager).requestPrepareReferrals(anyBoolean(), mCallbackCaptor.capture());

        mCallbackCaptor.getValue().onCallbackSuccess(mMockReferralResponse);

        Assert.assertNotNull(mFragment.getChildFragmentManager()
                                      .findFragmentByTag(ReferralFragment.class.getName()));
    }
}
