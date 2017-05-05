package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.proteam.model.ProTeam;

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
        mToolbar.setTitle(R.string.reschedule);
        return view;
    }

    @OnClick(R.id.choose_time_option)
    public void onChooseTimeOptionClicked() {

    }

    @OnClick(R.id.choose_pro_option)
    public void onChooseProOptionClicked() {

    }
}
