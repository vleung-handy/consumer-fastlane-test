package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.EditPlanFrequencyLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class EditPlanFrequencyFragment extends InjectedFragment
{
    @Bind(R.id.plan_frequency_options_layout)
    FrequencySelectionsView mFrequencySelectionsView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private RecurringBooking mPlan;
    private BookingEditFrequencyInfoResponse mFrequencyInfo;

    public static EditPlanFrequencyFragment newInstance(RecurringBooking plan)
    {
        final EditPlanFrequencyFragment fragment = new EditPlanFrequencyFragment();
        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.RECURRING_PLAN, plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPlan = (RecurringBooking) getArguments().getSerializable(BundleKeys.RECURRING_PLAN);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater
                .inflate(R.layout.fragment_plan_edit_frequency, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.edit_frequency));

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog
                .Shown(mPlan.getId(), mFrequencyInfo.getCurrentFrequency())));

        showUiBlockers();

        dataManager.getRecurringFrequency(
                Integer.toString(mPlan.getId()),
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
                {
                    @Override
                    public void onSuccess(final BookingEditFrequencyInfoResponse response)
                    {
                        mFrequencyInfo = response;
                        removeUiBlockers();
                        setupDisplay();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    private void setupDisplay()
    {
        mFrequencySelectionsView.addOption(
                1,
                mFrequencyInfo.getWeeklyPriceFormatted(),
                mFrequencyInfo.getCurrentFrequency() == 1
        );
        mFrequencySelectionsView.addOption(
                2,
                mFrequencyInfo.getBimonthlyPriceFormatted(),
                mFrequencyInfo.getCurrentFrequency() == 2
        );
        mFrequencySelectionsView.addOption(
                4,
                mFrequencyInfo.getMonthlyPriceFormatted(),
                mFrequencyInfo.getCurrentFrequency() == 4
        );
    }

    @OnClick(R.id.plan_frequency_update_button)
    public void updateFrequency()
    {
        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog.Submitted(
                mPlan.getId(),
                mFrequencyInfo.getCurrentFrequency(),
                mFrequencySelectionsView.getCurrentlySelectedFrequency()
        )));
        showUiBlockers();
        BookingEditFrequencyRequest editFrequencyRequest = new BookingEditFrequencyRequest();
        editFrequencyRequest.setRecurringFrequency(mFrequencySelectionsView.getCurrentlySelectedFrequency());
        dataManager.updateRecurringFrequency(
                Integer.toString(mPlan.getId()),
                editFrequencyRequest,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        bus.post(new LogEvent.AddLogEvent(new EditPlanFrequencyLog.Success(
                                mPlan.getId(),
                                mFrequencyInfo.getCurrentFrequency(),
                                mFrequencySelectionsView.getCurrentlySelectedFrequency()
                        )));
                        updateSuccess();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
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

    private void updateSuccess()
    {
        removeUiBlockers();
        showToast(R.string.updated_booking_frequency);
        mPlan.setFrequency(StringUtils.getFrequencyText(
                getContext(), mFrequencySelectionsView.getCurrentlySelectedFrequency()));
        getFragmentManager().popBackStack();
    }

    private void updateError(DataManager.DataManagerError error)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), error);
    }
}
