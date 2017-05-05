package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.activity.BookingProTeamRescheduleActivity;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingReschedulePreferencesFragment extends InjectedFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ProTeam.ProTeamCategory mProTeamCategory;
    private Booking mBooking;

    public static BookingReschedulePreferencesFragment newInstance(
            final ProTeam.ProTeamCategory category,
            final Booking booking
    ) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, category);
        arguments.putParcelable(BundleKeys.BOOKING, booking);

        final BookingReschedulePreferencesFragment fragment
                = new BookingReschedulePreferencesFragment();
        fragment.setArguments(arguments);

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
                R.layout.fragment_booking_reschedule_preferences,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.reschedule));
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

    @OnClick(R.id.choose_time_option)
    public void onChooseTimeOptionClicked() {
        showUiBlockers();

        bus.post(new BookingEvent.RequestPreRescheduleInfo(mBooking.getId()));
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleIndifferenceSelected()));
    }

    @OnClick(R.id.choose_pro_option)
    public void onChooseProOptionClicked() {
        Intent intent = new Intent(getContext(), BookingProTeamRescheduleActivity.class);
        intent.putExtra(BundleKeys.PRO_TEAM_CATEGORY, mProTeamCategory);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(BookingEvent.ReceivePreRescheduleInfoSuccess event) {
        removeUiBlockers();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.NORMAL);

        startActivityForResult(intent, ActivityResult.START_RESCHEDULE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(BookingEvent.ReceivePreRescheduleInfoError event) {
        removeUiBlockers();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
