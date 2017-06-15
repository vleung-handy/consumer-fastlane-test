package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.activity.BookingProTeamRescheduleActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Displayed as confirmation when the user selects a recurring series to cancel
 */
public class RescheduleDialogFragment extends BaseDialogFragment {

    private ProTeam.ProTeamCategory mCategory;
    private Booking mBooking;

    public static RescheduleDialogFragment newInstance(
            final ProTeam.ProTeamCategory category,
            final Booking booking
    ) {
        RescheduleDialogFragment fragment = new RescheduleDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, category);
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getParcelable(BundleKeys.PRO_TEAM_CATEGORY);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_reschedule, container, true);
        ButterKnife.bind(this, view);
        getDialog().setCanceledOnTouchOutside(true);

        return view;
    }

    @OnClick(R.id.reschedule_button)
    public void onRescheduleButtonClicked() {
        Intent intent = new Intent(getContext(), BookingProTeamRescheduleActivity.class);
        intent.putExtra(BundleKeys.PRO_TEAM_CATEGORY, mCategory);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
        dismiss();
    }
}
