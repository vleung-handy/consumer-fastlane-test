package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class EditPlanHoursFragment extends InjectedFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private RecurringBooking mPlan;

    @NonNull
    public static EditPlanHoursFragment newInstance(RecurringBooking plan) {
        final EditPlanHoursFragment fragment = new EditPlanHoursFragment();
        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.RECURRING_PLAN, plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_plan_edit_hours, container, false);
        ButterKnife.bind(this, view);
        mPlan = (RecurringBooking) getArguments().getSerializable(BundleKeys.RECURRING_PLAN);
        setupToolbar(mToolbar, getString(R.string.edit_plan_edit_hours_title));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showUiBlockers();
        dataManager.getRecurringHoursInfo(
                mPlan.getId(),
                new FragmentSafeCallback<BookingEditHoursInfoResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingEditHoursInfoResponse response) {
                        removeUiBlockers();
                        showToast("Received edit hours info");
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

}
