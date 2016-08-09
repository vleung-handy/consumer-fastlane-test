package com.handybook.handybook.module.referral.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.user.ReferralLog;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.ReferralChannels;
import com.handybook.handybook.module.referral.model.ReferralDescriptor;
import com.handybook.handybook.module.referral.model.ReferralInfo;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;
import com.handybook.handybook.util.ValidationUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralFragment extends InjectedFragment
{
    @Inject
    Bus mBus;

    @Bind(R.id.fragment_referral_content)
    View mReferralContent;
    @Bind(R.id.fragment_referral_title)
    TextView mTitle;
    @Bind(R.id.fragment_referral_subtitle)
    TextView mSubtitle;
    @Bind(R.id.fragment_referral_share_url)
    TextView mShareUrl;
    @Bind(R.id.fragment_referral_image)
    ImageView mImage;
    @Bind(R.id.fragment_referral_toolbar)
    Toolbar mToolbar;

    private ReferralDescriptor mReferralDescriptor;
    private ReferralChannels mReferralChannels;
    private boolean mIsReferralInfoFresh = false;
    private String mReferralUri = "http://fix.me"; //FIXME Fix the link

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

        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.free_cleanings));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.post(new LogEvent.AddLogEvent(new ReferralLog.ReferralOpenLog()));
        if (!mIsReferralInfoFresh)
        {
            requestPrepareReferrals();
        }
    }

    @OnClick(R.id.error_retry_button)
    public void requestPrepareReferrals()
    {
        showUiBlockers();
        mReferralContent.setVisibility(View.GONE);
        bus.post(new ReferralsEvent.RequestPrepareReferrals());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (requestCode == ActivityResult.PICK_ACTIVITY)
        {
            if (resultCode == Activity.RESULT_OK && intent != null)
            {
                final String resolvedChannel =
                        ReferralIntentUtil.addReferralIntentExtras(
                                getActivity(),
                                intent,
                                mReferralChannels
                        );
                final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (ValidationUtils.isNullOrEmpty(extraText))
                {
                    intent.putExtra(Intent.EXTRA_TEXT, mReferralDescriptor.getCouponCode());
                }
                final String applicationName =
                        ReferralIntentUtil.getApplicationNameFromIntent(getActivity(), intent);
                bus.post(new ReferralsEvent.OtherShareOptionsClicked(applicationName));
                launchShareIntent(intent, resolvedChannel);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void launchShareIntent(
            final Intent intent,
            @Nullable @ReferralChannels.Channel final String channel
    )
    {
        mIsReferralInfoFresh = false;
        if (channel != null)
        {
            final ReferralInfo referralInfo = mReferralChannels.getReferralInfoForChannel(channel);
            if (referralInfo != null)
            {
                bus.post(new ReferralsEvent.RequestConfirmReferral(referralInfo.getGuid()));
            }
        }
        Utils.safeLaunchIntent(intent, getActivity());
    }

    @Subscribe
    public void onReceivePrepareReferralsSuccess(
            ReferralsEvent.ReceivePrepareReferralsSuccess event
    )
    {
        mIsReferralInfoFresh = true;
        mReferralDescriptor = event.getReferralResponse().getReferralDescriptor();
        mReferralChannels = mReferralDescriptor.getReferralChannelsForSource(
                ReferralDescriptor.SOURCE_REFERRAL_PAGE
        );
        removeErrorLayout();
        removeUiBlockers();
        mReferralContent.setVisibility(View.VISIBLE);
        showReferralDetails();
        bus.post(new ReferralsEvent.ReferralScreenShown());
    }

    private void showReferralDetails()
    {
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
        mShareUrl.setText(mReferralDescriptor.getCouponCode());
        mTitle.setText(getString(R.string.referral_title_formatted, formattedReceiverCouponAmount,
                formattedSenderCreditAmount));
        mSubtitle.setText(getString(R.string.referral_subtitle_formatted, formattedReceiverCouponAmount,
                formattedSenderCreditAmount));
    }

    @Subscribe
    public void onReceivePrepareReferralsError(ReferralsEvent.ReceivePrepareReferralsError event)
    {
        String message = event.error.getMessage();
        if (ValidationUtils.isNullOrEmpty(message))
        {
            message = getString(R.string.error_fetching_connectivity_issue);
        }
        mReferralContent.setVisibility(View.GONE);
        showErrorLayout(message);
        removeUiBlockers();
    }

    @OnClick(R.id.fragment_referral_cta_more)
    public void onOtherShareCtaClicked()
    {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setType(ReferralIntentUtil.MIME_TYPE_PLAIN_TEXT);

        final Intent activityPickerIntent = new Intent();
        activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
        activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_using));
        activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
        startActivityForResult(activityPickerIntent, ActivityResult.PICK_ACTIVITY);
    }

    @OnClick(R.id.fragment_referral_button_sms)
    public void onSmsShareButtonClicked()
    {
        final ReferralInfo smsReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS);
        if (smsReferralInfo != null)
        {
            final Intent smsReferralIntent = ReferralIntentUtil.getSmsReferralIntent(
                    getActivity(),
                    smsReferralInfo
            );
            bus.post(new ReferralsEvent.InviteFriendsClicked());
            launchShareIntent(smsReferralIntent, ReferralChannels.CHANNEL_SMS);
        }
    }

    @OnClick(R.id.fragment_referral_button_email)
    public void onEmailShareButtonClicked()
    {
        final ReferralInfo emailReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_EMAIL);
        if (emailReferralInfo != null)
        {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailReferralInfo.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailReferralInfo.getMessage());
            bus.post(new ReferralsEvent.InviteFriendsClicked());
            launchShareIntent(emailIntent, ReferralChannels.CHANNEL_EMAIL);
        }
    }

    @OnClick(R.id.fragment_referral_share_url)
    public void onShareUrlClick()
    {
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        Uri copyUri = Uri.parse(mReferralUri);
        ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "URI", copyUri);
        clipboard.setPrimaryClip(clip);
        showToast("Sharing link copied to clipboard!");
    }

    private void showErrorLayout(String errorMessage)
    {
        final View errorLayout = getActivity().findViewById(R.id.error_layout);
        if (errorLayout != null)
        {
            final View errorText = getActivity().findViewById(R.id.error_text);
            if (errorText != null && errorText instanceof TextView)
            {
                ((TextView) errorText).setText(errorMessage);
            }
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void removeErrorLayout()
    {
        final View errorLayout = getActivity().findViewById(R.id.error_layout);
        if (errorLayout != null)
        {
            errorLayout.setVisibility(View.GONE);
        }
    }
}
