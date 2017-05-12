package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.view.OnScrolledToBottomListener;
import com.handybook.handybook.proprofiles.model.ProProfile;
import com.handybook.handybook.proprofiles.reviews.model.ProReviews;
import com.handybook.handybook.proprofiles.reviews.ui.ProProfileReviewsContainer;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * holds a tablayout and viewpager that holds
 * the pro reviews view and about view
 *
 * TODO refactor - make this a fragment? would reduce # of nested callbacks
 */
public class ProProfileDetailsTabLayout extends FrameLayout {

    @Bind(R.id.pro_profile_details_tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.pro_profile_details_tab_layout_view_pager)
    ViewPager mViewPager;

    /**
     * to get its position in the tab layout for logging purposes only
     */
    private TabLayout.Tab mReviewsTab;

    /**
     * to get its position in the tab layout for logging purposes only
     */
    private TabLayout.Tab mAboutTab;

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener;

    private OnScrolledToBottomListener mOnScrolledToBottomListener;
    private RequestReviewsListener mRequestReviewsListener;
    private ProProfileReviewsContainer mReviewsContainer;

    private ProProfileAboutView mProProfileAboutView;

    public ProProfileDetailsTabLayout(final Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext())
                                  .inflate(R.layout.layout_pro_profile_details_tab, this);
        ButterKnife.bind(this, view);

        //initialize view pager n stuff
        mReviewsContainer = new ProProfileReviewsContainer(getContext());
        mProProfileAboutView = new ProProfileAboutView(getContext());

        mTabLayout.removeAllTabs();
        mReviewsTab = mTabLayout.newTab().setText(getResources().getString(
                R.string.pro_profile_reviews_tab_title));
        mAboutTab = mTabLayout.newTab().setText(getResources().getString(
                R.string.pro_profile_about_tab_title));

        mTabLayout.addTab(mReviewsTab);
        mTabLayout.addTab(mAboutTab);

        //array needed to ensure positions match the tab titles
        View[] tabViews = new View[mTabLayout.getTabCount()];
        tabViews[mReviewsTab.getPosition()] = mReviewsContainer;
        tabViews[mAboutTab.getPosition()] = mProProfileAboutView;

        mViewPager.setAdapter(new TabAdapter(Arrays.asList(tabViews)));
        mOnScrolledToBottomListener = new OnScrolledToBottomListener() {
            @Override
            public void onScrolledToBottom() {
                if (mRequestReviewsListener != null) {
                    mRequestReviewsListener.onRequestMoreReviews(
                            mReviewsContainer.getCurrentPageLastReviewId());
                }
            }
        };
        mReviewsContainer.addOnScrollToBottomListener(mOnScrolledToBottomListener);

        //todo dont like this
        mReviewsContainer.setOnLoadingErrorTryAgainButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mReviewsContainer.clearReviews();
                mReviewsContainer.showLoadingLayout();
                mRequestReviewsListener.onRequestMoreReviews(
                        mReviewsContainer.getCurrentPageLastReviewId());
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    final int position,
                    final float positionOffset,
                    final int positionOffsetPixels
            ) {
            }

            @Override
            public void onPageSelected(final int position) {
                //update the tab selected indicator when we swipe
                mTabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (mOnTabSelectedListener != null) {
                    mOnTabSelectedListener.onTabSelected(tab);
                }
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {
            }
        });
    }

    /**
     * for logging purposes only
     */
    public int getAboutTabPosition() {
        return mAboutTab.getPosition();
    }

    /**
     * for logging purposes only
     */
    public int getReviewsTabPosition() {
        return mReviewsTab.getPosition();
    }

    public ProProfileDetailsTabLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProProfileDetailsTabLayout(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * needed for logging purposes since we can't use the event bus in this class
     * @param onTabSelectedListener
     */
    public void setOnTabSelectedListener(final TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    public void setRequestReviewsListener(final RequestReviewsListener requestReviewsListener) {
        mRequestReviewsListener = requestReviewsListener;
    }

    public void updateForProProfile(@NonNull ProProfile proProfile) {
        mProProfileAboutView.updateWithModel(proProfile);
        mReviewsContainer.updateWithProviderFirstName(
                proProfile.getProviderInformation().getFirstName());
    }

    @NonNull
    public ProProfileReviewsContainer getReviewsContainer() {
        return mReviewsContainer;
    }

    public void updateForAdditionalProReviews(@NonNull ProReviews proReviews) {
        //if no more, don't need listener
        mReviewsContainer.updateForAdditionalProReviews(proReviews);

        if (proReviews.getReviews() == null || proReviews.getReviews().length == 0) {
            mReviewsContainer.removeScrollToBottomListener(mOnScrolledToBottomListener);
            mOnScrolledToBottomListener = null;
            if (!mReviewsContainer.hasReviews()) {
                //select the About tab if there are no reviews
                mTabLayout.getTabAt(getAboutTabPosition()).select();
            }
        }
    }

    interface RequestReviewsListener {

        void onRequestMoreReviews(@Nullable String mCurrentPageLastReviewId);
    }


    private class TabAdapter extends PagerAdapter {

        private List<View> mViews;

        TabAdapter(final List<View> views) {
            mViews = views;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view = getItemAt(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(getItemAt(position));
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(final Object object) {
            return mViews.indexOf(object);
        }

        @Nullable
        private View getItemAt(final int position) {
            return mViews.get(position);
        }
    }
}
