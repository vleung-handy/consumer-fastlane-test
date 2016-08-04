package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ReportIssueFragment extends InjectedFragment
{
    @Bind(R.id.report_issue_date)
    TextView mDateText;
    @Bind(R.id.report_issue_time)
    TextView mTimeText;
    @Bind(R.id.report_issue_provider)
    TextView mProviderText;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private Booking mBooking;

    public static ReportIssueFragment newInstance(final Booking booking)
    {
        final ReportIssueFragment fragment = new ReportIssueFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_report_issue, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.help));
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        mDateText.setText(DateTimeUtils.formatDate(mBooking.getStartDate(), "EEEE',' MMM d',' yyyy",
                mBooking.getBookingTimezone()));

        final String startTime = DateTimeUtils.formatDate(mBooking.getStartDate(), "h:mm aaa",
                mBooking.getBookingTimezone());
        final String endTime = DateTimeUtils.formatDate(mBooking.getEndDate(), "h:mm aaa",
                mBooking.getBookingTimezone());
        mTimeText.setText(getString(R.string.dash_formatted, startTime, endTime));

        if (mBooking.getProvider().getFullName() != null)
        {
            mProviderText.setText(mBooking.getProvider().getFullName());
        }
    }
}
