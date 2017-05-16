package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.widget.CityInputTextView;
import com.handybook.handybook.core.ui.widget.StateInputTextView;
import com.handybook.handybook.core.ui.widget.StreetAddressInputTextView;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class EditPlanExtrasFragment extends InjectedFragment {

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
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_plan_edit_extras, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.edit_plan_edit_extras_title));
        return view;
    }

}
