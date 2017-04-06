package com.handybook.handybook.library.ui.view.proteamcarousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamCarouselView extends RelativeLayout
        implements CarouselPagerAdapter.ActionListener {

    @Bind(R.id.carousel_pager)
    ViewPager mViewPager;

    private List<ProCarouselVM> mProfiles;
    private CarouselPagerAdapter.ActionListener mActionListener;
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

        //Potentially might need to make padding flexible and configurable for different use cases
        //of this carousel.
        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding_4x);
        mViewPager.setPadding(padding, 0, padding, 0);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.default_margin_double));
    }

    public void bind(
            @NonNull final List<ProCarouselVM> models,
            @Nullable final CarouselPagerAdapter.ActionListener actionListener
    ) {
        mProfiles = models;
        mActionListener = actionListener;
        mCarouselRecyclerAdapter.setProCarouselVMs(mProfiles);
    }

    @Override
    public void onPrimaryButtonClick(@NonNull final ProCarouselVM pro, final View button) {
        //relay it to the parent fragment if it cares about the data.
        if (mActionListener != null) {
            mActionListener.onPrimaryButtonClick(pro, button);
        }
    }
}
