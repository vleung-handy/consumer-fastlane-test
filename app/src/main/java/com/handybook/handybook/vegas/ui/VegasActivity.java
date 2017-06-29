package com.handybook.handybook.vegas.ui;

import android.content.Context;
import android.content.Intent;
import android.database.StaleDataException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.vegas.model.VegasGame;

public class VegasActivity extends BaseActivity {

    private static final String EXTRA_GAME_VIEW_MODEL = "extra::game_view_model";

    private State mNextState = State.PRE_GAME;
    private VegasGame mVegasGame;

    @NonNull
    public static Intent getIntent(@NonNull Context activity, @NonNull VegasGame vegasGame) {
        Intent intent = new Intent(activity, VegasActivity.class);
        intent.putExtra(EXTRA_GAME_VIEW_MODEL, vegasGame);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVegasGame = (VegasGame) getIntent().getSerializableExtra(EXTRA_GAME_VIEW_MODEL);
        if (mVegasGame.isInvalid()) {
            Crashlytics.logException(new StaleDataException(String.format(
                    "Invalid game type %s",
                    mVegasGame.type.toString()
            )));
            finish();
            return;
        }
        continueFlow();
    }

    public synchronized void continueFlow() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment;
        switch (mNextState) {
            case PRE_GAME:
                fragment = fm.findFragmentByTag(PreGameFragment.TAG);
                if (fragment == null) {
                    fragment = PreGameFragment.newInstance(mVegasGame);
                    ft.add(android.R.id.content, fragment, PreGameFragment.TAG);
                }
                mNextState = State.GAME;
                break;
            case GAME:
                fragment = fm.findFragmentByTag(GameFragment.TAG);
                if (fragment == null) {
                    fragment = GameFragment.newInstance(mVegasGame);
                    ft.add(android.R.id.content, fragment, GameFragment.TAG);
                }
                mNextState = State.END;
                break;
            case END:
                finish();
                return;

        }
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
        ft.commit();
    }

    private enum State {
        PRE_GAME,
        GAME,
        END
    }
}
