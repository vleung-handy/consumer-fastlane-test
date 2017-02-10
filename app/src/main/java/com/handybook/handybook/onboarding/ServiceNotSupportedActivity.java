package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.activity.OnboardActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is an activity that displays verbage to the user telling them the zip they entered
 * is not supported at the moment.
 */
public class ServiceNotSupportedActivity extends BaseActivity
{

    @Bind(R.id.not_supported_zip)
    TextView mTextZip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_not_supported);
        ButterKnife.bind(this);

        mTextZip.setText(
                getString(
                        R.string.service_not_supported_zip_formatted,
                        getIntent().getStringExtra(BundleKeys.ZIP
                        )
                ));
    }

    @OnClick(R.id.not_supported_try_another_zip)
    public void submitClicked()
    {
        Intent intent = new Intent(this, OnboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
