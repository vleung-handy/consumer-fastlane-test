package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.PlanSelectionLog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlansFragment extends InjectedFragment {

    @Bind(R.id.plans_layout)
    LinearLayout mPlansLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ArrayList<RecurringBooking> mPlans;

    public static PlansFragment newInstance(ArrayList<RecurringBooking> plans) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.RECURRING_PLANS, plans);
        PlansFragment fragment = new PlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlans
                = (ArrayList<RecurringBooking>) getArguments().getSerializable(BundleKeys.RECURRING_PLANS);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = LayoutInflater.from(getContext())
                                  .inflate(R.layout.fragment_plans, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.account_select_plan));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDisplay();
        int[] ids = new int[mPlans.size()];
        for (int i = 0; i < ids.length; ++i) {
            ids[i] = mPlans.get(i).getId();
        }
        bus.post(new LogEvent.AddLogEvent(new PlanSelectionLog.Shown(ids)));
    }

    private void setupDisplay() {
        mPlansLayout.removeAllViews();
        for (final RecurringBooking plan : mPlans) {
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_cleaning_plan_item, mPlansLayout, false);
            view.setTag(plan);
            view.setBackgroundResource(R.drawable.background_list_entry);
            view.findViewById(R.id.button_edit).setVisibility(View.GONE);
            TextView title = (TextView) view.findViewById(R.id.text_plan_title);
            TextView subTitle = (TextView) view.findViewById(R.id.text_plan_subtitle);

            title.setText(StringUtils.capitalizeFirstCharacter(plan.getFrequency()));
            subTitle.setText(DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER.format(plan.getNextBookingDate()));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    bus.post(new LogEvent.AddLogEvent(new PlanSelectionLog.PlanTapped(plan.getId())));
                    EditPlanFragment fragment = EditPlanFragment.newInstance(plan);
                    FragmentUtils.switchToFragment(PlansFragment.this, fragment, true);
                }
            });

            mPlansLayout.addView(view);
        }
    }
}
