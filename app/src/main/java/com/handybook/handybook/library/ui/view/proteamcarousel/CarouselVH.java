package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CarouselVH extends RecyclerView.ViewHolder {

    @Bind(R.id.carousel_profile_image)
    CircleImageView mImage;

    @Bind(R.id.carousel_heart_container)
    FrameLayout mHeartContainer;

    @Bind(R.id.carousel_name)
    TextView mName;

    @Bind(R.id.carousel_ratings_container)
    View mRatingContainer;

    @Bind(R.id.mini_pro_profile_rating)
    TextView mRating;

    @Bind(R.id.mini_pro_profile_jobs_count)
    TextView mJobCount;

    @Bind(R.id.carousel_button)
    Button mButton;

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

        mHeartContainer.setVisibility(View.VISIBLE);
        mRatingContainer.setVisibility(View.VISIBLE);
        mName.setText(profile.getDisplayName());
        String rating;
        if (TextUtils.isBlank(profile.getAverageRating())) {
            rating = mRating.getResources().getString(R.string.pro_referral_not_yet_rated);
        }
        else {
            rating = profile.getAverageRating();
        }

        mRating.setText(rating);
        String jobCount = mJobCount.getResources()
                                   .getQuantityString(R.plurals.jobs_count,
                                                      profile.getJobCount(),
                                                      profile.getJobCount()
                                   );
        mJobCount.setText(jobCount);

        Picasso.with(mImage.getContext())
               .load(profile.getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .into(mImage);

        mButton.setText(profile.getButtonText());
    }

    @OnClick(R.id.carousel_button)
    void recommendClick() {
        mRecommendClickListener.onRecommendClick(mProCarouselVM);
    }

    public interface RecommendClickListener {

        void onRecommendClick(ProCarouselVM pro);
    }

}
