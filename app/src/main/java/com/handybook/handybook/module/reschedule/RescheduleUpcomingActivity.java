package com.handybook.handybook.module.reschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This displays a list of future bookings that is qualified to be rescheduled. Used in the context
 * of a pro team conversation
 */
public class RescheduleUpcomingActivity extends BaseActivity
{
    @Bind(R.id.reschedule_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private BookingListAdapter mAdapter;
    private List<Booking> mBookings;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_upcoming);
        ButterKnife.bind(this);
        User user = mUserManager.getCurrentUser();

        mToolbar.setTitle(R.string.reschedule_title);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        if (user == null)
        {
            startActivity(new Intent(this, ServiceCategoriesActivity.class));
            finish();
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        mDataManager.getBookings(
                user,
                Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING,
                new BookingsCallback(this)
        );

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    @OnClick(R.id.reschedule_next)
    public void nextClicked()
    {

        if (mAdapter.getCheckedIndex() < 0)
        {
            Toast.makeText(this, "Please select a booking to reschedule", Toast.LENGTH_SHORT)
                 .show();
            return;
        }

        //TODO: JIA implement this
        Toast.makeText(this, "Submit not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void onBookingReceived(final List<Booking> bookings)
    {
        mProgressDialog.dismiss();
        mBookings = bookings;

        mAdapter = new BookingListAdapter(mBookings);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onBookingsRequestError()
    {
        mProgressDialog.dismiss();
    }

    private static class BookingsCallback implements DataManager.Callback<UserBookingsWrapper>
    {

        private final WeakReference<RescheduleUpcomingActivity> mActivity;

        public BookingsCallback(RescheduleUpcomingActivity activity)
        {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final UserBookingsWrapper response)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onBookingReceived(response.getBookings());
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onBookingsRequestError();
            }
        }
    }
}
