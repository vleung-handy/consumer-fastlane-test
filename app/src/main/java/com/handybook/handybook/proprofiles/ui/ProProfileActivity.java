package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;

public final class ProProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(ProProfileFragment.TAG);
        if (fragment == null) {
            if (getIntent().getExtras() != null) {
                String providerId = getIntent().getExtras().getString(BundleKeys.PROVIDER_ID);
                @SourcePage.HandyLoggerSourcePage
                String pageSource = getIntent().getExtras().getString(BundleKeys.PAGE_SOURCE);
                if (TextUtils.isBlank(providerId)) {
                    Toast.makeText(this, R.string.default_error_string, Toast.LENGTH_SHORT).show();
                    Crashlytics.logException(new Exception(
                            "Provider id is null; unable to show profiles page"));
                    finish();
                    return;
                }
                FragmentTransaction ft = fm.beginTransaction();
                fragment = ProProfileFragment.newInstance(providerId, pageSource);
                ft.add(android.R.id.content, fragment, ProProfileFragment.TAG);
                ft.commit();
            }

        }
    }

    @NonNull
    public static Intent buildIntent(
            @NonNull Context context,
            @NonNull String providerId,
            String sourcePage
    ) {
        Intent intent = new Intent(context, ProProfileActivity.class);
        intent.putExtra(BundleKeys.PROVIDER_ID, providerId);
        intent.putExtra(BundleKeys.PAGE_SOURCE, sourcePage);
        return intent;
    }
}
