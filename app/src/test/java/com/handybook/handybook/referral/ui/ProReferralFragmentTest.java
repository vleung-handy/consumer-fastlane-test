package com.handybook.handybook.referral.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.library.ui.view.ViewPager;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

/**
 * Tests handling of {@link ProReferralFragment}
 */
public class ProReferralFragmentTest extends RobolectricGradleTestWrapper {

    private static final String RECOMMEND = "Recommend";
    private static final String NAME = "Test Name";
    private static final String COUPON_CODE = "ABCD1234";

    private ProReferralFragment mFragment;

    @Mock
    ReferralDescriptor mMockReferralDescriptor;

    @Mock
    ProReferral mMockProReferral;

    @Mock
    Provider mMockProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        when(mMockProvider.getBookingCount()).thenReturn(5);
        when(mMockProvider.getAverageRating()).thenReturn(4.5f);
        when(mMockProvider.getName()).thenReturn(NAME);
        when(mMockProReferral.getProvider()).thenReturn(mMockProvider);
        when(mMockProReferral.getReferralButtonText()).thenReturn(RECOMMEND);

        List<ProReferral> mReferrals = new ArrayList<>();

        //make it so there are 4 ProReferral
        mReferrals.add(mMockProReferral);
        mReferrals.add(mMockProReferral);
        mReferrals.add(mMockProReferral);
        mReferrals.add(mMockProReferral);

        when(mMockReferralDescriptor.getProReferralInfo()).thenReturn(mReferrals);
        when(mMockReferralDescriptor.getSenderCreditAmount()).thenReturn(25);
        when(mMockReferralDescriptor.getCouponCode()).thenReturn(COUPON_CODE);

        mFragment = ProReferralFragment.newInstance(mMockReferralDescriptor, null);
    }

    /**
     * Test that the data displays properly onto the screen.
     */
    @Test
    public void testDataBind() {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        TextView subtitle = (TextView) mFragment.getView().findViewById(R.id.pro_referral_subtitle);
        TextView shareURL = (TextView) mFragment.getView()
                                                .findViewById(R.id.pro_referral_share_link);

        //test that the sender/receiver credits are shown properly.
        Assert.assertTrue(subtitle.getText().toString().contains("25"));
        Assert.assertTrue(shareURL.getText().toString().contains(COUPON_CODE));

        //verify that the pager has 4 items.
        ViewPager carouselPager = (ViewPager) mFragment.getView().findViewById(R.id.carousel_pager);
        Assert.assertEquals(4, carouselPager.getAdapter().getCount());
    }

    /**
     * Tests launching of the shared intent
     */
    @Test
    public void testShareButton() {
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        mFragment.sharedButtonClicked();
        ShadowActivity shadowActivity = shadowOf(mFragment.getActivity());
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        Intent nestedIntent = (Intent) startedIntent.getExtras().get(Intent.EXTRA_INTENT);

        Assert.assertEquals(nestedIntent.getAction(), Intent.ACTION_SEND);
        Assert.assertEquals(Intent.ACTION_PICK_ACTIVITY, startedIntent.getAction());
    }


}
