package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingProTeamActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment()
    {
        return BookingProTeamFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }

}
