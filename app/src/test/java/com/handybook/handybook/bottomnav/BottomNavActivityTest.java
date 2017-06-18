package com.handybook.handybook.bottomnav;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.core.util.TestUtils;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.referral.ui.ReferralV2Fragment;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class BottomNavActivityTest extends RobolectricGradleTestWrapper {

    private BottomNavActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = Robolectric.buildActivity(BottomNavActivity.class)
                               .create().start().resume().visible().get();
    }

    @Test
    public void shouldDisplayServiceCategoriesHomeFragmentOnStart() {
        Fragment currentFragment =
                TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ServiceCategoriesHomeFragment.class));
    }

    @Test
    public void shouldDisplayAccountFragmentAfterClick() {
        mActivity.findViewById(R.id.account).performClick();
        Fragment currentFragment =
                TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(AccountFragment.class));
    }

    @Test
    public void shouldDisplayReferralV2FragmentAfterClick() {
        mActivity.findViewById(R.id.gift).performClick();
        Fragment currentFragment =
                TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ReferralV2Fragment.class));
    }

    @Test
    public void shouldDisplayUpcomingBookingsFragmentAfterClick() {
        mActivity.findViewById(R.id.bookings).performClick();
        Fragment currentFragment =
                TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(UpcomingBookingsFragment.class));
    }

    @Test
    public void shouldDisplayProTeamConversationsFragmentAfterClick() {
        mActivity.findViewById(R.id.messages).performClick();
        Fragment currentFragment =
                TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ProTeamConversationsFragment.class));
    }
}
