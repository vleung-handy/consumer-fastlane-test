package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

import javax.inject.Inject;

public final class BookingRecurrenceActivity extends MenuDrawerActivity
{

    @Inject
    BookingManager mBookingManager;

    @Override
    protected final Fragment createFragment() {
        BookingQuote quote = mBookingManager.getCurrentQuote();
        if (quote.isCommitmentMonthsActive())
        {
            //TODO: JIA: use the new subscription fragment UI that Sammy created to handle "months" here.
            return BookingRecurrenceFragment.newInstance();
        }
        else
        {
            return BookingRecurrenceFragment.newInstance();
        }
    }

    @Override
    protected final String getNavItemTitle() {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
