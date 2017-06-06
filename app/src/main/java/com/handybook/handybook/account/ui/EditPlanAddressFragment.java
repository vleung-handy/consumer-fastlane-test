package com.handybook.handybook.account.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.account.model.RecurringPlanWrapper;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.widget.CityInputTextView;
import com.handybook.handybook.core.ui.widget.StateInputTextView;
import com.handybook.handybook.core.ui.widget.StreetAddressInputTextView;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.UiUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.EditAddressLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class EditPlanAddressFragment extends ProgressSpinnerFragment {

    @Bind(R.id.plan_address_street_addr_text)
    StreetAddressInputTextView mStreetAddressText;
    @Bind(R.id.plan_address_apt_addr_text)
    EditText mAptAddressText;
    @Bind(R.id.plan_address_city_text)
    CityInputTextView mCityText;
    @Bind(R.id.plan_address_state_text)
    StateInputTextView mStateText;
    @Bind(R.id.plan_address_zip_text)
    ZipCodeInputTextView mZipCodeText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private RecurringBooking mPlan;

    @NonNull
    public static EditPlanAddressFragment newInstance(RecurringBooking plan) {
        final EditPlanAddressFragment fragment = new EditPlanAddressFragment();
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
        view.addView(inflater.inflate(R.layout.fragment_plan_edit_address, container, false));

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.booking_edit_address_title));

        if (mPlan.getAddress() != null) {
            //initialize with the booking's current address
            mStreetAddressText.setText(mPlan.getAddress().getAddress1());
            mAptAddressText.setText(mPlan.getAddress().getAddress2());
            mZipCodeText.setText(mPlan.getAddress().getZip());
            mCityText.setText(mPlan.getAddress().getCity());
            mStateText.setText(mPlan.getAddress().getState());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new LogEvent.AddLogEvent(new EditAddressLog.Shown()));

    }

    @OnClick(R.id.plan_address_update_button)
    public void updateAddress() {
        if (validateFields()) {
            bus.post(new LogEvent.AddLogEvent(new EditAddressLog.Submitted()));
            sendEditAddressRequest();
        }
    }

    private void sendEditAddressRequest() {
        final EditAddressRequest request = new EditAddressRequest(
                mStreetAddressText.getAddress(),
                mAptAddressText.getText().toString(),
                mZipCodeText.getZipCode(),
                mCityText.getCity(),
                mStateText.getState()
        );
        showProgressSpinner(true);
        UiUtils.dismissKeyboard(getActivity());
        dataManager.editBookingPlanAddress(
                // TODO: use plan manager instead (oh... no plan manager? Write it then!)
                mPlan.getId(),
                request,
                new FragmentSafeCallback<RecurringPlanWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final RecurringPlanWrapper response) {
                        bus.post(new LogEvent.AddLogEvent(new EditAddressLog.Success()));
                        onReceiveEditBookingAddressSuccess(response);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new LogEvent.AddLogEvent(new EditAddressLog.Error()));
                        onReceiveEditBookingAddressError(error);
                    }
                }
        );
    }

    private boolean validateFields() {
        return (mStreetAddressText.validate() && mZipCodeText.validate());
    }

    private void onReceiveEditBookingAddressSuccess(RecurringPlanWrapper planWrapper) {
        hideProgressSpinner();
        mPlan.setAddress(planWrapper.getRecurringBooking().getAddress());
        showToast(getString(R.string.account_update_plan_address_success));

        Intent data = new Intent();
        data.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().onBackPressed();
    }

    private void onReceiveEditBookingAddressError(DataManager.DataManagerError error) {
        hideProgressSpinner();
        showToast(getString(R.string.account_update_plan_address_error));
    }

}
