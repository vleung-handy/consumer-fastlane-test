package com.handybook.handybook.proprofiles.reviews.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.ratingflow.ui.RatingFlowFiveStarsView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProReviewsRecyclerViewItem extends FrameLayout {

    @Bind(R.id.pro_review_recycler_view_item_star_rating_view)
    RatingFlowFiveStarsView mRatingFlowFiveStarsView;

    @Bind(R.id.pro_review_recycler_view_item_body_text)
    TextView mBodyText;

    @Bind(R.id.pro_review_recycler_review_item_date_text)
    TextView mDateText;

    public ProReviewsRecyclerViewItem(@NonNull final Context context) {
        super(context);
        init();
    }

    public ProReviewsRecyclerViewItem(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public ProReviewsRecyclerViewItem(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @AttrRes final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext())
                      .inflate(R.layout.layout_pro_review_recycler_view_item, this);
        ButterKnife.bind(this);
        mRatingFlowFiveStarsView.setEnabled(false);
        mRatingFlowFiveStarsView.setClickable(false);
        mRatingFlowFiveStarsView.lock();//this makes it so that the rating can't be modified via clicking
    }

    /**
     * @param rating expected range: [0.0, 5.0]
     */
    public void setRating(@Nullable final Float rating) {
        if (rating == null) {
            mRatingFlowFiveStarsView.setVisibility(GONE);
        }
        else {
            /*
            the existing custom star ratings view doesn't support float ratings right now

            currently, we ONLY show 5-star ratings. if we want to support other rating values
            we must refactor that star ratings view to display non-int values so that it is not misleading.
             */
            mRatingFlowFiveStarsView.selectRating(Math.round(rating), true);
            mRatingFlowFiveStarsView.setVisibility(VISIBLE);
        }
    }

    public void setBodyText(final String bodyText) {
        if(TextUtils.isBlank(bodyText))
        {
            mBodyText.setVisibility(GONE);
        }
        else
        {
            /*
            todo would rather not use formatter here if we have a viewmodel
            but need to pass context to viewmodel cleanly (it is created from an adapter) first
             */
            mBodyText.setText(getResources().getString(R.string.pro_profile_review_body_formatted, bodyText));
            mBodyText.setVisibility(VISIBLE);
        }
    }

    public void setDateText(@NonNull final String dateText) {
        mDateText.setText(dateText);
    }
}
