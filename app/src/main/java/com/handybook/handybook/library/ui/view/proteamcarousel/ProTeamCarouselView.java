package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamCarouselView extends RelativeLayout
        implements CarouselPagerAdapter.RecommendClickListener {

    @Bind(R.id.carousel_pager)
    ViewPager mViewPager;

    private List<ProCarouselVM> mProfiles;
    private CarouselPagerAdapter.RecommendClickListener mRecommendClickListener;
    private CarouselPagerAdapter mCarouselRecyclerAdapter;

    public ProTeamCarouselView(final Context context) {
        super(context);
        init();
    }

    public ProTeamCarouselView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProTeamCarouselView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.pro_team_carousel, this);
        ButterKnife.bind(this);

        mProfiles = new ArrayList<>();
        mCarouselRecyclerAdapter = new CarouselPagerAdapter(
                getContext(),
                mProfiles,
                this
        );
        mViewPager.setAdapter(mCarouselRecyclerAdapter);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.default_margin_half));

    }

    public void bind(
            @NonNull final List<ProCarouselVM> models,
            @Nullable final CarouselPagerAdapter.RecommendClickListener recommendClickListener
    ) {
        mProfiles = models;
        mRecommendClickListener = recommendClickListener;
        mCarouselRecyclerAdapter.setProCarouselVMs(mProfiles);
    }

    @Override
    public void onRecommendClick(@NonNull final ProCarouselVM pro) {
        //relay it to the parent fragment if it cares about the data.
        if (mRecommendClickListener != null) {
            mRecommendClickListener.onRecommendClick(pro);
        }
    }
}
