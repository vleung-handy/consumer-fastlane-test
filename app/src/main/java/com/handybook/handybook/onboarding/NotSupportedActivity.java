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

    @Bind(R.id.not_supported_zip)
    TextView mTextZip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_supported);
        ButterKnife.bind(this);

        mTextZip.setText(
                getString(
                        R.string.not_supported_zip_formatted,
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
