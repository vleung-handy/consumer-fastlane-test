package com.handybook.handybook.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MiniProProfile extends FrameLayout
{
    @Bind(R.id.mini_pro_profile_title)
    TextView mTitle;
    @Bind(R.id.mini_pro_profile_rating_and_jobs_count_container)
    ViewGroup mRatingAndJobsCountContainer;
    @Bind(R.id.mini_pro_profile_rating)
    TextView mRating;
    @Bind(R.id.mini_pro_profile_jobs_count)
    TextView mJobsCount;
    @Bind(R.id.mini_pro_profile_image_container)
    ViewGroup mImageContainer;
    @Bind(R.id.mini_pro_profile_image)
    ImageView mImage;
    @Bind(R.id.mini_pro_profile_pro_team_indicator_image)
    View mProTeamIndicatorImage;
    @Bind(R.id.mini_pro_profile_pro_team_indicator_name)
    View mProTeamIndicatorName;

    private boolean mIsProTeam;
    private boolean mIsProTeamIndicatorEnabled;

    public MiniProProfile(final Context context)
    {
        super(context);
        init();
    }

    public MiniProProfile(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MiniProProfile(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_mini_pro_profile, this);
        ButterKnife.bind(this);
    }

    public void setTitle(final String title)
    {
        mTitle.setText(title);
    }

    public void setProTeamIndicatorEnabled(final boolean proTeamIndicatorEnabled)
    {
        mIsProTeamIndicatorEnabled = proTeamIndicatorEnabled;
        updateProTeamIndicator();
    }

    public void setRatingAndJobsCount(final Float rating, final Integer jobsCount)
    {
        if (rating != null && rating > 0.0f && jobsCount != null && jobsCount > 0)
        {
            mRatingAndJobsCountContainer.setVisibility(VISIBLE);

            final DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            mRating.setText(format.format(rating));

            mJobsCount.setText(
                    getResources().getQuantityString(R.plurals.jobs_count, jobsCount, jobsCount));
        }
    }

    public void setImage(@Nullable final String imageUrl)
    {
        mImageContainer.setVisibility(VISIBLE);
        if (imageUrl != null)
        {
            Picasso.with(getContext())
                   .load(imageUrl)
                   .placeholder(R.drawable.img_pro_placeholder)
                   .noFade()
                   .into(mImage);
        }
        else
        {
            mImage.setImageResource(R.drawable.img_pro_placeholder);
        }
        updateProTeamIndicator();
    }

    public void setIsProTeam(final boolean isProTeam)
    {
        mIsProTeam = isProTeam;
        updateProTeamIndicator();
    }

    private void updateProTeamIndicator()
    {
        mProTeamIndicatorImage.setVisibility(GONE);
        mProTeamIndicatorName.setVisibility(GONE);
        if (mIsProTeam && mIsProTeamIndicatorEnabled)
        {
            if (mImageContainer.getVisibility() == VISIBLE)
            {
                mProTeamIndicatorImage.setVisibility(VISIBLE);
            }
            else
            {
                mProTeamIndicatorName.setVisibility(VISIBLE);
            }
        }

    }
}
