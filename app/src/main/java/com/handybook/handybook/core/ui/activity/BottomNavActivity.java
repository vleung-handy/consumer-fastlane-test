package com.handybook.handybook.core.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.module.bookings.UpcomingBookingsFragment;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.module.referral.ui.ReferralFragment;

import butterknife.ButterKnife;


public class BottomNavActivity extends BaseActivity
{
    // TODO: uncomment when buildToolsVersion is updated to 25
//    @Bind(R.id.bottom_navigation)
//    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);
//        UiUtils.removeShiftMode(mBottomNavigationView);
//
//        mBottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener()
//                {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull final MenuItem item)
//                    {
//                        return onNavItemSelected(item);
//                    }
//                });
//        User user = mUserManager.getCurrentUser();
//        if (user != null
//                && user.getAnalytics() != null
//                && user.getAnalytics().getUpcomingBookings() > 0
//                && ((BaseApplication) getApplication()).isNewlyLaunched())
//
//        {
//            mBottomNavigationView.findViewById(R.id.bookings).performClick();
//        }
//        else
//        {
//            mBottomNavigationView.findViewById(R.id.add_booking).performClick();
//        }
    }

    private boolean onNavItemSelected(@NonNull final MenuItem item)
    {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.bookings:
                fragment = UpcomingBookingsFragment.newInstance();
                break;
            case R.id.pro_team:
                if (mConfigurationManager.getPersistentConfiguration().isChatEnabled())
                {
                    fragment = ProTeamConversationsFragment.newInstance();
                }
                else
                {
                    fragment = ProTeamFragment.newInstance();
                }
                break;
            case R.id.add_booking:
                fragment = ServiceCategoriesFragment.newInstance(null, null);
                break;
            case R.id.gift:
                fragment = ReferralFragment.newInstance(null);
                break;
            case R.id.account:
                fragment = AccountFragment.newInstance();
                break;
        }
        if (fragment != null)
        {
            FragmentUtils.switchToFragment(BottomNavActivity.this, fragment, false);
            return true;
        }
        else
        {
            return false;
        }
    }
}
