package com.handybook.handybook.module.bookings;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Use with R.layout.layout_cleaning_plan
 */
public class CleaningPlanHolder extends RecyclerView.ViewHolder
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            if (v.getTag() != null)
            {
                RecurringBooking rb = (RecurringBooking) v.getTag();
                Toast.makeText(v.getContext(), "Edit Clicked for plan " + rb.getId(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public CleaningPlanHolder(final View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.button_plan_expand)
    public void expand()
    {
        mPlanContainer.setVisibility(View.VISIBLE);
        mButtonExpand.setVisibility(View.GONE);
        mImageCollapse.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.image_plan_collapse)
    public void collapse()
    {
        mPlanContainer.setVisibility(View.GONE);
        mImageCollapse.setVisibility(View.GONE);
        mButtonExpand.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.GONE);
    }

    public void bind(final List<RecurringBooking> recurringBookings, final String activePlanCountTitle)
    {
        mTextView.setText(activePlanCountTitle);
        if (recurringBookings.size() != mPlanContainer.getChildCount())
        {
            mPlanContainer.removeAllViews();
            for (final RecurringBooking recurringBooking : recurringBookings)
            {
                View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_cleaning_plan_item, mPlanContainer, false);
                view.setTag(recurringBooking);

                Button editButton = (Button) view.findViewById(R.id.button_edit);
                editButton.setTag(recurringBooking);
                TextView title = (TextView) view.findViewById(R.id.text_plan_title);
                TextView subTitle = (TextView) view.findViewById(R.id.text_plan_subtitle);

                title.setText(getTitle(recurringBooking));
                subTitle.setText(getSubTitle(recurringBooking));

                editButton.setOnClickListener(mOnClickListener);
                view.setOnClickListener(mOnClickListener);

                mPlanContainer.addView(view);
            }
        }
    }

    private String getTitle(RecurringBooking booking)
    {
        //FIXME: JIA: update this
        return "Monthly Home Cleaning";
    }

    private String getSubTitle(RecurringBooking booking)
    {
        //FIXME: JIA: update this
        return "Monday's @ 2pm - 3 hours.";
    }
}
