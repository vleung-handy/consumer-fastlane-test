package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.ui.fragment.ReportIssueFragment;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class ReportIssueActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment()
    {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        final JobStatus proStatuses =
                (JobStatus) getIntent().getSerializableExtra(BundleKeys.PRO_JOB_STATUS);
        return ReportIssueFragment.newInstance(booking, proStatuses);
    }

    @Override
    protected String getNavItemTitle() { return null; }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
