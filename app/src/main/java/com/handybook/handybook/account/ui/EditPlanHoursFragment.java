package com.handybook.handybook.account.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.LabelValueView;
import com.handybook.handybook.logger.handylogger.model.account.EditPlanHoursLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class EditPlanHoursFragment extends InjectedFragment {

    private RecurringBooking mPlan;
    private BookingEditHoursViewModel mBookingEditHoursViewModel;
    private BookingOptionsSpinnerView mOptionsView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.plan_edit_hours_container)
    ViewGroup mContainer;
    @Bind(R.id.plan_edit_hours_progressbar)
    ProgressBar mProgressBar;
    @Bind(R.id.plan_edit_hours_base_time_row)
    LabelValueView mRowBaseTime;
    @Bind(R.id.plan_edit_hours_added_time_row)
    LabelValueView mRowAddedTime;
    @Bind(R.id.plan_edit_hours_extras_time_row)
    LabelValueView mRowExtras;
    @Bind(R.id.plan_edit_hours_total_due_text)
    TextView mTotalDueText;
    @Bind(R.id.plan_edit_hours_booking_duration_text)
    TextView mBookingDurationText;
    @Bind(R.id.plan_edit_hours_options_view_container)
    ViewGroup mOptionsViewContainer;
    @Bind(R.id.plan_edit_hours_billed_on_text)
    TextView mBilledOnText;
    @Bind(R.id.plan_edit_hours_save_button)
    Button mSaveButton;

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
        mPlan = (RecurringBooking) getArguments().getSerializable(BundleKeys.RECURRING_PLAN);
        final View view = inflater.inflate(R.layout.fragment_plan_edit_hours, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.edit_plan_edit_hours_title));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
        dataManager.getRecurringHoursInfo(
                mPlan.getId(),
                new FragmentSafeCallback<BookingEditHoursInfoResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingEditHoursInfoResponse response) {
                        mBookingEditHoursViewModel = BookingEditHoursViewModel.from(response);
                        initUI();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        getActivity().onBackPressed();
                    }
                }
        );
    }

    private void initUI() {
        if (mBookingEditHoursViewModel == null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mContainer.setVisibility(View.GONE);
            mSaveButton.setEnabled(false);
            return;
        }
        mProgressBar.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
        mSaveButton.setEnabled(true);
        initOptionsView();
    }

    /**
     * initializes the option selector view based on the edit hours view model
     */
    private void initOptionsView() {
        bus.post(new EditPlanHoursLog.Shown(
                mPlan.getId(),
                mBookingEditHoursViewModel.getBaseHours()
        ));

        mRowBaseTime.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_base_hours_formatted,
                        mBookingEditHoursViewModel.getBaseHoursFormatted()
                ),
                mBookingEditHoursViewModel.getBasePriceFormatted()
        );
        mRowExtras.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_extra_hours_formatted,
                        mBookingEditHoursViewModel.getExtrasHoursFormatted()
                ),
                getResources().getString(
                        R.string.booking_edit_positive_price_formatted,
                        mBookingEditHoursViewModel.getExtrasPriceFormatted()
                )
        );

        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);
        String[] optionHourStrings = mBookingEditHoursViewModel.getSelectableHoursArray();
        bookingOption.setOptions(optionHourStrings);

        //by default, the selected option will be the # of base hours in the booking
        double baseHours = mBookingEditHoursViewModel.getBaseHours();
        //find out which index in the option hour array should be selected
        int selectedIndex = 0;
        for (int i = 0; i < optionHourStrings.length; i++) {
            if (baseHours == Double.parseDouble(optionHourStrings[i])) {
                selectedIndex = i;
                break;
            }
        }
        bookingOption.setDefaultValue(Integer.toString(selectedIndex));
        //for some reason this function only accepts a string, but then the view converts it to an index?

        mOptionsView = new BookingOptionsSpinnerView(
                getActivity(), bookingOption,
                new BookingOptionsView.OnUpdatedListener() {
                    @Override
                    public void onUpdate(final BookingOptionsView view) {
                        updateUiForOptionSelected();
                    }

                    @Override
                    public void onShowChildren(
                            final BookingOptionsView view,
                            final String[] items
                    ) {
                    }

                    @Override
                    public void onHideChildren(
                            final BookingOptionsView view,
                            final String[] items
                    ) {
                    }
                }
        );

        ((TextView) mOptionsView.findViewById(R.id.title_text)).setText(
                R.string.booking_edit_hours_options_title);

        mOptionsViewContainer.removeAllViews();
        mOptionsViewContainer.addView(mOptionsView);
        updateUiForOptionSelected();

    }

    /**
     * Updates the price details view (which includes "Base time", "Added time", "Total Due", etc) based on the option that the user has selected
     */
    private void updateUiForOptionSelected() {
        float selectedHours = Float.parseFloat(mOptionsView.getCurrentValue());

        String addedHoursFormatted = mBookingEditHoursViewModel
                .getAddedHoursFormatted(selectedHours);
        String addedPriceFormatted = mBookingEditHoursViewModel
                .getAddedHoursPriceFormatted(selectedHours);
        String totalHoursFormatted
                = mBookingEditHoursViewModel.getTotalHoursFormatted(selectedHours);

        mRowAddedTime.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_added_hours_formatted,
                        addedHoursFormatted
                ),
                mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours) ?
                addedPriceFormatted : getResources().getString(
                        R.string.booking_edit_positive_price_formatted,
                        addedPriceFormatted
                )
        );

        mBookingDurationText.setText(
                getResources().getString(
                        R.string.booking_edit_num_hours_formatted,
                        totalHoursFormatted
                ));

        mBilledOnText.setText(getResources().getString(
                R.string.billed_on_date_formatted,
                mBookingEditHoursViewModel.getFutureBillDateFormatted()
        ));
        mBilledOnText.setVisibility(
                mBookingEditHoursViewModel.hasFutureBillDate()
                ? View.VISIBLE
                : View.GONE
        );

        mTotalDueText.setText(mBookingEditHoursViewModel.getTotalDuePriceFormatted(selectedHours));

        TextView warningText = ((TextView) mOptionsView.findViewById(R.id.warning_text));

        //this is the same logic that the web is using to show the edit hours warning message
        if (mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours)) {
            warningText.setVisibility(View.VISIBLE);
            warningText.setText(R.string.booking_edit_hours_options_warning);
        }
        else {
            warningText.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.plan_edit_hours_save_button)
    public void onSaveButtonPressed() {
        showUiBlockers();
        final double selectedHours = Double.parseDouble(mOptionsView.getCurrentValue());
        bus.post(new EditPlanHoursLog.Submitted(mPlan.getId(), selectedHours));

        BookingEditHoursRequest bookingEditHoursRequest = new BookingEditHoursRequest();
        bookingEditHoursRequest.setNewBaseHrs((float) selectedHours);
        bookingEditHoursRequest.setApplyToRecurring(true);
        dataManager.updateRecurringHours(
                mPlan.getId(),
                bookingEditHoursRequest,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        removeUiBlockers();
                        bus.post(new EditPlanHoursLog.Success(mPlan.getId(), selectedHours));
                        mPlan.setHours(selectedHours);
                        showToast(getString(R.string.account_update_plan_hours_success));
                        Intent data = new Intent();
                        data.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        bus.post(new EditPlanHoursLog.Error(mPlan.getId(), selectedHours));
                        showToast(getString(R.string.account_update_plan_hours_error));
                    }
                }
        );
    }

    @Override
    protected void showUiBlockers() {
        super.showUiBlockers();
        mSaveButton.setEnabled(false);
    }

    @Override
    protected void removeUiBlockers() {
        super.removeUiBlockers();
        mSaveButton.setEnabled(true);
    }


}
