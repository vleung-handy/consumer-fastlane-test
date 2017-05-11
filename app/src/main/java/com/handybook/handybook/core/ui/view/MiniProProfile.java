package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
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

public class MiniProProfile extends FrameLayout {

    @Bind(R.id.mini_pro_profile_title)
    TextView mTitleText;
    @Bind(R.id.mini_pro_profile_rating_and_jobs_count_container)
    ViewGroup mRatingAndJobsCountContainer;
    @Bind(R.id.mini_pro_profile_rating)
    TextView mRatingText;
    @Bind(R.id.mini_pro_profile_jobs_count)
    TextView mJobsCountText;
    @Bind(R.id.mini_pro_profile_image_container)
    ViewGroup mProfileImageContainer;
    @Bind(R.id.mini_pro_profile_image)
    ImageView mProfileImage;
    @Bind(R.id.mini_pro_profile_pro_team_indicator_image)
    View mProTeamIndicatorImage;
    @Bind(R.id.mini_pro_profile_pro_team_indicator_name)
    View mProTeamIndicatorName;
    @Bind(R.id.mini_pro_profile_handyman_indicator)
    View mHandymanIndicator;
    @Bind(R.id.mini_pro_profile_no_ratings_indicator)
    View mNoRatingsIndicator;

    private boolean mIsProTeam;
    private boolean mIsProTeamIndicatorEnabled;

    public MiniProProfile(final Context context) {
        super(context);
        init();
    }

    public MiniProProfile(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiniProProfile(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MiniProProfile(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), getLayoutResourceId(), this);
        ButterKnife.bind(this);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.layout_mini_pro_profile;
    }

    public void setTitle(final String title) {
        mTitleText.setText(title);
    }

    public void setProTeamIndicatorEnabled(final boolean proTeamIndicatorEnabled) {
        mIsProTeamIndicatorEnabled = proTeamIndicatorEnabled;
        updateProTeamIndicator();
    }

    public void setHandymanIndicatorEnabled(final boolean handymanIndicatorEnabled) {
        mHandymanIndicator.setVisibility(handymanIndicatorEnabled ? VISIBLE : GONE);
    }

    public void setRatingAndJobsCount(
            @Nullable final Float rating,
            @Nullable final Integer jobsCount
    ) {
        if (rating != null && rating > 0.0f && jobsCount != null && jobsCount > 0) {
            mNoRatingsIndicator.setVisibility(GONE);
            mRatingAndJobsCountContainer.setVisibility(VISIBLE);

            final DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            mRatingText.setText(format.format(rating));

            mJobsCountText.setText(
                    getResources().getQuantityString(R.plurals.jobs_count, jobsCount, jobsCount));
        }
        else {
            /*
            business logic is to show this view when the ratings and jobs count does not show
             */
            mRatingAndJobsCountContainer.setVisibility(GONE);
            mNoRatingsIndicator.setVisibility(VISIBLE);
        }
    }

    public void setImage(@Nullable final String imageUrl) {
        mProfileImageContainer.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(getContext())
                   .load(imageUrl)
                   .placeholder(R.drawable.img_pro_placeholder)
                   .noFade()
                   .into(mProfileImage);
        }
        else {
            mProfileImage.setImageResource(R.drawable.img_pro_placeholder);
        }
        updateProTeamIndicator();
    }

    public void setIsProTeam(final boolean isProTeam) {
        mIsProTeam = isProTeam;
        updateProTeamIndicator();
    }

    private void updateProTeamIndicator() {
        mProTeamIndicatorImage.setVisibility(GONE);
        mProTeamIndicatorName.setVisibility(GONE);
        if (mIsProTeam && mIsProTeamIndicatorEnabled) {
            if (mProfileImageContainer.getVisibility() == VISIBLE) {
                mProTeamIndicatorImage.setVisibility(VISIBLE);
            }
            else {
                mProTeamIndicatorName.setVisibility(VISIBLE);
            }
        }

    }
}
