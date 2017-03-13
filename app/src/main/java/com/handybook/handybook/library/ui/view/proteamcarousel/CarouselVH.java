package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CarouselVH extends RecyclerView.ViewHolder {

    @Bind(R.id.avatar_image_profile)
    CircleImageView mImage;

    @Bind(R.id.avatar_heart_container)
    FrameLayout mHeartContainer;

    @Bind(R.id.carousel_name)
    TextView mName;

    @Bind(R.id.carousel_ratings_container)
    View mRatingContainer;

    @Bind(R.id.mini_pro_profile_rating)
    TextView mRating;

    @Bind(R.id.mini_pro_profile_jobs_count)
    TextView mJobCount;

    private ProCarouselVM mProCarouselVM;
    private RecommendClickListener mRecommendClickListener;

    public CarouselVH(
            @NonNull final View itemView,
            @NonNull final RecommendClickListener listener
    ) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mRecommendClickListener = listener;
    }

    public void bind(@NonNull ProCarouselVM profile) {
        mProCarouselVM = profile;
        Picasso.with(mImage.getContext())
               .load(profile.getImageUrl())
               .into(mImage);

        mHeartContainer.setVisibility(View.VISIBLE);
        mRatingContainer.setVisibility(View.VISIBLE);

        mName.setText(profile.getDisplayName());
        mRating.setText(profile.getAverageRating());
        mJobCount.setText(profile.getJobCount());

        Picasso.with(mImage.getContext())
               .load(profile.getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .into(mImage);
    }

    @OnClick(R.id.carousel_recommend)
    void recommendClick() {
        mRecommendClickListener.onRecommendClick(mProCarouselVM);
    }

    interface RecommendClickListener {

        void onRecommendClick(ProCarouselVM pro);
    }

}
