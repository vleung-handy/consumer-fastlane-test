package com.handybook.handybook.booking.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.deeplink.DeepLinkParams;

import javax.inject.Inject;

public final class ServiceCategoriesActivity extends SingleFragmentActivity {

    @Inject
    SecurePreferencesManager mSecurePreferencesManager;

    public static Intent getIntent(Activity activity, Intent startupIntent) {
        Intent intent = new Intent(activity, ServiceCategoriesActivity.class);
        if (startupIntent != null) {
            intent.putExtras(startupIntent);
        }
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserManager.isUserLoggedIn()) {
            startActivity(new Intent(this, BottomNavActivity.class));
            finish();
        }
    }

    @Override
    protected final Fragment createFragment() {
        //handle deep link params
        String serviceId = bundleOrUrlParam(DeepLinkParams.SERVICE_ID);
        String promoCode = bundleOrUrlParam(DeepLinkParams.PROMO_CODE);

        return ServiceCategoriesHomeFragment.newInstance(serviceId, promoCode);
    }

    private String bundleOrUrlParam(final String name) {
        if (getIntent().hasExtra(name)) {
            return getIntent().getStringExtra(name);
        }
        else {
            final Uri data = getIntent().getData();
            if (data != null && !data.isOpaque()) {
                return data.getQueryParameter(name);
            }
        }
        return null;
    }
}
