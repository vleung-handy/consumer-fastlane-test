package com.handybook.handybook.promos.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashPromoDialogFragment extends BaseDialogFragment {

    private static final String BUNDLE_KEY_SPLASH_PROMO = "SPLASH_PROMO";

    @Bind(R.id.splash_promo_header_image)
    ImageView mUrlImageView;
    @Bind(R.id.splash_promo_subtitle)
    TextView mSubtitle;
    @Bind(R.id.splash_promo_title)
    TextView mTitle;
    @Bind(R.id.splash_promo_action_button)
    Button mActionButton;

    private SplashPromo mSplashPromo;

    public static SplashPromoDialogFragment newInstance(@NonNull SplashPromo splashPromo) {
        SplashPromoDialogFragment splashPromoDialogFragment =
                new SplashPromoDialogFragment();
        splashPromoDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_SPLASH_PROMO, splashPromo);
        splashPromoDialogFragment.setArguments(bundle);

        return splashPromoDialogFragment;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_splash_promo, container, true);
        ButterKnife.bind(this, view);
        mSplashPromo = getArguments().getParcelable(BUNDLE_KEY_SPLASH_PROMO);

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUrlImageView.getViewTreeObserver()
                     .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                         @Override
                         public void onGlobalLayout() {
                             try //picasso doesn't catch all errors like empty URL!
                             {
                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                     mUrlImageView.getViewTreeObserver()
                                                  .removeOnGlobalLayoutListener(this);
                                 }
                                 else {
                                     mUrlImageView.getViewTreeObserver()
                                                  .removeGlobalOnLayoutListener(this);
                                 }
                                 //we are going to use an animated placeholder eventually
                                 Picasso.with(getContext()).
                                         load(mSplashPromo.getImageUrl()).
                                                error(R.drawable.banner_image_load_failed).
                                                resize(mUrlImageView.getWidth(), 0).
                                                into(mUrlImageView);
                            /*
                            need to call resize() because ImageView
                            can have rounding errors when scaling
                            that causes a 1px margin around the image and
                            there's no known way of scaling it against one dimension
                            using the view params
                             */
                             }
                             catch (Exception e) {
                                 Crashlytics.log("Exception in loading image url: '" +
                                                 mSplashPromo.getImageUrl() + "' " +
                                                 "with Picasso for splash promo id " +
                                                 mSplashPromo.getId());
                                 Crashlytics.logException(e);
                             }
                         }
                     });

        mTitle.setText(mSplashPromo.getTitle());
        mSubtitle.setText(mSplashPromo.getSubtitle());
        mActionButton.setText(mSplashPromo.getActionText());

        //TODO: will consolidate
        mBus.post(new SplashPromoEvent.RequestMarkSplashPromoAsDisplayed(mSplashPromo));
        mBus.post(new LogEvent.AddLogEvent(new AppLog.PromoLog.Shown(
                mSplashPromo.getId(),
                AppLog.PromoLog.Type.SPLASH
        )));
    }

    @OnClick(R.id.splash_promo_action_button)
    public void onActionButtonClicked(View view) {
        //TODO: will consolidate
        mBus.post(new SplashPromoEvent.RequestMarkSplashPromoAsAccepted(mSplashPromo));
        mBus.post(new LogEvent.AddLogEvent(new AppLog.PromoLog.Accepted(
                mSplashPromo.getId(),
                AppLog.PromoLog.Type.SPLASH
        )));
        String deepLink = mSplashPromo.getDeepLinkUrl();
        if (deepLink != null) {
            Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            Utils.safeLaunchIntent(deepLinkIntent, getContext());
        }
        else {
            //can we have a splash promo whose action button only dismisses?
            Crashlytics.logException(new Exception(
                    "Deeplink url for splash promo " + mSplashPromo.getId() + " is null"));
        }
        dismiss();
    }
}
