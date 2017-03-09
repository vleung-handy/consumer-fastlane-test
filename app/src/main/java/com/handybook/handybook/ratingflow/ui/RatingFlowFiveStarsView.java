package com.handybook.handybook.ratingflow.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handybook.handybook.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class RatingFlowFiveStarsView extends LinearLayout {

    private static final int STARS_COUNT = 5;
    private List<ImageView> mStarViews;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mIsLocked) { return; }
            final Object tag = view.getTag();
            if (tag != null && tag instanceof Integer) {
                final Integer indexSelected = (Integer) tag;
                selectRating(indexSelected + 1);
            }
            for (final ImageView starView : mStarViews) {
                starView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pulse));
            }
        }
    };

    public void selectRating(final int rating) {
        if (mIsLocked || rating < 1 || rating > STARS_COUNT) { return; }
        for (int index = 0; index < mStarViews.size(); index++) {
            setActive(mStarViews.get(index), index < rating);
        }
        if (mRatingSelectionListener != null) {
            mRatingSelectionListener.onRatingSelected(rating);
        }
    }

    public void lock() {
        mIsLocked = true;
    }

    @BindDimen(R.dimen.rating_flow_star_size)
    int mDefaultStarSize;
    @BindDimen(R.dimen.rating_flow_star_margin)
    int mDefaultStarMargin;
    private int mStarWidth;
    private int mStarHeight;
    private int mStarMargin;

    private boolean mIsLocked;

    private RatingSelectionListener mRatingSelectionListener;

    public RatingFlowFiveStarsView(final Context context) {
        super(context);
        init(null);
    }

    public RatingFlowFiveStarsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RatingFlowFiveStarsView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RatingFlowFiveStarsView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable final AttributeSet attributeSet) {

        ButterKnife.bind(this);

        if (attributeSet != null) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.RatingFlowFiveStarsView);
            mStarWidth = typedArray.getDimensionPixelSize(
                    R.styleable.RatingFlowFiveStarsView_starWidth, mDefaultStarSize);
            mStarHeight = typedArray.getDimensionPixelSize(
                    R.styleable.RatingFlowFiveStarsView_starHeight, mDefaultStarSize);
            mStarMargin = typedArray.getDimensionPixelSize(
                    R.styleable.RatingFlowFiveStarsView_starMargin, mDefaultStarMargin);
            typedArray.recycle();
        }
        else {
            mStarWidth = mStarHeight = mDefaultStarSize;
            mStarMargin = mDefaultStarMargin;
        }

        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        mStarViews = new ArrayList<>();
        removeAllViews();
        for (int index = 0; index < STARS_COUNT; index++) {
            final ImageView imageView = createStarImageView();
            imageView.setTag(index);
            imageView.setOnClickListener(mOnClickListener);
            setActive(imageView, false);
            mStarViews.add(imageView);
            addView(imageView);
        }
    }

    private void setActive(final ImageView starView, final boolean active) {
        if (active) {
            starView.clearColorFilter();
        }
        else {
            starView.setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.placeholder_gray),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
    }

    @NonNull
    private ImageView createStarImageView() {
        final ImageView starView = new ImageView(getContext());
        final LayoutParams layoutParams = new LayoutParams(mStarWidth, mStarHeight);
        layoutParams.leftMargin = mStarMargin;
        layoutParams.rightMargin = mStarMargin;
        starView.setLayoutParams(layoutParams);
        starView.setImageResource(R.drawable.ic_star);
        return starView;
    }

    public void setRatingSelectionListener(final RatingSelectionListener listener) {
        mRatingSelectionListener = listener;
    }

    public interface RatingSelectionListener {

        void onRatingSelected(final int rating);
    }
}
