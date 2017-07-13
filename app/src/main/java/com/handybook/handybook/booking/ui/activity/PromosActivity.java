package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.deeplink.DeepLinkParams;

public final class PromosActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {

        //handle deep link params
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            String promoCode = parameters.getString(DeepLinkParams.PROMO_CODE);
            if (promoCode != null) {
                return PromosFragment.newInstance(promoCode);
            }
        }
        return PromosFragment.newInstance();
    }
}
