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
import com.handybook.handybook.module.referral.model.ReferralMedia;

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

    @Nullable
    @ReferralMedia.Medium
    public static String addReferralIntentExtras(
            final Context context, final Intent intent, final ReferralMedia referralMedia
    )
    {
        final String targetPackage = intent.getComponent().getPackageName();
        if (targetPackage.contains(PACKAGE_IDENTIFIER_GMAIL))
        {
            addReferralIntentExtrasForMail(intent, referralMedia, ReferralMedia.GMAIL);
            return ReferralMedia.GMAIL;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_GPLUS))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralMedia, ReferralMedia.GPLUS);
            return ReferralMedia.GPLUS;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_FACEBOOK))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralMedia, ReferralMedia.FACEBOOK);
            return ReferralMedia.FACEBOOK;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_TWITTER))
        {
            addReferralIntentExtrasForSocialMedia(intent, referralMedia, ReferralMedia.TWITTER);
            return ReferralMedia.TWITTER;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_SMS))
        {
            addReferralIntentExtrasForSms(intent, referralMedia);
            return ReferralMedia.SMS;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_MAIL))
        {
            addReferralIntentExtrasForMail(intent, referralMedia, ReferralMedia.EMAIL);
            return ReferralMedia.EMAIL;
        }
        return null;
    }

    private static void addReferralIntentExtrasForSocialMedia(
            final Intent intent,
            final ReferralMedia referralMedia,
            @NonNull @ReferralMedia.Medium final String medium
    )
    {
        final ReferralInfo referralInfo = referralMedia.getReferralInfo(medium);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_SUBJECT, referralInfo.getMessage());
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getUrl());
        }
    }

    private static void addReferralIntentExtrasForMail(
            final Intent intent,
            final ReferralMedia referralMedia,
            @NonNull @ReferralMedia.Medium final String medium
    )
    {
        final ReferralInfo referralInfo = referralMedia.getReferralInfo(medium);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_SUBJECT, referralInfo.getSubject());
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getMessage());
        }
    }

    private static void addReferralIntentExtrasForSms(
            final Intent intent,
            final ReferralMedia referralMedia
    )
    {
        final ReferralInfo referralInfo = referralMedia.getReferralInfo(ReferralMedia.SMS);
        if (referralInfo != null)
        {
            intent.putExtra(Intent.EXTRA_TEXT, referralInfo.getMessage());
        }
    }

    private static boolean canPackageHandleScheme(
            final Context context, final String targetPackage, final String scheme
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
