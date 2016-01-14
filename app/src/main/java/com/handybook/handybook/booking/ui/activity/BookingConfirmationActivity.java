package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.fragment.BookingConfirmationFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingConfirmationActivity extends MenuDrawerActivity
{
    public static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    public static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    public static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";

    @Override
    protected final Fragment createFragment()
    {
        final int page = getIntent().getIntExtra(EXTRA_PAGE, 0);
        final boolean isNewUser = getIntent().getBooleanExtra(EXTRA_NEW_USER, false);
        final Instructions instructions = getIntent().getParcelableExtra(EXTRA_INSTRUCTIONS);
        return BookingConfirmationFragment.newInstance(page, isNewUser, instructions);
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
