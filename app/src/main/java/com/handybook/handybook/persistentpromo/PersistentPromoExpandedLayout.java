package com.handybook.handybook.persistentpromo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

//FIXME clean up!!!

/**
 * layout for the full/expanded persistent promo view
 */
public class PersistentPromoExpandedLayout extends FrameLayout
{
    @Bind(R.id.persistent_promo_expanded_title)
    TextView mTitleText;
    @Bind(R.id.persistent_promo_expanded_subtitle)
    TextView mSubtitleText;
    @Bind(R.id.persistent_promo_expanded_header_image)
    ImageView mHeaderImage;
    @Bind(R.id.persistent_promo_expanded_action_button)
    Button mActionButton;
    @Bind(R.id.persistent_promo_expanded_dismiss_button)
    View mDismissButton;
    @Bind(R.id.persistent_promo_expanded_text_content_layout)
    View mTextContentLayout;

    private OnActionButtonClickedListener mOnActionButtonClickedListener;

    public PersistentPromoExpandedLayout(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_persistent_promo_expanded, this);
        ButterKnife.bind(this);
    }

    public PersistentPromoExpandedLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PersistentPromoExpandedLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnActionButtonClickedListener(final OnActionButtonClickedListener onActionButtonClickedListener)
    {
        mOnActionButtonClickedListener = onActionButtonClickedListener;
    }

    public void setDismissButtonClickListener(@NonNull OnClickListener onClickListener)
    {
        mDismissButton.setOnClickListener(onClickListener);
    }

    private void updateAndShowTextIfNecessary(TextView textView, String text)
    {
        if (text != null)
        {
            textView.setText(text);
            textView.setVisibility(VISIBLE);
        }
        else
        {
            textView.setVisibility(GONE);
        }
    }

    public void updateWithModel(@NonNull final PersistentPromo persistentPromo)
    {
        updateAndShowTextIfNecessary(mTitleText, persistentPromo.getTitleText());
        updateAndShowTextIfNecessary(mSubtitleText, persistentPromo.getSubtitleText());
        if (persistentPromo.getActionText() != null)
        {
            mActionButton.setText(persistentPromo.getActionText());
            mActionButton.setVisibility(VISIBLE);
            mActionButton.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (mOnActionButtonClickedListener != null)
                    {
                        mOnActionButtonClickedListener.onActionButtonClicked(persistentPromo.getDeepLinkUrl());
                    }
                }
            });
        }
        else
        {
            mActionButton.setVisibility(GONE);
        }

        if (persistentPromo.getImageUrl() != null)
        {
            //load image
            loadHeaderImage(persistentPromo.getImageUrl());
            mHeaderImage.setVisibility(VISIBLE);
        }
        else
        {
            mHeaderImage.setVisibility(GONE);
        }
    }

    private void loadHeaderImage(@NonNull final String imageUrl)
    {
        mHeaderImage.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                    {
                        @Override
                        public void onGlobalLayout()
                        {
                            try //picasso doesn't catch all errors like empty URL!
                            {
                                if (mHeaderImage.getWidth() <= 0)
                                {
                                    //may have to make more than one layout pass before this gets measured
                                    return;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    mHeaderImage.getViewTreeObserver()
                                                .removeOnGlobalLayoutListener(this);
                                }
                                else
                                {
                                    mHeaderImage.getViewTreeObserver()
                                                .removeGlobalOnLayoutListener(this);
                                }
                                //we are going to use an animated placeholder eventually
                                Picasso.with(getContext()).
                                        load(imageUrl).
                                               error(R.drawable.banner_image_load_failed).
                                               resize(mHeaderImage.getWidth(), 0).
                                               into(mHeaderImage);
                            /*
                            need to call resize() because ImageView
                            can have rounding errors when scaling
                            that causes a 1px margin around the image and
                            there's no known way of scaling it against one dimension
                            using the view params
                             */
                            }
                            catch (Exception e)
                            {
                                Crashlytics.logException(e);
                            }
                        }
                    });

    }

    public interface OnActionButtonClickedListener
    {
        void onActionButtonClicked(String actionUrl);
    }


    /**
     * defines a behavior for this view, that is dependent on a PersistentAppBarLayout
     */
    public static class DefaultBehavior extends CoordinatorLayout.Behavior<PersistentPromoExpandedLayout>
    {
        public DefaultBehavior(@NonNull Context context, @Nullable AttributeSet attrs)
        {
            super(context, attrs);
        }

        /**
         * if this view is not in a CoordinatorLayout inside a PersistentPromoAppBarLayout,
         * this will return false and the behavior will not apply
         * @param parent
         * @param child
         * @param dependency
         * @return
         */
        @Override
        public boolean layoutDependsOn(
                final CoordinatorLayout parent,
                final PersistentPromoExpandedLayout child,
                final View dependency
        )
        {
            return dependency instanceof PersistentPromoAppBarLayout;
        }

        @Override
        public boolean onDependentViewChanged(
                final CoordinatorLayout parent, final PersistentPromoExpandedLayout child, final View dependency
        )
        {
            super.onDependentViewChanged(parent, child, dependency);

            //also set alpha depending on how much the dependent view is scrolled
            PersistentPromoAppBarLayout appBarLayout = (PersistentPromoAppBarLayout) dependency;

            float appbarExpandedPercent = appBarLayout.getExpandedPercent();
            onDependentViewPercentExpandedChanged(child, appbarExpandedPercent);
            return false;
        }

        private void onDependentViewPercentExpandedChanged(final PersistentPromoExpandedLayout childView,
                                                           float dependentViewPercentExpanded)
        {
            childView.setAlpha(dependentViewPercentExpanded);

            float percentExpandedThresholdForDismissButtonVisible = 0.5f;
            float dismissButtonMaxAlpha = 0.7f;
            float dismissButtonAlpha =
                    Math.max(0, dependentViewPercentExpanded * (dismissButtonMaxAlpha + percentExpandedThresholdForDismissButtonVisible)
                            - percentExpandedThresholdForDismissButtonVisible);
            childView.mDismissButton.setAlpha(dismissButtonAlpha);

            float percentExpandedThresholdForNormalScale = 0.5f;
            float imageScale = Math.max(1, 1 + percentExpandedThresholdForNormalScale - dependentViewPercentExpanded);
            childView.mHeaderImage.setScaleX(imageScale);
            childView.mHeaderImage.setScaleY(imageScale);

            float percentExpandedThresholdForNormalTextPosition = 0.8f;
            int maxTextMarginPx = childView.mTextContentLayout.getHeight() / 2;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childView.mTextContentLayout.getLayoutParams();
            layoutParams.topMargin =
                    (int) (Math.max(0, maxTextMarginPx * (percentExpandedThresholdForNormalTextPosition - dependentViewPercentExpanded)));
            childView.mTextContentLayout.setLayoutParams(layoutParams);
        }
    }
}
