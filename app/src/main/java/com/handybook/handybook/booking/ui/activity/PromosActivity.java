package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.deeplink.DeepLinkParams;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class PromosActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment() {

        //handle deep link params
        Bundle parameters = getIntent().getExtras();
        if(parameters != null)
        {
            String promoCode = parameters.getString(DeepLinkParams.PROMO_CODE);
            if(promoCode != null)
            {
                return PromosFragment.newInstance(promoCode);
            }
        }
        return PromosFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.promotions);
    }
}
