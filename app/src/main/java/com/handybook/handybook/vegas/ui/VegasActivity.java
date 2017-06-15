package com.handybook.handybook.vegas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.vegas.model.GameViewModel;

public class VegasActivity extends BaseActivity {

    private static final String EXTRA_GAME_VIEW_MODEL = "extra::game_view_model";

    private State mNextState = State.PRE_GAME;
    private GameViewModel mGameViewModel;

    @NonNull
    public static Intent getIntent(Activity activity, GameViewModel gameViewModel) {
        Intent intent = new Intent(activity, VegasActivity.class);
        intent.putExtra(EXTRA_GAME_VIEW_MODEL, gameViewModel);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        mGameViewModel = (GameViewModel) getIntent().getSerializableExtra(EXTRA_GAME_VIEW_MODEL);
        mGameViewModel = GameViewModel.demo(); //FIXME: Replace this line with above
        if (mGameViewModel == null || mGameViewModel.isInvalid()) {
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
                    fragment = PreGameFragment.newInstance(mGameViewModel);
                    ft.add(android.R.id.content, fragment, PreGameFragment.TAG);
                }
                mNextState = State.GAME;
                break;
            case GAME:
                fragment = fm.findFragmentByTag(ScratchOffGameFragment.TAG);
                if (fragment == null) {
                    fragment = ScratchOffGameFragment.newInstance(mGameViewModel);
                    ft.add(android.R.id.content, fragment, ScratchOffGameFragment.TAG);
                }
                mNextState = State.END;
                break;
            case END:
                finish();
                return;

        }
        ft.commit();
    }

    private enum State {
        PRE_GAME,
        GAME,
        END
    }
}
