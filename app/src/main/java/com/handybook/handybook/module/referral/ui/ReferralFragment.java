package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.ReferralDescriptor;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.util.TextUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralFragment extends InjectedFragment
{
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.referral_content)
    View mReferralContent;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;
    @Bind(R.id.code)
    TextView mCode;
    @Bind(R.id.envelope)
    View mEnvelope;
    @Bind(R.id.envelope_shadow)
    View mEnvelopeShadow;
    @Bind(R.id.bling)
    View mBling;

    public static Fragment newInstance()
    {
        return new ReferralFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_referral, container, false);
        ButterKnife.bind(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        menuButton.setColor(getResources().getColor(R.color.white));
        mMenuButtonLayout.addView(menuButton);

        requestPrepareReferrals();

        return view;
    }

    @OnClick(R.id.error_retry_button)
    public void requestPrepareReferrals()
    {
        showUiBlockers();
        bus.post(new ReferralsEvent.RequestPrepareReferrals());
    }

    @Subscribe
    public void onReceivePrepareReferralsSuccess(
            ReferralsEvent.ReceivePrepareReferralsSuccess event
    )
    {
        removeErrorLayout();
        removeUiBlockers();
        mReferralContent.setVisibility(View.VISIBLE);
        final String currencyChar = userManager.getCurrentUser().getCurrencyChar();
        final ReferralDescriptor referralDescriptor =
                event.getReferralResponse().getReferralDescriptor();

        String formattedReceiverCouponAmount = TextUtils.formatPrice(
                referralDescriptor.getReceiverCouponAmount(), currencyChar, null);
        String formattedSenderCreditAmount = TextUtils.formatPrice(
                referralDescriptor.getSenderCreditAmount(), currencyChar, null);

        mCode.setText(referralDescriptor.getCouponCode());
        mTitle.setText(getString(R.string.referral_title, formattedReceiverCouponAmount,
                formattedSenderCreditAmount));
        mSubtitle.setText(getString(R.string.referral_subtitle, formattedReceiverCouponAmount,
                formattedSenderCreditAmount));

        mEnvelope.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.levitate));
        mEnvelopeShadow.startAnimation(
                AnimationUtils.loadAnimation(getActivity(), R.anim.expand_contract));
    }

    @Subscribe
    public void onReceivePrepareReferralsError(ReferralsEvent.ReceivePrepareReferralsError event)
    {
        String message = event.error.getMessage();
        if (message == null || message.isEmpty())
        {
            message = getString(R.string.error_fetching_connectivity_issue);
        }
        showErrorLayout(message);
        removeUiBlockers();
    }

    @OnClick(R.id.envelope)
    public void onEnvelopeClicked()
    {
        mBling.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.sparkle_fade));
    }

    @OnClick(R.id.share_button)
    public void onShareButtonClicked()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "JOHN1234");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_to)));
    }
}
