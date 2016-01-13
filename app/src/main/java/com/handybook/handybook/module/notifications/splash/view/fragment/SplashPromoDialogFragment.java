package com.handybook.handybook.module.notifications.splash.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.util.Utils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashPromoDialogFragment extends BaseDialogFragment
{
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

    public static SplashPromoDialogFragment newInstance(@NonNull SplashPromo splashPromo)
    {
        SplashPromoDialogFragment splashPromoDialogFragment =
                new SplashPromoDialogFragment();
        splashPromoDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_SPLASH_PROMO, splashPromo);
        splashPromoDialogFragment.setArguments(bundle);

        return splashPromoDialogFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_splash_promo, container, true);
        ButterKnife.bind(this, view);
        mSplashPromo = getArguments().getParcelable(BUNDLE_KEY_SPLASH_PROMO);

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        //TODO: placeholder image?
        super.onViewCreated(view, savedInstanceState);
        Picasso.with(getContext()).
                load(mSplashPromo.getImageUrl()).
                placeholder(R.drawable.ic_noimage).
                error(R.drawable.ic_noimage).
                into(mUrlImageView);
        mTitle.setText(mSplashPromo.getTitle());
        mSubtitle.setText(mSplashPromo.getSubtitle());
        mActionButton.setText(mSplashPromo.getActionText());

        //TODO: will consolidate
        mBus.post(new HandyEvent.RequestMarkSplashPromoAsDisplayed(mSplashPromo));
        mBus.post(new MixpanelEvent.TrackSplashPromoShow(mSplashPromo.getId()));
    }

    @OnClick(R.id.splash_promo_action_button)
    public void onActionButtonClicked(View view)
    {
        //TODO: will consolidate
        mBus.post(new HandyEvent.RequestMarkSplashPromoAsAccepted(mSplashPromo));
        mBus.post(new MixpanelEvent.TrackSplashPromoAction(mSplashPromo.getId()));
        String deepLink = mSplashPromo.getDeepLinkUrl();
        if(deepLink != null)
        {
            Intent deepLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            Utils.safeLaunchIntent(deepLinkIntent, getContext());
        }
        else
        {
            //can we have a splash promo whose action button only dismisses?
            Crashlytics.logException(new Exception("Deeplink url for splash promo " + mSplashPromo.getId() + " is null"));
        }
        dismiss();
    }
}
