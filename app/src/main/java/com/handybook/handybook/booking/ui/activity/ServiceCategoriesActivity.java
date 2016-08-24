package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.deeplink.DeepLinkParams;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.usebutton.sdk.Button;

import javax.inject.Inject;

public final class ServiceCategoriesActivity extends MenuDrawerActivity
{
    @Inject
    PrefsManager mPrefsManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (mPrefsManager.getBoolean(PrefsKey.APP_FIRST_LAUNCH, true))
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(true, true)));
            mPrefsManager.setBoolean(PrefsKey.APP_FIRST_LAUNCH, false);
        }
        else
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, true)));
        }
        Button.checkForDeepLink(this, new Button.DeepLinkListener()
        {
            @Override
            public void onDeepLink(final Intent intent)
            {
                startActivity(intent);
            }

            @Override
            public void onNoDeepLink()
            {

            }
        });
    }

    @Override
    protected final Fragment createFragment()
    {
        //handle deep link params
        String serviceId = bundleOrUrlParam(DeepLinkParams.SERVICE_ID);
        String promoCode = bundleOrUrlParam(DeepLinkParams.PROMO_CODE);
        return ServiceCategoriesFragment.newInstance(serviceId, promoCode);
    }

    private String bundleOrUrlParam(final String name)
    {
        if (getIntent().hasExtra(name))
        {
            return getIntent().getStringExtra(name);
        }
        else
        {
            final Uri data = getIntent().getData();
            if (data != null && !data.isOpaque())
            {
                return data.getQueryParameter(name);
            }
        }
        return null;
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.home);
    }
}
