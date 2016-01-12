package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.deeplink.DeepLinkParams;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class ServiceCategoriesActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment() {

        //handle deep link params
        Bundle parameters = getIntent().getExtras();
        if(parameters != null)
        {
            String serviceId = parameters.getString(DeepLinkParams.SERVICE_ID);
            String promoCode = parameters.getString(DeepLinkParams.PROMO_CODE);
            return ServiceCategoriesFragment.newInstance(serviceId, promoCode);
        }
        return ServiceCategoriesFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.home);
    }
}
