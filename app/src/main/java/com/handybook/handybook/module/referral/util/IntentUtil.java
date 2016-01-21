package com.handybook.handybook.module.referral.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.module.referral.model.ReferralInfo;
import com.handybook.handybook.module.referral.model.ReferralChannels;

import java.util.List;

public class IntentUtil
{
    public static final String PACKAGE_IDENTIFIER_GMAIL = "android.gm";
    public static final String PACKAGE_IDENTIFIER_GPLUS = "android.apps.plus";
    public static final String PACKAGE_IDENTIFIER_FACEBOOK = "facebook";
    public static final String PACKAGE_IDENTIFIER_TWITTER = "twitter";
    public static final String SCHEME_SMS = "sms:";
    public static final String SCHEME_MAIL = "mailto:";
    public static final String MIME_TYPE_PLAIN_TEXT = "text/plain";
    public static final String EXTRA_SMS_BODY = "sms_body";

    private IntentUtil() {}

    public static String getApplicationNameFromIntent(final Context context, final Intent intent)
    {
        try
        {
            final String packageName = intent.getComponent().getPackageName();
            final PackageManager packageManager = context.getPackageManager();
            final ApplicationInfo applicationInfo =
                    packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static Intent getSmsReferralIntent(
            final Context context, final ReferralInfo smsReferralInfo
    )
    {
        final String smsText = smsReferralInfo.getMessage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            final String defaultSmsPackageName =
                    Telephony.Sms.getDefaultSmsPackage(context);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(MIME_TYPE_PLAIN_TEXT);
            intent.putExtra(Intent.EXTRA_TEXT, smsText);
            if (defaultSmsPackageName != null)
            {
                intent.setPackage(defaultSmsPackageName);
            }
            return intent;
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(SCHEME_SMS));
            intent.putExtra(EXTRA_SMS_BODY, smsText);
            return intent;
        }
    }

    /**
     * Adds extras to the intent provided based on its component's package name or the scheme
     * it can handle. Returns a String object which corresponds to the channel used to obtain
     * the ReferralInfo object where the data added to the intent came from.
     *
     * @param context          context
     * @param intent           the intent to be modified
     * @param referralChannels the ReferralChannels object used to obtain ReferralInfo
     * @return channel associated with the intent
     */
    @Nullable
    @ReferralChannels.Channel
    public static String addReferralIntentExtras(
            final Context context, final Intent intent, final ReferralChannels referralChannels
    )
    {
        final String targetPackage = intent.getComponent().getPackageName();
        if (targetPackage.contains(PACKAGE_IDENTIFIER_GMAIL))
        {
            addReferralIntentExtrasForMail(intent, referralChannels,
                    ReferralChannels.CHANNEL_GMAIL);
            return ReferralChannels.CHANNEL_GMAIL;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_GPLUS))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralChannels,
                    ReferralChannels.CHANNEL_GPLUS);
            return ReferralChannels.CHANNEL_GPLUS;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_FACEBOOK))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralChannels,
                    ReferralChannels.CHANNEL_FACEBOOK);
            return ReferralChannels.CHANNEL_FACEBOOK;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_TWITTER))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralChannels,
                    ReferralChannels.CHANNEL_TWITTER);
            return ReferralChannels.CHANNEL_TWITTER;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_SMS))
        {
            addReferralIntentExtrasForSms(intent, referralChannels);
            return ReferralChannels.CHANNEL_SMS;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_MAIL))
        {
            addReferralIntentExtrasForMail(intent, referralChannels,
                    ReferralChannels.CHANNEL_EMAIL);
            return ReferralChannels.CHANNEL_EMAIL;
        }
        return null;
    }

    private static void addReferralIntentExtrasForSocialMedia(
            final Intent intent,
            final ReferralChannels referralChannels,
            @NonNull @ReferralChannels.Channel final String channel
    )
    {
        final ReferralInfo referralInfo = referralChannels.getReferralInfoForChannel(channel);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_SUBJECT, referralInfo.getMessage());
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getUrl());
        }
    }

    private static void addReferralIntentExtrasForMail(
            final Intent intent,
            final ReferralChannels referralChannels,
            @NonNull @ReferralChannels.Channel final String channel
    )
    {
        final ReferralInfo referralInfo = referralChannels.getReferralInfoForChannel(channel);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_SUBJECT, referralInfo.getSubject());
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getMessage());
        }
    }

    private static void addReferralIntentExtrasForSms(
            @NonNull final Intent intent,
            @NonNull final ReferralChannels referralChannels
    )
    {
        final ReferralInfo referralInfo =
                referralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getMessage());
        }
    }

    private static boolean canPackageHandleScheme(
            @NonNull final Context context,
            @NonNull final String targetPackage,
            @NonNull final String scheme
    )
    {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setData(Uri.parse(scheme));
        dummyIntent.setType(MIME_TYPE_PLAIN_TEXT);
        List<ResolveInfo> resolveInfos =
                context.getPackageManager().queryIntentActivities(dummyIntent, 0);
        for (final ResolveInfo resolveInfo : resolveInfos)
        {
            final String potentialHandlerPackage = resolveInfo.activityInfo.packageName;
            if (potentialHandlerPackage.equalsIgnoreCase(targetPackage))
            {
                return true;
            }
        }
        return false;
    }

}
