package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.OnboardingLog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is an activity that displays verbage to the user telling them the zip they entered
 * is not supported at the moment.
 */
public class ServiceNotSupportedActivity extends BaseActivity
{

    public static final String EXTRA_FROM_ZIP = "extra-from-zip";

    @Bind(R.id.not_supported_zip)
    TextView mTextZip;

    @Inject
    public Bus bus;

    boolean mFromZipFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_not_supported);
        ButterKnife.bind(this);

        mFromZipFragment = getIntent().getBooleanExtra(EXTRA_FROM_ZIP, false);

        String zip = getIntent().getStringExtra(BundleKeys.ZIP);

        mTextZip.setText(
                getString(
                        R.string.service_not_supported_zip_formatted,
                        zip
                ));

        bus.post(new LogEvent.AddLogEvent(new OnboardingLog.ZipNotSupportedLog(zip)));
    }

    @OnClick(R.id.not_supported_try_another_zip)
    public void submitClicked()
    {
        if (mFromZipFragment) {
            //if we're coming from the ZipFragment, then this button is essentially the same as
            //a backpress
            onBackPressed();
        }
        else {
            Intent intent = new Intent(this, OnboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
