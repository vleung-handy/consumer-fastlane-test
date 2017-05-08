package com.handybook.handybook.proteam.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.view.SimpleDividerItemDecoration;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingProTeamRescheduleFragment extends InjectedFragment {

    @Inject
    ProTeamManager mProTeamManager;

    @Inject
    BookingManager mBookingManager;

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pro_team_recycler_view)
    RecyclerView mRecyclerView;

    private ProTeam.ProTeamCategory mProTeamCategory;
    private ProTeamProViewModel mSelectedProTeamMember;
    private ProRescheduleAdapter mAdapter;
    private Booking mBooking;

    public static BookingProTeamRescheduleFragment newInstance(
            ProTeam.ProTeamCategory category,
            Booking booking
    ) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, category);
        args.putParcelable(BundleKeys.BOOKING, booking);

        BookingProTeamRescheduleFragment fragment = new BookingProTeamRescheduleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProTeamCategory = getArguments().getParcelable(BundleKeys.PRO_TEAM_CATEGORY);
            mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        }
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_booking_pro_team_conversations,
                container,
                false
        );
        ButterKnife.bind(this, view);

        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleSelectedProShown(
                mProTeamCategory.getPreferred().size())));
    }

    private void initRecyclerView() {
        if (mRecyclerView == null || mProTeamCategory == null) { return; }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        mAdapter = new ProRescheduleAdapter(
                mProTeamCategory,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int position = mRecyclerView.getChildAdapterPosition(v);

                        mSelectedProTeamMember = mAdapter.getItem(position);

                        showUiBlockers();
                        //Go to date picker
                        mBookingManager.rescheduleBookingWithProAvailability(
                                mSelectedProTeamMember.getProTeamPro().getId(),
                                mBooking,
                                null
                        );
                    }
                }
        );

        mAdapter.setAssignedProviderId(mBooking.getProvider().getId());

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.choose_a_pro));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
            if (requestCode == ActivityResult.START_RESCHEDULE) {
                getActivity().finish();
            }
        }
    }

    @Subscribe
    public void onRescheduleWithAvailabilitySuccess(BookingEvent.RescheduleBookingWithProAvailabilitySuccess success) {
        removeUiBlockers();

        //todo new logging
//        mBus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleBookingSelectedLog(
//                mProMessageViewModel.getProviderId(),
//                mBooking.getId(),
//                String.valueOf(mBooking.getRecurringId())
//        )));

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, success.getNotice());
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.FROM_CHAT);
        intent.putExtra(BundleKeys.PROVIDER_ID, mSelectedProTeamMember.getProTeamPro().getId());
        intent.putExtra(BundleKeys.PRO_TEAM_PRO, mSelectedProTeamMember);
        intent.putExtra(BundleKeys.PRO_AVAILABILITY, success.getProAvailability());
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }
}
