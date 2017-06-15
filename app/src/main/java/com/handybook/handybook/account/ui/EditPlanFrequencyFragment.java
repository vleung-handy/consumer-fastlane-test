package com.handybook.handybook.account.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.constant.BookingRecurrence;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.EditPlanFrequencyLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class EditPlanFrequencyFragment extends ProgressSpinnerFragment {

    @BindView(R.id.plan_frequency_options_layout)
    FrequencySelectionsView mFrequencySelectionsView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private RecurringBooking mPlan;
    private BookingEditFrequencyInfoResponse mFrequencyInfo;

    public static EditPlanFrequencyFragment newInstance(RecurringBooking plan) {
        final EditPlanFrequencyFragment fragment = new EditPlanFrequencyFragment();
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
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_plan_edit_frequency, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.edit_plan_edit_frequency_title));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog
                .Shown(mPlan.getId(), mPlan.getFrequencyValue())));

        showProgressSpinner(true);

        dataManager.getRecurringFrequency(
                // TODO: use plan manager instead (oh... no plan manager? Write it then!)
                Integer.toString(mPlan.getId()),
                new FragmentSafeCallback<BookingEditFrequencyInfoResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingEditFrequencyInfoResponse response) {
                        mFrequencyInfo = response;
                        hideProgressSpinner();
                        setupDisplay();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        hideProgressSpinner();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    private void setupDisplay() {
        mFrequencySelectionsView.addOption(
                BookingRecurrence.WEEKLY,
                mFrequencyInfo.getWeeklyPriceFormatted(),
                mPlan.getFrequencyValue() == BookingRecurrence.WEEKLY
        );
        mFrequencySelectionsView.addOption(
                BookingRecurrence.BIWEEKLY,
                mFrequencyInfo.getBimonthlyPriceFormatted(),
                mPlan.getFrequencyValue() == BookingRecurrence.BIWEEKLY
        );
        mFrequencySelectionsView.addOption(
                BookingRecurrence.MONTHLY,
                mFrequencyInfo.getMonthlyPriceFormatted(),
                mPlan.getFrequencyValue() == BookingRecurrence.MONTHLY
        );
    }

    @OnClick(R.id.plan_frequency_update_button)
    public void updateFrequency() {
        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog.Submitted(
                mPlan.getId(),
                mFrequencyInfo.getCurrentFrequency(),
                mFrequencySelectionsView.getCurrentlySelectedFrequency()
        )));
        showProgressSpinner(true);
        BookingEditFrequencyRequest editFrequencyRequest = new BookingEditFrequencyRequest();
        editFrequencyRequest.setRecurringFrequency(mFrequencySelectionsView.getCurrentlySelectedFrequency());
        dataManager.updateRecurringFrequency(
                // TODO: use plan manager instead (oh... no plan manager? Write it then!)
                Integer.toString(mPlan.getId()),
                editFrequencyRequest,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog.Success(
                                mPlan.getId(),
                                mFrequencyInfo.getCurrentFrequency(),
                                mPlan.getFrequencyValue()
                        )));
                        updateSuccess();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog.Error(
                                mPlan.getId(),
                                mFrequencyInfo.getCurrentFrequency(),
                                mFrequencySelectionsView.getCurrentlySelectedFrequency()
                        )));
                        updateError(error);
                    }
                }
        );
    }

    private void updateSuccess() {
        hideProgressSpinner();
        showToast(R.string.updated_booking_frequency);
        mPlan.setFrequency(StringUtils.getFrequencyText(
                getContext(), mFrequencySelectionsView.getCurrentlySelectedFrequency()));
        mPlan.setFrequencyValue(mFrequencySelectionsView.getCurrentlySelectedFrequency());

        Intent data = new Intent();
        data.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().onBackPressed();
    }

    private void updateError(DataManager.DataManagerError error) {
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), error);
    }
}
