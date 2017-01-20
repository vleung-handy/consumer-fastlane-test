package com.handybook.handybook.onboarding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.handybook.handybook.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotSupportedActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dont_support);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_submit)
    public void submitClicked()
    {
        Toast.makeText(this, "Make API call to keep me updated", Toast.LENGTH_SHORT).show();
    }
}
