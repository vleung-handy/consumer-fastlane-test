package com.handybook.handybook.booking.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.fragment.ZipFragment;
import com.handybook.handybook.core.ui.activity.BaseActivity;

import java.io.Serializable;
import java.util.List;

public class ZipActivity extends BaseActivity {

    public static final String EXTRA_SERVICES = "extra-services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        ZipFragment zipFragment = (ZipFragment) getSupportFragmentManager().findFragmentByTag(
                ZipFragment.class.getName());

        if (zipFragment == null) {
            zipFragment = ZipFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.zip_activity_container, zipFragment, ZipFragment.class.getName())
                    .commit();
        }
    }

    public void finishWithResults(List<Service> services) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_SERVICES, (Serializable) services);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
