package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handybook.handybook.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamCarouselView extends RelativeLayout
        implements CarouselVH.RecommendClickListener {

    @Bind(R.id.carousel_recycler_view)
    RecyclerView mRecyclerView;

    private List<ProCarouselVM> mProfiles;
    private CarouselVH.RecommendClickListener mRecommendClickListener;
    private CarouselRecyclerAdapter mCarouselRecyclerAdapter;

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

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mProfiles = new ArrayList<>();
        mCarouselRecyclerAdapter = new CarouselRecyclerAdapter(
                mProfiles,
                this
        );
        mRecyclerView.setAdapter(mCarouselRecyclerAdapter);

        if (mProfiles.size() <= 1) {
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        else {
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        }

    }

    public void bind(
            @NonNull final List<ProCarouselVM> models,
            @Nullable final CarouselVH.RecommendClickListener recommendClickListener
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

        Toast.makeText(
                getContext(),
                "Recommend click for pro: " + pro.getDisplayName(),
                Toast.LENGTH_SHORT
        ).show();
    }
}
