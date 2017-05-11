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
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarouselPagerAdapter extends PagerAdapter {

    private List<ProCarouselVM> mProCarouselVMs;
    private ActionListener mActionListener;
    private Context mContext;

    public interface ActionListener {

        void onPrimaryButtonClick(ProCarouselVM pro, View button);
    }

    public CarouselPagerAdapter(
            @NonNull Context context,
            @NonNull List<ProCarouselVM> proCarouselVMs,
            @NonNull ActionListener listener
    ) {
        mProCarouselVMs = proCarouselVMs;
        mActionListener = listener;
        mContext = context;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View itemView = LayoutInflater.from(mContext)
                                      .inflate(R.layout.pro_team_carousel_item, container, false);
        final ProCarouselVM profile = mProCarouselVMs.get(position);

        FrameLayout heartContainer
                = (FrameLayout) itemView.findViewById(R.id.carousel_heart_container);
        View ratingContainer = itemView.findViewById(R.id.carousel_ratings_container);
        View noRatingsContainer = itemView.findViewById(R.id.carousel_no_ratings_indicator);
        TextView name = (TextView) itemView.findViewById(R.id.carousel_name);
        TextView rating = (TextView) itemView.findViewById(R.id.mini_pro_profile_rating);
        TextView jobCount = (TextView) itemView.findViewById(R.id.mini_pro_profile_jobs_count);
        CircleImageView image
                = (CircleImageView) itemView.findViewById(R.id.carousel_profile_image);
        Button mButton = (Button) itemView.findViewById(R.id.carousel_button);

        heartContainer.setVisibility(profile.isProTeam() ? View.VISIBLE : View.GONE);
        if (profile.getJobCount() > 0 && profile.getAverageRating() > 0.f) {

            ratingContainer.setVisibility(View.VISIBLE);
            noRatingsContainer.setVisibility(View.GONE);

            final String jobCountString = jobCount
                    .getResources()
                    .getQuantityString(
                            R.plurals.jobs_count,
                            profile.getJobCount(),
                            profile.getJobCount()
                    );
            jobCount.setText(jobCountString);

            final DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            rating.setText(format.format(profile.getAverageRating()));
        }
        else {
            ratingContainer.setVisibility(View.GONE);
            noRatingsContainer.setVisibility(View.VISIBLE);
        }

        name.setText(profile.getDisplayName());

        Picasso.with(image.getContext())
               .load(profile.getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .into(image);

        if(profile.isProfileEnabled())
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mContext.startActivity(ProProfileActivity.buildIntent(
                            mContext,
                            profile.getProviderId(),
                            SourcePage.SHARE
                    ));
                }
            });
        }

        if (!TextUtils.isBlank(profile.getButtonText())) {
            mButton.setText(profile.getButtonText());
        }

        mButton.setEnabled(profile.isActionable());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mActionListener.onPrimaryButtonClick(profile, view);
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
