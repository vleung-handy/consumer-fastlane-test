package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.subscription.CommitmentType;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingSubscriptionFragment;
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
            mBookingManager.getCurrentTransaction().setCommitmentType(CommitmentType.STRING_MONTHS);
            return BookingSubscriptionFragment.newInstance();
        }
        else
        {
            mBookingManager.getCurrentTransaction().setCommitmentType(CommitmentType.STRING_NO_COMMITMENT);
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
