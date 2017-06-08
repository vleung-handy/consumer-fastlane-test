package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.proprofiles.ui.ProProfileFragment;
import com.handybook.handybook.vegas.model.GameResponse;

public class VegasDevActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(RatingFlowGameFragment.TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = RatingFlowGameFragment.newInstance(GameResponse.demo());
            ft.add(android.R.id.content, fragment, ProProfileFragment.TAG);
            ft.commit();
        }
    }
}
