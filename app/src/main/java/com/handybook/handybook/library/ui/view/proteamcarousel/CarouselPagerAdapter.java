package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.VerticalMiniProProfile;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;

import java.util.List;

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

        VerticalMiniProProfile verticalMiniProProfile
                = (VerticalMiniProProfile) itemView.findViewById(R.id.pro_team_carousel_item_mini_pro_profile_card);

        verticalMiniProProfile.setProTeamIndicatorEnabled(true);
        verticalMiniProProfile.setIsProTeam(profile.isProTeam());
        verticalMiniProProfile.setIsProTeamFavorite(profile.isFavorite());
        verticalMiniProProfile.setTitle(profile.getDisplayName());
        verticalMiniProProfile.setImage(profile.getImageUrl());
        Button mButton = (Button) itemView.findViewById(R.id.carousel_button);

        verticalMiniProProfile.setRatingAndJobsCount(
                profile.getAverageRating(),
                profile.getJobCount()
        );

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
