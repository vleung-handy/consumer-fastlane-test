package com.handybook.handybook.persistentpromo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

//FIXME clean up! what are alternatives to this for grouping the promo logic?

/**
 * groups and manages the promo views
 * note that the AppBarLayout is expected to be a direct child of the CoordinatorLayout
 * so we cannot wrap it in another view group
 */
public class PersistentPromoCoordinatorLayout extends CoordinatorLayout
{
    /**
     * the expanded persistent promo view that is behind the app bar layout
     * and is gradually revealed as the app bar layout is dragged down
     */
    @Bind(R.id.persistent_promo_expanded_view)
    PersistentPromoExpandedLayout mPersistentPromoExpandedLayout;
    /**
     * contains the persistent promo preview toolbar and functionality for it to expand and collapse
     */
    @Bind(R.id.persistent_promo_appbar_layout)
    PersistentPromoAppBarLayout mPersistentPromoAppBarLayout;

    public PersistentPromoCoordinatorLayout(final Context context)
    {
        super(context);
        init(context, null);
    }

    public PersistentPromoCoordinatorLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attributeSet)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_persistent_promo_composite, this);
        ButterKnife.bind(this);
        updateWithModel(null);

        //cover the expanded layout when the dismiss button is pressed
        mPersistentPromoExpandedLayout.setDismissButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                mPersistentPromoAppBarLayout.setExpanded(false, true);
            }
        });

        //handle the expanded layout's action button. putting it here because we may want to collapse the sibling view
        mPersistentPromoExpandedLayout.setOnActionButtonClickedListener(new PersistentPromoExpandedLayout.OnActionButtonClickedListener() {
            @Override
            public void onActionButtonClicked(final String deeplinkUrl)
            {
                if(!TextUtils.isEmpty(deeplinkUrl))
                {
                    Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplinkUrl));
                    Utils.safeLaunchIntent(deepLinkIntent, getContext());
                }
                else
                {
                    //just collapse the offer view if no deep link
                    mPersistentPromoAppBarLayout.setExpanded(false, true);
                }
            }
        });
    }

    /**
     * sets the visibility of the persistent promo views
     * @param visible
     */
    private void setPersistentPromoVisible(boolean visible)
    {
        mPersistentPromoExpandedLayout.setVisibility(visible ? VISIBLE : GONE);
        mPersistentPromoAppBarLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * updates the UI for the given model
     * @param persistentPromo
     */
    public void updateWithModel(@Nullable PersistentPromo persistentPromo)
    {
        if(!canDisplayPersistentPromo(persistentPromo))
        {
            setPersistentPromoVisible(false);
            return;
        }
        setPersistentPromoVisible(true);
        mPersistentPromoExpandedLayout.updateWithModel(persistentPromo);
        mPersistentPromoAppBarLayout.updateWithModel(persistentPromo);
    }

    /**
     * given the model, whether we should show the promo view
     * @param persistentPromo
     * @return
     */
    private boolean canDisplayPersistentPromo(@Nullable PersistentPromo persistentPromo)
    {
        return persistentPromo != null && !TextUtils.isEmpty(persistentPromo.getPreviewText())
                && !TextUtils.isEmpty(persistentPromo.getActionText());
    }
}
