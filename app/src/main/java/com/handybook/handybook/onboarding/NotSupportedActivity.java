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

public class NotSupportedActivity extends BaseActivity
{

    @Bind(R.id.text_zip)
    TextView mTextZip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dont_support);
        ButterKnife.bind(this);
        String zipPrefix = getString(R.string.zip_code);
        mTextZip.setText(zipPrefix + ": " + getIntent().getStringExtra(BundleKeys.ZIP));
    }

    @OnClick(R.id.button_try_another_zip)
    public void submitClicked()
    {
        startActivity(new Intent(this, OnboardActivity.class));
        finish();
    }
}
