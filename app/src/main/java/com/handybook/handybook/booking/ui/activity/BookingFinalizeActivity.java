package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.fragment.BookingEntryInfoFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPasswordPromptFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPreferencesFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingFinalizeActivity extends MenuDrawerActivity
{
    public static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    public static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    public static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";
    public static final String EXTRA_ENTRY_METHODS_INFO = "com.handy.handy.EXTRA_ENTRY_METHODS_INFO";
    public static final String EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID";

    public static final int PAGE_ENTRY_INFORMATION = 0;
    public static final int PAGE_PREFERENCES = 1;
    public static final int PAGE_PASSWORD_PROMPT = 2;


    @Override
    protected final Fragment createFragment()
    {
        final boolean isNewUser = getIntent().getBooleanExtra(EXTRA_NEW_USER, false);
        final Instructions instructions = getIntent().getParcelableExtra(EXTRA_INSTRUCTIONS);
        final EntryMethodsInfo entryMethodsInfo =
                (EntryMethodsInfo)
                getIntent().getSerializableExtra(EXTRA_ENTRY_METHODS_INFO);
        switch (getIntent().getIntExtra(EXTRA_PAGE, PAGE_ENTRY_INFORMATION))
        {
            case PAGE_ENTRY_INFORMATION:
                return BookingEntryInfoFragment.newInstance(isNewUser, instructions, entryMethodsInfo);
            case PAGE_PREFERENCES:
                return BookingPreferencesFragment.newInstance(isNewUser, instructions);
            case PAGE_PASSWORD_PROMPT:
                return BookingPasswordPromptFragment.newInstance();
            default:
                return BookingEntryInfoFragment.newInstance(isNewUser, instructions, entryMethodsInfo);
        }
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
