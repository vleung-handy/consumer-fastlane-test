package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.ReferralChannels;
import com.handybook.handybook.module.referral.model.ReferralDescriptor;
import com.handybook.handybook.module.referral.model.ReferralInfo;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.util.StringUtils;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralDialogFragment extends BaseDialogFragment
{
    public static final String TAG = ReferralDialogFragment.class.getSimpleName();

    @Bind(R.id.dialog_referral_subtitle)
    TextView mSubtitle;

    private static final String REFERRAL_DESCRIPTOR = "referral_descriptor";
    private static final String[] REFERRALS_EMAIL_BCC_ARRAY = new String[]{"handy-referrals@handy.com"};
    private ReferralChannels mReferralChannels;
    private ReferralDescriptor mReferralDescriptor;
    private String mReferralContext;

    public static ReferralDialogFragment newInstance(final ReferralDescriptor referralDescriptor,
                                                     final String referralContext)
    {
        final ReferralDialogFragment dialogFragment = new ReferralDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(REFERRAL_DESCRIPTOR, referralDescriptor);
        arguments.putString(BundleKeys.REFERRAL_CONTEXT, referralContext);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReferralDescriptor = (ReferralDescriptor) getArguments()
                .getSerializable(REFERRAL_DESCRIPTOR);
        mReferralChannels = mReferralDescriptor
                .getReferralChannelsForSource(ReferralDescriptor.SOURCE_HIGH_RATING_MODAL);
        mReferralContext = getArguments().getString(BundleKeys.REFERRAL_CONTEXT);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.dialog_referral, container, true);
        ButterKnife.bind(this, view);
        showReferralDetails();
        allowDialogDismissable();
        return view;
    }

    @OnClick(R.id.dialog_referral_close_button)
    public void onCloseButtonClicked()
    {
        dismiss();
    }

    @OnClick(R.id.dialog_referral_email_button)
    public void onEmailButtonClicked()
    {
        final ReferralInfo emailReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_EMAIL);
        if (emailReferralInfo != null)
        {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailReferralInfo.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailReferralInfo.getMessage());
            emailIntent.putExtra(Intent.EXTRA_BCC, REFERRALS_EMAIL_BCC_ARRAY);
            launchShareIntent(emailIntent, ReferralChannels.CHANNEL_EMAIL);
            sendShareButtonTappedLog(emailReferralInfo.getGuid(), ReferralChannels.CHANNEL_EMAIL);
        }
        else
        {
            Crashlytics.logException(new Exception("Email referral info is null"));
        }
    }

    @OnClick(R.id.dialog_referral_text_button)
    public void onTextButtonClicked()
    {
        final ReferralInfo smsReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS);
        if (smsReferralInfo != null)
        {
            final Intent smsReferralIntent = ReferralIntentUtil.getSmsReferralIntent(
                    getActivity(),
                    smsReferralInfo
            );
            launchShareIntent(smsReferralIntent, ReferralChannels.CHANNEL_SMS);
            sendShareButtonTappedLog(smsReferralInfo.getGuid(), ReferralChannels.CHANNEL_SMS);
        }
        else
        {
            Crashlytics.logException(new Exception("SMS referral info is null"));
        }

    }

    private void showReferralDetails()
    {
        final String currencyChar = userManager.getCurrentUser().getCurrencyChar();
        final String formattedReceiverCouponAmount = TextUtils.formatPrice(
                mReferralDescriptor.getReceiverCouponAmount(),
                currencyChar,
                null
        );
        final String formattedSenderCreditAmount = TextUtils.formatPrice(
                mReferralDescriptor.getSenderCreditAmount(),
                currencyChar,
                null
        );
        mSubtitle.setText(getString(R.string.referral_dialog_subtitle_formatted,
                formattedReceiverCouponAmount, formattedSenderCreditAmount));
    }


    private void launchShareIntent(
            final Intent intent,
            @Nullable @ReferralChannels.Channel final String channel
    )
    {
        if (channel != null)
        {
            final ReferralInfo referralInfo = mReferralChannels.getReferralInfoForChannel(channel);
            if (referralInfo != null)
            {
                mBus.post(new ReferralsEvent.RequestConfirmReferral(referralInfo.getGuid()));
            }
        }
        Utils.safeLaunchIntent(intent, getActivity());
    }

    private void sendShareButtonTappedLog(final String guid, final String referralMedium)
    {
        if (mReferralDescriptor != null)
        {
            String couponCode = StringUtils.replaceWithEmptyIfNull(
                    mReferralDescriptor.getCouponCode());
            String identifier = StringUtils.replaceWithEmptyIfNull(guid);

            mBus.post(new LogEvent.AddLogEvent(
                    new ShareModalLog.ShareButtonTappedLog(
                            mReferralContext, referralMedium, identifier,
                            couponCode, mReferralDescriptor.getSenderCreditAmount(),
                            mReferralDescriptor.getReceiverCouponAmount())));
        }
    }
}
