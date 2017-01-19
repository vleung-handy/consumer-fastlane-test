package com.handybook.handybook.core.ui.view;

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
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.library.util.UiUtils;
import com.handybook.handybook.booking.util.BookingUtil;

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
    public LinearLayout planContainer;

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
        planContainer.setVisibility(View.VISIBLE);
        mButtonExpand.setVisibility(View.GONE);
        mImageCollapse.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);

        UiUtils.extendTouchArea(mHeaderContainer, mImageCollapse, UiUtils.SERVICE_ICON_TOUCH_PADDING);
    }

    @OnClick(R.id.image_plan_collapse)
    public void collapse()
    {
        planContainer.setVisibility(View.GONE);
        mButtonExpand.setVisibility(View.VISIBLE);
        mImageCollapse.setVisibility(View.GONE);
        mDivider.setVisibility(View.GONE);
    }

    public void bind(
            final View.OnClickListener clickListener,
            final List<RecurringBooking> recurringBookings,
            final String activePlanCountTitle
    )
    {
        mTextView.setText(activePlanCountTitle);
        planContainer.removeAllViews();
        for (int i = 0; i < recurringBookings.size(); i++)
        {
            RecurringBooking recurringBooking = recurringBookings.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_cleaning_plan_item, planContainer, false);
            view.setTag(recurringBooking);

            Button editButton = (Button) view.findViewById(R.id.button_edit);
            editButton.setTag(recurringBooking);
            TextView title = (TextView) view.findViewById(R.id.text_plan_title);
            TextView subTitle = (TextView) view.findViewById(R.id.text_plan_subtitle);

            title.setText(StringUtils.capitalizeFirstCharacter(recurringBooking.getRecurringStringShort()));
            subTitle.setText(BookingUtil.getRecurrenceSubTitle(recurringBooking));

            editButton.setOnClickListener(clickListener);
            view.setOnClickListener(clickListener);

            planContainer.addView(view);

            if (i < recurringBookings.size() - 1)
            {
                //since there are more bookings to come, add a divider
                View divider = LayoutInflater.from(getContext()).inflate(R.layout.layout_divider, planContainer, false);
                planContainer.addView(divider);
            }
        }

    }
}