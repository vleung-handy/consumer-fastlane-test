package com.handybook.handybook.account.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.RequestCode;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.EditPlanLog;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPlanFragment extends InjectedFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.edit_plan_frequency_text)
    TextView mFrequencyText;
    @Bind(R.id.edit_plan_address_text)
    TextView mAddressText;
    @Bind(R.id.edit_plan_next_cleaning_time_text)
    TextView mNextCleaningTimeText;

    @Bind(R.id.edit_plan_hours_container)
    ViewGroup mEditHoursContainer;
    @Bind(R.id.edit_plan_hours_text)
    TextView mPlanHoursText;
    @Bind(R.id.edit_plan_hours_title)
    TextView mPlanHoursTitle;
    @Bind(R.id.edit_plan_hours_subtext)
    TextView mPlanHoursSubtext;

    @Bind(R.id.edit_plan_extras_container)
    ViewGroup mEditExtrasContainer;
    @Bind(R.id.edit_plan_extras_title)
    TextView mPlanExtrasTitle;
    @Bind(R.id.edit_plan_extras_text)
    TextView mPlanExtrasText;
    @Bind(R.id.edit_plan_extras_subtext)
    TextView mPlanExtrasSubtext;

    private RecurringBooking mPlan;

    @NonNull
    public static EditPlanFragment newInstance(RecurringBooking plan) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.RECURRING_PLAN, plan);
        EditPlanFragment fragment = new EditPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlan = (RecurringBooking) getArguments().getSerializable(BundleKeys.RECURRING_PLAN);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = LayoutInflater.from(getContext())
                                  .inflate(R.layout.fragment_plan_edit, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.account_your_plan));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUi();
        bus.post(new LogEvent.AddLogEvent(
                new EditPlanLog.Shown(mPlan.getId(), mPlan.canEditHours(), mPlan.canEditExtras())));
    }

    @OnClick(R.id.edit_plan_frequency)
    public void editFrequency() {
        bus.post(new LogEvent.AddLogEvent(
                new EditPlanLog.EditFrequencyTapped(mPlan.getId(), mPlan.getFrequencyValue())));

        Intent intent = new Intent(getContext(), EditPlanFrequencyActivity.class);
        intent.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        startActivityForResult(intent, RequestCode.EDIT_PLAN_FREQUENCY);
    }

    @OnClick(R.id.edit_plan_address)
    public void editAddress() {
        bus.post(new LogEvent.AddLogEvent(new EditPlanLog.EditAddressTapped(mPlan.getId())));

        Intent intent = new Intent(getContext(), EditPlanAddressActivity.class);
        intent.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        startActivityForResult(intent, RequestCode.EDIT_PLAN_ADDRESS);
    }

    @OnClick(R.id.edit_plan_hours_container)
    public void editHours() {
        if (!mPlan.canEditHours()) { return;}
        bus.post(new LogEvent.AddLogEvent(new EditPlanLog.EditHoursSelected(
                mPlan.getId(),
                mPlan.getHours()
        )));

        Intent intent = new Intent(getContext(), EditPlanHoursActivity.class);
        intent.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        startActivityForResult(intent, RequestCode.EDIT_PLAN_HOURS);
    }

    @OnClick(R.id.edit_plan_extras_container)
    public void editExtras() {
        if (!mPlan.canEditExtras()) { return;}
        bus.post(new LogEvent.AddLogEvent(new EditPlanLog.EditExtrasSelected(
                mPlan.getId(),
                mPlan.getExtrasLabels()
        )));
        Intent intent = new Intent(getContext(), EditPlanExtrasActivity.class);
        intent.putExtra(BundleKeys.RECURRING_PLAN, mPlan);
        startActivityForResult(intent, RequestCode.EDIT_PLAN_EXTRAS);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.EDIT_PLAN_ADDRESS:
                case RequestCode.EDIT_PLAN_FREQUENCY:
                case RequestCode.EDIT_PLAN_HOURS:
                case RequestCode.EDIT_PLAN_EXTRAS:
                    mPlan = (RecurringBooking) data.getSerializableExtra(BundleKeys.RECURRING_PLAN);
                    initUi();
                    break;
            }
        }
    }

    @OnClick(R.id.edit_plan_cancel)
    public void cancelPlan() {
        bus.post(new LogEvent.AddLogEvent(new EditPlanLog.CancelPlanTapped(mPlan.getId())));
        WebViewFragment fragment = WebViewFragment
                .newInstance(mPlan.getCancelUrl(), getString(R.string.account_cancel_plan));
        FragmentUtils.switchToFragment(this, fragment, true);
    }

    private void initUi() {
        mFrequencyText.setText(mPlan.getFrequency());
        Booking.Address address = mPlan.getAddress();
        if (address != null) {
            mAddressText.setText(address.toString());
        }
        else {
            mAddressText.setText(mPlan.getFullAddress());
        }

        mPlanHoursText.setText(getString(
                R.string.template_x_hours,
                new DecimalFormat("#.#").format(mPlan.getHours()
                )
        ));
        mNextCleaningTimeText.setText(DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER.format(
                mPlan.getNextBookingDate()));
        initEditHours();
        initEditExtras();
    }

    private void initEditHours() {
        mPlanHoursTitle.setTextColor(getResources().getColor(
                mPlan.canEditHours() ? R.color.handy_text_black : R.color.handy_text_gray
        ));
        mEditHoursContainer.setClickable(mPlan.canEditHours());
        final String editHoursSubtext = mPlan.getEditHoursSubtext();
        mPlanHoursSubtext.setVisibility(editHoursSubtext == null ? View.GONE : View.VISIBLE);
        mPlanHoursSubtext.setText(editHoursSubtext);
    }

    private void initEditExtras() {
        mPlanExtrasTitle.setTextColor(getResources().getColor(
                mPlan.canEditHours() ? R.color.handy_text_black : R.color.handy_text_gray
        ));
        mEditExtrasContainer.setClickable(mPlan.canEditExtras());
        mPlanExtrasText.setText(TextUtils.join(", ", mPlan.getExtrasLabels()));
        final String editExtrasSubtext = mPlan.getEditExtrasSubtext();
        mPlanExtrasSubtext.setVisibility(editExtrasSubtext == null ? View.GONE : View.VISIBLE);
        mPlanExtrasSubtext.setText(editExtrasSubtext);
    }
}
