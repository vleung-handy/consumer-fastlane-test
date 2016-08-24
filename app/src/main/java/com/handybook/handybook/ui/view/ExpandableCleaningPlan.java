package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.UserRecurringBooking;
import com.handybook.handybook.util.BookingUtil;
import com.handybook.handybook.util.StringUtils;
import com.handybook.handybook.util.UiUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Use with R.layout.layout_cleaning_plan
 */
public class ExpandableCleaningPlan extends LinearLayout
{

    @Bind(R.id.text_view_title)
    TextView mTextView;

    @Bind(R.id.container_plan)
    LinearLayout mPlanContainer;

    @Bind(R.id.image_plan_collapse)
    ImageView mImageCollapse;

    @Bind(R.id.button_plan_expand)
    Button mButtonExpand;

    @Bind(R.id.divider)
    View mDivider;

    @Bind(R.id.header_container)
    FrameLayout mHeaderContainer;

    public ExpandableCleaningPlan(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        inflate(getContext(), R.layout.layout_cleaning_plan, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_plan_expand)
    public void expand()
    {
        mPlanContainer.setVisibility(View.VISIBLE);
        mButtonExpand.setVisibility(View.GONE);
        mImageCollapse.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);

        UiUtils.extendTouchArea(mHeaderContainer, mImageCollapse, UiUtils.SERVICE_ICON_TOUCH_PADDING);
    }

    @OnClick(R.id.image_plan_collapse)
    public void collapse()
    {
        mPlanContainer.setVisibility(View.GONE);
        mButtonExpand.setVisibility(View.VISIBLE);
        mImageCollapse.setVisibility(View.GONE);
        mDivider.setVisibility(View.GONE);
    }

    public void bind(
            final View.OnClickListener clickListener,
            final List<UserRecurringBooking> recurringBookings,
            final String activePlanCountTitle
    )
    {
        mTextView.setText(activePlanCountTitle);
        mPlanContainer.removeAllViews();
        for (int i = 0; i < recurringBookings.size(); i++)
        {
            UserRecurringBooking recurringBooking = recurringBookings.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_cleaning_plan_item, mPlanContainer, false);
            view.setTag(recurringBooking);

            Button editButton = (Button) view.findViewById(R.id.button_edit);
            editButton.setTag(recurringBooking);
            TextView title = (TextView) view.findViewById(R.id.text_plan_title);
            TextView subTitle = (TextView) view.findViewById(R.id.text_plan_subtitle);

            title.setText(StringUtils.capitalizeFirstCharacter(recurringBooking.getRecurringStringShort()));
            subTitle.setText(BookingUtil.getRecurrenceSubTitle(recurringBooking));

            editButton.setOnClickListener(clickListener);
            view.setOnClickListener(clickListener);

            mPlanContainer.addView(view);

            if (i < recurringBookings.size() - 1)
            {
                //since there are more bookings to come, add a divider
                View divider = LayoutInflater.from(getContext()).inflate(R.layout.layout_divider, mPlanContainer, false);
                mPlanContainer.addView(divider);
            }
        }

    }
}
