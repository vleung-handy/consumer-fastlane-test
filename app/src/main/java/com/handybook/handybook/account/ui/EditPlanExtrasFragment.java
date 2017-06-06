package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class EditPlanExtrasFragment extends ProgressSpinnerFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private RecurringBooking mPlan;
    private BookingEditExtrasViewModel mBookingEditHoursViewModel;

    @NonNull
    public static EditPlanExtrasFragment newInstance(RecurringBooking plan) {
        final EditPlanExtrasFragment fragment = new EditPlanExtrasFragment();
        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.RECURRING_PLAN, plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlan = (RecurringBooking) getArguments().getSerializable(BundleKeys.RECURRING_PLAN);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_plan_edit_extras, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.edit_plan_edit_extras_title));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataManager.getRecurringExtrasInfo(
                // TODO: use plan manager instead (oh... no plan manager? Write it then!)
                mPlan.getId(),
                new FragmentSafeCallback<BookingEditExtrasInfoResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingEditExtrasInfoResponse response) {
                        hideProgressSpinner();
                        mBookingEditHoursViewModel = BookingEditExtrasViewModel.from(response);
                        //initOptionsView(); // If this isn't obvious.. this is not a functional fragment
                        //updateUiForOptionSelected(); // It is not reachable from code, don't worry about it
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        hideProgressSpinner();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );

    }
}
