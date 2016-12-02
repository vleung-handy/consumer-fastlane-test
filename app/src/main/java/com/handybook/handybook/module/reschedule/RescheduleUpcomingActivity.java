package com.handybook.handybook.module.reschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.view.BookingListItem;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This displays a list of future bookings that is qualified to be rescheduled. Used in the context
 * of a pro team conversation
 */
public class RescheduleUpcomingActivity extends BaseActivity
{
    @Bind(R.id.reschedule_recycler_view)
    EmptiableRecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.reschedule_empty_view)
    RelativeLayout mEmptyView;

    private BookingListAdapter mAdapter;
    private List<Booking> mBookings;
    private ProgressDialog mProgressDialog;
    private String mProviderId;
    private Booking mSelectedBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_upcoming);
        ButterKnife.bind(this);
        User user = mUserManager.getCurrentUser();

        mProviderId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);

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
        mRecyclerView.setEmptyView(mEmptyView);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE)
        {
            //reschedule date has been successful, just finish
            finish();
        }
    }

    public void onBookingReceived(final List<Booking> bookings)
    {
        mProgressDialog.dismiss();
        mBookings = bookings;

        mAdapter = new BookingListAdapter(mBookings, new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                //before we advance to the reschedule flow, we first must grab the pre-reschedule info
                mSelectedBooking = ((BookingListItem) v).getBooking();
                getPreRescheduleInfo();
            }
        },
                                          mConfigurationManager.getPersistentConfiguration()
                                                               .isBookingHoursClarificationExperimentEnabled()
        );
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onBookingsRequestError()
    {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void getPreRescheduleInfo()
    {
        mProgressDialog.setMessage(getString(R.string.rescheduling));
        mProgressDialog.show();
        mDataManager.getPreRescheduleInfo(
                mSelectedBooking.getId(),
                new PreRescheduleCallback(this)
        );
    }

    public void onReceivePreRescheduleInfoSuccess(String notice)
    {
        mProgressDialog.dismiss();

        final Intent intent = new Intent(this, BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mSelectedBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.FROM_CHAT);
        intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    public void onRescheduleRequestError()
    {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.reschedule_try_again, Toast.LENGTH_SHORT).show();
    }


    private static class PreRescheduleCallback implements DataManager.Callback<String>
    {

        private final WeakReference<RescheduleUpcomingActivity> mActivity;

        public PreRescheduleCallback(RescheduleUpcomingActivity activity)
        {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final String response)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onReceivePreRescheduleInfoSuccess(response);
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (mActivity.get() != null)
            {
                mActivity.get().onRescheduleRequestError();
            }
        }
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
