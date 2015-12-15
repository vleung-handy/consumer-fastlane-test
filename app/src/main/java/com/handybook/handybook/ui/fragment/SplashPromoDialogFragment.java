package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.model.response.SplashPromo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashPromoDialogFragment extends BaseDialogFragment
{
    private static final String BUNDLE_KEY_SPLASH_PROMO = "SPLASH_PROMO";

    @Bind(R.id.splash_promo_subtitle)
    TextView mSubtitle;
    @Bind(R.id.splash_promo_title)
    TextView mTitle;
    @Bind(R.id.splash_promo_action_button)
    Button mActionButton;

    private SplashPromo mSplashPromo;

    public static SplashPromoDialogFragment newInstance(SplashPromo splashPromo)
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
        super.onViewCreated(view, savedInstanceState);
        mTitle.setText(mSplashPromo.getTitle());
        mSubtitle.setText(mSplashPromo.getSubtitle());
        mActionButton.setText(mSplashPromo.getActionText());
    }

    @OnClick(R.id.splash_promo_action_button)
    public void onActionButtonClicked(View view)
    {
        dismiss();
    }
}
