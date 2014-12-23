package com.handybook.handybook;

import android.content.Intent;
import android.support.v4.app.Fragment;

public final class BookingsActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return BookingsFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.my_bookings);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
