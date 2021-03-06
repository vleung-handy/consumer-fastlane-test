package com.handybook.handybook.booking.reschedule;

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
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.Retrofit2ActivitySafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.view.BookingListItem;
import com.handybook.handybook.core.ui.view.SimpleDividerItemDecoration;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This displays a list of future bookings that is qualified to be rescheduled. Used in the context
 * of a pro team conversation.  Assumes that this activity will only be called if there are bookings
 * to reschedule
 */
public class RescheduleUpcomingActivity extends BaseActivity {

    @BindView(R.id.reschedule_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private BookingListAdapter mAdapter;
    private List<Booking> mBookings;
    private ProgressDialog mProgressDialog;
    private String mProviderId;
    private Booking mSelectedBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_upcoming);
        ButterKnife.bind(this);

        mProviderId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);
        mBookings = (List<Booking>) getIntent().getSerializableExtra(BundleKeys.BOOKINGS);

        mToolbar.setTitle(R.string.reschedule_title);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (!mUserManager.isUserLoggedIn()) {
            //user is not logged in.
            startActivity(new Intent(this, ServiceCategoriesActivity.class));
            finish();
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setDelay(400);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading));

        if (mBookings == null) {
            loadBookings();
        }
        else {
            onBookingReceived(mBookings);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void loadBookings() {
        mProgressDialog.show();
        mDataManager.getBookingsForReschedule(mProviderId, new BookingsCallback(this));
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
            finish();
        }
    }

    public void onBookingReceived(final List<Booking> bookings) {
        mProgressDialog.dismiss();
        mBookings = bookings;

        mAdapter = new BookingListAdapter(
                mBookings,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        //before we advance to the reschedule flow, we first must grab the pre-reschedule info
                        mSelectedBooking = ((BookingListItem) v.getParent()
                                                               .getParent()).getBooking();
                        getPreRescheduleInfo();
                    }
                },
                mConfigurationManager.getPersistentConfiguration()
                                     .isBookingHoursClarificationExperimentEnabled()
        );
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onBookingsRequestError() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void getPreRescheduleInfo() {
        mProgressDialog.setMessage(getString(R.string.rescheduling));
        mProgressDialog.show();
        mDataManager.getPreRescheduleInfo(
                mSelectedBooking.getId(),
                new PreRescheduleCallback(this)
        );
    }

    public void onReceivePreRescheduleInfoSuccess(String notice) {
        mProgressDialog.dismiss();

        mBus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleBookingSelectedLog(
                mProviderId,
                mSelectedBooking.getId(),
                String.valueOf(mSelectedBooking.getRecurringId())
        )));

        final Intent intent = new Intent(this, BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mSelectedBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.FROM_CHAT);
        intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    public void onRescheduleRequestError() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.reschedule_try_again, Toast.LENGTH_SHORT).show();
    }

    private static class PreRescheduleCallback implements DataManager.Callback<String> {

        private final WeakReference<RescheduleUpcomingActivity> mActivity;

        public PreRescheduleCallback(RescheduleUpcomingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final String response) {
            if (mActivity.get() != null) {
                mActivity.get().onReceivePreRescheduleInfoSuccess(response);
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (mActivity.get() != null) {
                mActivity.get().onRescheduleRequestError();
            }
        }
    }


    private static class BookingsCallback
            extends Retrofit2ActivitySafeCallback<UserBookingsWrapper, RescheduleUpcomingActivity> {

        public BookingsCallback(RescheduleUpcomingActivity activity) {
            super(activity);
        }

        @Override
        public void onSuccess(final UserBookingsWrapper response) {
            mActivityWeakReference.get().onBookingReceived(response.getBookings());

        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            mActivityWeakReference.get().onBookingsRequestError();
        }
    }
}
