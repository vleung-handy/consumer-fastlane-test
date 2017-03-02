package com.handybook.handybook.promos.persistent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * groups and manages the promo views
 * note that the AppBarLayout is expected to be a direct child of the CoordinatorLayout
 * so we cannot wrap it in another view group
 */
public class PersistentPromoCoordinatorLayout extends CoordinatorLayout {

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

    /**
     * this is needed for logging only
     * really don't like injecting this inside a view,
     * but also don't want to create a bunch of custom callbacks
     * just to be able to move this to a fragment
     *
     * TODO what are better alternatives?
     */
    @Inject
    Bus mBus;

    /**
     * the model that was used to create the UI
     * for logging purposes only
     */
    private PersistentPromo mPersistentPromo;

    public PersistentPromoCoordinatorLayout(final Context context) {
        super(context);
        init(context);
    }

    public PersistentPromoCoordinatorLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(@NonNull final Context context) {
        Utils.inject(getContext(), this);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_persistent_promo_composite, this);
        ButterKnife.bind(this);
        updateWithModel(null);

        //cover the expanded layout when the dismiss button is pressed
        mPersistentPromoExpandedLayout.setDismissButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPersistentPromoAppBarLayout.setExpanded(false, true);
            }
        });

        //log when user swipes down to reveal promo
        mPersistentPromoAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean mWasPromoFullyCollapsed = false;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //fully collapsed
                    mWasPromoFullyCollapsed = true;
                }
                else {
                    if (mWasPromoFullyCollapsed && mPersistentPromo != null) {
                        //user swiped down to reveal promo
                        mBus.post(new LogEvent.AddLogEvent(new AppLog.PromoLog.Previewed(
                                mPersistentPromo.getId(), AppLog.PromoLog.Type.PERSISTENT
                        )));
                    }
                    mWasPromoFullyCollapsed = false;
                }
            }
        });

        //handle the expanded layout's action button. putting it here
        //because we may want to collapse the sibling view
        mPersistentPromoExpandedLayout.setOnActionButtonClickedListener(new PersistentPromoExpandedLayout.OnActionButtonClickedListener() {
            @Override
            public void onActionButtonClicked(final String deeplinkUrl) {
                if (mPersistentPromo != null) {
                    mBus.post(new LogEvent.AddLogEvent(
                            new AppLog.PromoLog.Accepted(
                                    mPersistentPromo.getId(), AppLog.PromoLog.Type.PERSISTENT)));
                }

                if (!TextUtils.isEmpty(deeplinkUrl)) {
                    Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplinkUrl));
                    Utils.safeLaunchIntent(deepLinkIntent, getContext());
                }
                else {
                    //just collapse the offer view if no deep link
                    mPersistentPromoAppBarLayout.setExpanded(false, true);
                }
            }
        });

        //for logging only
        mPersistentPromoAppBarLayout.setOnPersistentPromoFullyExpandedListener(
                new PersistentPromoAppBarLayout.OnPersistentPromoFullyExpandedListener() {
                    @Override
                    public void onPersistentPromoFullyExpanded() {
                        if (mPersistentPromo != null) {
                            mBus.post(new LogEvent.AddLogEvent(
                                    new AppLog.PromoLog.FullyExpanded(
                                            mPersistentPromo.getId(),
                                            AppLog.PromoLog.Type.PERSISTENT
                                    )));
                        }
                    }
                });
    }

    /**
     * sets the visibility of the persistent promo views
     * @param visible
     */
    private void setPersistentPromoVisible(boolean visible) {
        /*
        note: can't just use setVisibility(GONE) because that doesn't properly hide the view
        if called after setVisibility(VISIBLE) in this CoordinatorLayout.
        it causes an empty space to be left behind, so doing this as a workaround
         */
        mPersistentPromoAppBarLayout.setExpanded(false, false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                mPersistentPromoAppBarLayout.getLayoutParams();
        params.height = visible ? LayoutParams.MATCH_PARENT : 0;
        mPersistentPromoAppBarLayout.setLayoutParams(params);
    }

    /**
     * updates the UI for the given model
     * @param persistentPromo
     */
    public void updateWithModel(@Nullable PersistentPromo persistentPromo) {
        mPersistentPromo = persistentPromo;
        if (!canDisplayPersistentPromo(persistentPromo)) {
            setPersistentPromoVisible(false);
            return;
        }
        setPersistentPromoVisible(true);
        if (mPersistentPromo != null) {
            mBus.post(new LogEvent.AddLogEvent(
                    new AppLog.PromoLog.Shown(
                            mPersistentPromo.getId(),
                            AppLog.PromoLog.Type.PERSISTENT
                    )));
        }

        mPersistentPromoExpandedLayout.updateWithModel(persistentPromo);
        mPersistentPromoAppBarLayout.updateWithModel(persistentPromo);
    }

    /**
     * given the model, whether we should show the promo view
     * @param persistentPromo
     * @return
     */
    private boolean canDisplayPersistentPromo(@Nullable PersistentPromo persistentPromo) {
        return persistentPromo != null && !TextUtils.isEmpty(persistentPromo.getPreviewText())
               && !TextUtils.isEmpty(persistentPromo.getActionText());
    }
}
