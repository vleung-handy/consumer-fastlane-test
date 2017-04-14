package com.handybook.handybook.referral.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.library.util.ValidationUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventType;
import com.handybook.handybook.logger.handylogger.model.user.NativeShareLog;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;
import com.handybook.handybook.referral.util.ReferralIntentUtil;

import static com.handybook.handybook.logger.handylogger.constants.EventContext.NATIVE_SHARE;

public abstract class BaseReferralFragment extends InjectedFragment {

    public static final String BASE_REFERRAL_URL = "handy.com/r/";
    public static final String BASE_REFERRAL_URL_SCHEME = "https://";

    protected ReferralDescriptor mReferralDescriptor;
    protected String mSource;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSource = getArguments().getString(BundleKeys.REFERRAL_PAGE_SOURCE);
        mReferralDescriptor
                = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
    }

    protected void launchGenericShareIntent() {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setType(ReferralIntentUtil.MIME_TYPE_PLAIN_TEXT);

        final Intent activityPickerIntent = new Intent();
        activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
        activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_using));
        activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
        sendShareButtonTappedLog("", ReferralChannels.CHANNEL_OTHER);
        startActivityForResult(activityPickerIntent, ActivityResult.PICK_ACTIVITY);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == ActivityResult.PICK_ACTIVITY && resultCode == Activity.RESULT_OK &&
            intent != null) {
            final String resolvedChannel =
                    ReferralIntentUtil.addReferralIntentExtras(
                            getActivity(),
                            intent,
                            getReferralChannels()
                    );
            final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (ValidationUtils.isNullOrEmpty(extraText)) {
                intent.putExtra(Intent.EXTRA_TEXT, getCouponCode());
            }
            launchShareIntent(intent, resolvedChannel);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected void shareEmailClicked() {
        final ReferralInfo emailReferralInfo =
                getReferralInfo(ReferralChannels.CHANNEL_EMAIL);
        if (emailReferralInfo != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailReferralInfo.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailReferralInfo.getMessage());
            emailIntent.putExtra(
                    Intent.EXTRA_BCC,
                    getResources().getStringArray(R.array.referral_email_bcc_array)
            );
            launchShareIntent(emailIntent, ReferralChannels.CHANNEL_EMAIL);
        }
        else {
            Crashlytics.logException(new Exception("Email referral info is null"));
        }
    }

    protected void shareSmsClicked() {
        final ReferralInfo smsReferralInfo =
                getReferralInfo(ReferralChannels.CHANNEL_SMS);
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

    private void launchShareIntent(
            @NonNull final Intent intent,
            @Nullable @ReferralChannels.Channel final String channel
    ) {
        onLaunchShareIntent();
        if (channel != null) {
            final ReferralInfo referralInfo = getReferralInfo(channel);
            if (referralInfo != null) {
                bus.post(new ReferralsEvent.RequestConfirmReferral(referralInfo.getGuid()));
                sendShareMethodSelectedLog(referralInfo.getGuid(), channel);
            }
        }
        Utils.safeLaunchIntent(intent, getActivity());
    }

    /**
     * This logs the generic share clicked, the action that launches the default android share intent
     */
    protected void sendShareButtonTappedLog(final String guid, final String referralMedium) {
        if (mReferralDescriptor != null) {
            String couponCode
                    = StringUtils.replaceWithEmptyIfNull(mReferralDescriptor.getCouponCode());

            if (TextUtils.isBlank(getProviderId())) {
                bus.post(new LogEvent.AddLogEvent(new NativeShareLog(
                        EventType.SHARE_BUTTON_TAPPED,
                        NATIVE_SHARE,
                        referralMedium,
                        guid,
                        couponCode,
                        mSource,
                        mReferralDescriptor.getSenderCreditAmount(),
                        mReferralDescriptor.getReceiverCouponAmount()
                )));
            }
            else {
                //this was a tapping of the share button for a specific pro member, log that
                //pro member's information
                bus.post(new LogEvent.AddLogEvent(new NativeShareLog.NativeShareProLog(
                        EventType.SHARE_BUTTON_TAPPED,
                        NATIVE_SHARE,
                        getProviderId(),
                        referralMedium,
                        guid,
                        couponCode,
                        mSource,
                        mReferralDescriptor.getSenderCreditAmount(),
                        mReferralDescriptor.getReceiverCouponAmount()
                )));
            }
        }
    }

    /**
     * Log the specific type of medium that was selected.
     * @param guid
     * @param referralMedium
     *
     * */
    protected void sendShareMethodSelectedLog(
            final String guid,
            final String referralMedium
    ) {
        if (mReferralDescriptor != null) {
            String couponCode
                    = StringUtils.replaceWithEmptyIfNull(mReferralDescriptor.getCouponCode());
            String identifier = StringUtils.replaceWithEmptyIfNull(guid);

            if (TextUtils.isBlank(getProviderId())) {
                bus.post(new LogEvent.AddLogEvent(new NativeShareLog(
                        EventType.SHARE_METHOD_SELECTED,
                        NATIVE_SHARE,
                        referralMedium,
                        identifier,
                        couponCode,
                        mSource,
                        mReferralDescriptor.getSenderCreditAmount(),
                        mReferralDescriptor.getReceiverCouponAmount()
                )));
            }
            else {
                //this was a tapping of the share button for a specific pro member, log that
                //pro member's information
                bus.post(new LogEvent.AddLogEvent(new NativeShareLog.NativeShareProLog(
                        EventType.SHARE_METHOD_SELECTED,
                        NATIVE_SHARE,
                        getProviderId(),
                        referralMedium,
                        identifier,
                        couponCode,
                        mSource,
                        mReferralDescriptor.getSenderCreditAmount(),
                        mReferralDescriptor.getReceiverCouponAmount()
                )));
            }

        }
    }

    protected void shareUrlClicked(String couponCode) {
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        final String sharingLink = BASE_REFERRAL_URL_SCHEME + BASE_REFERRAL_URL + couponCode;
        Uri copyUri = Uri.parse(sharingLink);
        ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "URI", copyUri);
        clipboard.setPrimaryClip(clip);
        showToast(R.string.referral_copied_to_clipboard);
    }

    @Nullable
    protected abstract String getProviderId();

    protected abstract ReferralChannels getReferralChannels();

    protected abstract String getCouponCode();

    protected abstract void onLaunchShareIntent();

    protected abstract ReferralInfo getReferralInfo(@ReferralChannels.Channel String channel);
}
