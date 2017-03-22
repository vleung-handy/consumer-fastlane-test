package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarouselPagerAdapter extends PagerAdapter {

    private List<ProCarouselVM> mProCarouselVMs;
    private RecommendClickListener mRecommendClickListener;
    private Context mContext;


    public interface RecommendClickListener {

        void onRecommendClick(ProCarouselVM pro);
    }

    public CarouselPagerAdapter(
            @NonNull Context context,
            @NonNull List<ProCarouselVM> proCarouselVMs,
            @NonNull RecommendClickListener listener
    ) {
        mProCarouselVMs = proCarouselVMs;
        mRecommendClickListener = listener;
        mContext = context;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View itemView = LayoutInflater.from(mContext)
                                      .inflate(R.layout.carousel_item, container, false);
        final ProCarouselVM profile = mProCarouselVMs.get(position);

        FrameLayout heartContainer
                = (FrameLayout) itemView.findViewById(R.id.carousel_heart_container);
        View ratingContainer = itemView.findViewById(R.id.carousel_ratings_container);
        TextView name = (TextView) itemView.findViewById(R.id.carousel_name);
        TextView rating = (TextView) itemView.findViewById(R.id.mini_pro_profile_rating);
        TextView jobCount = (TextView) itemView.findViewById(R.id.mini_pro_profile_jobs_count);
        CircleImageView image
                = (CircleImageView) itemView.findViewById(R.id.carousel_profile_image);
        Button mButton = (Button) itemView.findViewById(R.id.carousel_button);

        heartContainer.setVisibility(View.VISIBLE);
        ratingContainer.setVisibility(View.VISIBLE);
        name.setText(profile.getDisplayName());
        String ratingString;
        if (TextUtils.isBlank(profile.getAverageRating())) {
            ratingString = rating.getResources().getString(R.string.pro_referral_not_yet_rated);
        }
        else {
            ratingString = profile.getAverageRating();
        }

        rating.setText(ratingString);
        String jobCountString = jobCount
                .getResources()
                .getQuantityString(
                        R.plurals.jobs_count,
                        profile.getJobCount(),
                        profile.getJobCount()
                );
        jobCount.setText(jobCountString);

        Picasso.with(image.getContext())
               .load(profile.getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .into(image);

        if (!TextUtils.isBlank(profile.getButtonText())) {
            mButton.setText(profile.getButtonText());
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mRecommendClickListener.onRecommendClick(profile);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    public void setProCarouselVMs(final List<ProCarouselVM> proCarouselVMs) {
        mProCarouselVMs = proCarouselVMs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProCarouselVMs.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }
}
