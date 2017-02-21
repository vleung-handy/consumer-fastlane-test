package com.handybook.handybook.persistentpromo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * note that the AppBarLayout is expected to be a direct child of the CoordinatorLayout
 * See https://developer.android.com/reference/android/support/design/widget/AppBarLayout.html
 * "This view depends heavily on being used as a direct child within a CoordinatorLayout.
 * If you use AppBarLayout within a different ViewGroup, most of it's functionality will not work."
 */
public class PersistentPromoAppBarLayout extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener
{
    @Bind(R.id.persistent_promo_preview_view)
    PersistentPromoPreviewToolbar mPersistentPromoPreviewToolbar;

    private OnPersistentPromoFullyExpandedListener mOnPersistentPromoFullyExpandedListener;

    public PersistentPromoAppBarLayout(final Context context)
    {
        super(context);
        init(context);
    }

    public PersistentPromoAppBarLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    private void init(@NonNull Context context)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_persistent_promo_collapsible, this);
        ButterKnife.bind(this);
        addOnOffsetChangedListener(this);
    }

    public void setOnPersistentPromoFullyExpandedListener(final OnPersistentPromoFullyExpandedListener onPersistentPromoFullyExpandedListener)
    {
        mOnPersistentPromoFullyExpandedListener = onPersistentPromoFullyExpandedListener;
    }

    public void updateWithModel(@NonNull PersistentPromo persistentPromo)
    {
        mPersistentPromoPreviewToolbar.updateWithModel(persistentPromo);
    }

    @OnClick(R.id.persistent_promo_preview_view)
    public void onPersistentPromoPreviewViewClicked()
    {
        setExpanded(true, true);
    }

    @Override
    public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset)
    {
        /*
        TODO
        intercepting touch listener and setting clickable, focusable=false
        doesn't seem to allow click events to be propagated to views underneath the toolbar
        would prefer to do that instead of this
         */
        if(verticalOffset == 0)
        {
            //offer fully expanded. don't want this toolbar interfering with clicks meant for view below it
            mPersistentPromoPreviewToolbar.setVisibility(GONE);
            if(mOnPersistentPromoFullyExpandedListener != null)
            {
                mOnPersistentPromoFullyExpandedListener.onPersistentPromoFullyExpanded();
            }
        }
        else
        {
            //offer not fully expanded
            mPersistentPromoPreviewToolbar.setVisibility(VISIBLE);
        }
        float persistentPromoExpandedPercent = getExpandedPercent();
        setAlpha(1 - persistentPromoExpandedPercent);
    }

    public float getExpandedPercent()
    {
        //getY() is always negative here
        return 1 + getY()/getTotalScrollRange();
    }

    public interface OnPersistentPromoFullyExpandedListener
    {
        void onPersistentPromoFullyExpanded();
    }
    /**
     * defines a scrolling and alpha transition behavior for a view, that is dependent on this view
     */
    public static class ScrollingFadingViewBehavior extends ScrollingViewBehavior
    {
        public ScrollingFadingViewBehavior(@NonNull Context context, @Nullable AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(
                final CoordinatorLayout parent,
                final View child,
                final View dependency
        )
        {
            return dependency instanceof PersistentPromoAppBarLayout;
        }

        @Override
        public boolean onDependentViewChanged(
                final CoordinatorLayout parent, final View child, final View dependency
        )
        {
            super.onDependentViewChanged(parent, child, dependency);

            //also set alpha depending on how much the dependent view is scrolled
            PersistentPromoAppBarLayout appBarLayout = (PersistentPromoAppBarLayout) dependency;
            child.setAlpha(1 - appBarLayout.getExpandedPercent());
            return false;
        }
    }


    /**
     * defines an alpha transition behavior for a view, that is dependent on this view
     */
    public static class FadingViewBehavior extends CoordinatorLayout.Behavior<View>
    {
        public FadingViewBehavior(@NonNull Context context, @Nullable AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(
                final CoordinatorLayout parent,
                final View child,
                final View dependency
        )
        {
            return dependency instanceof PersistentPromoAppBarLayout;
        }

        @Override
        public boolean onDependentViewChanged(
                final CoordinatorLayout parent, final View child, final View dependency
        )
        {
            super.onDependentViewChanged(parent, child, dependency);

            //also set alpha depending on how much the dependent view is scrolled
            PersistentPromoAppBarLayout appBarLayout = (PersistentPromoAppBarLayout) dependency;
            child.setAlpha(1 - appBarLayout.getExpandedPercent());
            return false;
        }
    }
}
