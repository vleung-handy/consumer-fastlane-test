package com.handybook.handybook.proprofiles.reviews.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.library.ui.view.OnScrollToBottomListener;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.proprofiles.reviews.model.ProReviews;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * container for:
 * - reviews recycler view
 * - loading view
 * - empty view
 * - error view
 * todo a reusable component to handle that would be ideal, but out of scope for this ticket
 */
public class ProProfileReviewsContainer extends FrameLayout {

    @Bind(R.id.pro_profile_reviews_recycler_view)
    EmptiableRecyclerView mProReviewsRecyclerView;

    @Bind(R.id.pro_profile_reviews_loading_view)
    View mLoadingView;

    @Bind(R.id.pro_profile_reviews_no_reviews_view)
    View mEmptyView;

    @Bind(R.id.pro_profile_no_reviews_body_text)
    TextView mNoReviewsBodyText;

    @Bind(R.id.loading_error_layout)
    View mLoadingErrorLayout;

    @Bind(R.id.loading_error_try_again_button)
    Button mLoadingErrorTryAgainButton;

    ProReviewsRecyclerViewAdapter mProReviewsRecyclerViewAdapter;
    private Integer mCurrentPageLastReviewId;

    public ProProfileReviewsContainer(final Context context) {
        super(context);
        init();
    }

    public ProProfileReviewsContainer(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProProfileReviewsContainer(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext())
                      .inflate(R.layout.layout_pro_profile_reviews, this);
        ButterKnife.bind(this);

        initReviewsRecyclerView();
    }

    private void initReviewsRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mProReviewsRecyclerView.setLayoutManager(linearLayoutManager);

        mProReviewsRecyclerViewAdapter = new ProReviewsRecyclerViewAdapter(
                null
        );
        mProReviewsRecyclerView.setAdapter(mProReviewsRecyclerViewAdapter);
        mProReviewsRecyclerView.setEmptyView(mEmptyView);
        mLoadingView.setVisibility(VISIBLE);
    }

    /**
     * resets the state
     */
    public void clearReviews()//todo give better name
    {
        mCurrentPageLastReviewId = null;
        mProReviewsRecyclerViewAdapter.clear();
        mLoadingErrorLayout.setVisibility(GONE);
        mLoadingView.setVisibility(VISIBLE);//todo dont like this here
    }

    public Integer getCurrentPageLastReviewId() {
        return mCurrentPageLastReviewId;
    }

    public boolean hasReviews() {
        return mProReviewsRecyclerViewAdapter.getItemCount() > 0;
    }

    public void addOnScrollToBottomListener(@NonNull final OnScrollToBottomListener onScrollToBottomListener) {
        mProReviewsRecyclerView.addOnScrollListener(onScrollToBottomListener);
    }

    public void setOnLoadingErrorTryAgainButtonClickListener(OnClickListener onClickListener) {
        mLoadingErrorTryAgainButton.setOnClickListener(onClickListener);
    }

    public void removeScrollToBottomListener(OnScrollToBottomListener onScrollToBottomListener) {
        mProReviewsRecyclerView.removeOnScrollListener(onScrollToBottomListener);
    }

    public void updateWithProviderFirstName(@Nullable String providerFirstName) {
        mNoReviewsBodyText.setText(getResources().getString(
                R.string.pro_profile_no_reviews_body_text,
                providerFirstName
        ));
        mNoReviewsBodyText.setVisibility(TextUtils.isBlank(providerFirstName) ? GONE : VISIBLE);
    }

    public void updateForAdditionalProReviews(@Nullable ProReviews proReviews) {
        if (proReviews != null) {
            mCurrentPageLastReviewId = proReviews.getLastReviewId();
        }
        mLoadingErrorLayout.setVisibility(GONE);
        mLoadingView.setVisibility(GONE);

        //need to call this even if null so that the adapter listener can be notified so that empty view can be triggered
        mProReviewsRecyclerViewAdapter.append(proReviews);
    }

    public void showErrorView() {
        mLoadingErrorLayout.setVisibility(VISIBLE);
    }
}
