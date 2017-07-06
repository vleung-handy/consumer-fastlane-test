package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.ui.fragment.ReportIssueFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public class ReportIssueActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        final JobStatus proStatuses =
                (JobStatus) getIntent().getSerializableExtra(BundleKeys.PRO_JOB_STATUS);
        return ReportIssueFragment.newInstance(booking, proStatuses);
    }
}
