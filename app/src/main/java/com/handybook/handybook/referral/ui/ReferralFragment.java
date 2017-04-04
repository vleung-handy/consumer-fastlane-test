package com.handybook.handybook.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.view.snowflake.SnowView;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.ValidationUtils;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;
import com.handybook.handybook.referral.util.ReferralIntentUtil;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralFragment extends BaseReferralFragment {

    private static final String[] REFERRALS_EMAIL_BCC_ARRAY = new String[]{
            "handy-referrals@handy.com"
    };

    @Bind(R.id.fragment_referral_content)
    View mReferralContent;
    @Bind(R.id.fragment_referral_title)
    TextView mTitle;
    @Bind(R.id.fragment_referral_subtitle)
    TextView mSubtitle;
    @Bind(R.id.fragment_referral_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fragment_referral_snowview)
    SnowView mSnowView;

    private ReferralChannels mReferralChannels;
    private boolean mIsReferralInfoFresh = false;
    private boolean mHideToolbar;

    public static Fragment newInstance(final @Nullable String source) {
        return newInstance(null, source, false);
    }

    public static Fragment newInstance(
            @Nullable final ReferralDescriptor referralDescriptor,
            @Nullable final String source,
            final boolean hideToolbar
    ) {
        ReferralFragment fragment = new ReferralFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        args.putString(BundleKeys.REFERRAL_PAGE_SOURCE, source);
        args.putBoolean(BundleKeys.REFERRAL_HIDE_TOOLBAR, hideToolbar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHideToolbar = getArguments().getBoolean(BundleKeys.REFERRAL_HIDE_TOOLBAR);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_referral, container, false);
        ButterKnife.bind(this, view);
        mSnowView.setVisibility(mConfigurationManager.getPersistentConfiguration().isSnowEnabled()
                                ? View.VISIBLE
                                : View.GONE);

        //this fragment can sometimes be embedded in a container that already has a toolbar.
        if (!mHideToolbar) {
            setupToolbar(mToolbar, getString(R.string.free_cleanings));
            mToolbar.setNavigationIcon(null);
        }
        else {
            mToolbar.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if we already have this, don't need to request
        if (mReferralDescriptor == null) {
            if (!mIsReferralInfoFresh) {
                requestPrepareReferrals();
            }
        }
        else {
            onReferralDescriptorReceived(mReferralDescriptor);
        }
    }

    @Override
    public void onPause() {
        removeUiBlockers();
        super.onPause();
    }

    @OnClick(R.id.error_retry_button)
    public void requestPrepareReferrals() {
        showUiBlockers();
        mReferralContent.setVisibility(View.GONE);
        bus.post(new ReferralsEvent.RequestPrepareReferrals(
                false,
                ReferralsManager.Source.REFERRAL_PAGE
        ));
    }




    @Subscribe
    public void onReceivePrepareReferralsSuccess(
            ReferralsEvent.ReceivePrepareReferralsSuccess event
    ) {
        if (event.isForDialog()) {
            return;
        }
        mIsReferralInfoFresh = true;
        onReferralDescriptorReceived(event.getReferralResponse().getReferralDescriptor());
    }

    public void onReferralDescriptorReceived(ReferralDescriptor referralDescriptor) {
        mReferralDescriptor = referralDescriptor;
        mReferralChannels = mReferralDescriptor.getReferralChannelsForSource(
                ReferralDescriptor.SOURCE_REFERRAL_PAGE
        );
        removeErrorLayout();
        removeUiBlockers();
        mReferralContent.setVisibility(View.VISIBLE);
        showReferralDetails();
    }

    private void showReferralDetails() {
        final String currencyChar = userManager.getCurrentUser().getCurrencyChar();
        String formattedReceiverCouponAmount = TextUtils.formatPrice(
                mReferralDescriptor.getReceiverCouponAmount(),
                currencyChar,
                null
        );
        String formattedSenderCreditAmount = TextUtils.formatPrice(
                mReferralDescriptor.getSenderCreditAmount(),
                currencyChar,
                null
        );
        mTitle.setText(getString(R.string.referral_title));
        mSubtitle.setText(getString(R.string.referral_subtitle_formatted,
                                    formattedReceiverCouponAmount, formattedSenderCreditAmount
        ));
    }

    @Subscribe
    public void onReceivePrepareReferralsError(ReferralsEvent.ReceivePrepareReferralsError event) {
        String message = event.error.getMessage();
        if (ValidationUtils.isNullOrEmpty(message)) {
            message = getString(R.string.error_fetching_connectivity_issue);
        }
        mReferralContent.setVisibility(View.GONE);
        showErrorLayout(message);
        removeUiBlockers();
    }

    @OnClick(R.id.fragment_referral_cta_more)
    public void onOtherShareCtaClicked() {
        launchGenericShareIntent();
    }

    @OnClick(R.id.fragment_referral_button_sms)
    public void onSmsShareButtonClicked() {
        final ReferralInfo smsReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS);
        if (smsReferralInfo != null) {
            final Intent smsReferralIntent = ReferralIntentUtil.getSmsReferralIntent(
                    getActivity(),
                    smsReferralInfo
            );
            launchShareIntent(smsReferralIntent, ReferralChannels.CHANNEL_SMS);
        }
        else {
            Crashlytics.logException(new Exception("SMS referral info is null"));
        }
    }

    @OnClick(R.id.fragment_referral_button_email)
    public void onEmailShareButtonClicked() {
        final ReferralInfo emailReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_EMAIL);
        if (emailReferralInfo != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailReferralInfo.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailReferralInfo.getMessage());
            emailIntent.putExtra(Intent.EXTRA_BCC, REFERRALS_EMAIL_BCC_ARRAY);
            launchShareIntent(emailIntent, ReferralChannels.CHANNEL_EMAIL);
        }
        else {
            Crashlytics.logException(new Exception("Email referral info is null"));
        }
    }

    private void showErrorLayout(String errorMessage) {
        final View errorLayout = getActivity().findViewById(R.id.error_layout);
        if (errorLayout != null) {
            final View errorText = getActivity().findViewById(R.id.error_text);
            if (errorText != null && errorText instanceof TextView) {
                ((TextView) errorText).setText(errorMessage);
            }
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void removeErrorLayout() {
        final View errorLayout = getActivity().findViewById(R.id.error_layout);
        if (errorLayout != null) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getProviderId() {
        return null;
    }

    @Override
    protected ReferralChannels getReferralChannels() {
        return mReferralChannels;
    }

    @Override
    protected String getCouponCode() {
        return mReferralDescriptor.getCouponCode();
    }

    @Override
    protected void onLaunchShareIntent() {
        mIsReferralInfoFresh = false;
    }

    @Override
    protected ReferralInfo getReferralInfo(@ReferralChannels.Channel final String channel) {
        return mReferralChannels.getReferralInfoForChannel(channel);
    }
}
