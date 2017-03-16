package com.handybook.handybook.referral.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.library.util.ValidationUtils;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralInfo;
import com.handybook.handybook.referral.util.ReferralIntentUtil;

public abstract class BaseReferralFragment extends InjectedFragment {

    public static final String BASE_REFERRAL_URL = "handy.com/r/";
    public static final String BASE_REFERRAL_URL_SCHEME = "https://";

    protected void launchGenericShareIntent() {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setType(ReferralIntentUtil.MIME_TYPE_PLAIN_TEXT);

        final Intent activityPickerIntent = new Intent();
        activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
        activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_using));
        activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
        startActivityForResult(activityPickerIntent, ActivityResult.PICK_ACTIVITY);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == ActivityResult.PICK_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
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
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected void launchShareIntent(
            final Intent intent,
            @Nullable @ReferralChannels.Channel final String channel
    ) {
        onLaunchShareIntent();
        if (channel != null) {
            final ReferralInfo referralInfo = getReferralInfo(channel);
            if (referralInfo != null) {
                bus.post(new ReferralsEvent.RequestConfirmReferral(referralInfo.getGuid()));
            }
        }
        Utils.safeLaunchIntent(intent, getActivity());
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

    protected abstract ReferralChannels getReferralChannels();

    protected abstract String getCouponCode();

    protected abstract void onLaunchShareIntent();

    protected abstract ReferralInfo getReferralInfo(@ReferralChannels.Channel String channel);
}
