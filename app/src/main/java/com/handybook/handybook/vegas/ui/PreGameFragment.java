package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.vegas.model.GameViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreGameFragment extends Fragment {

    public static final String TAG = PreGameFragment.class.getSimpleName();
    private static final String KEY_GAME_VM = "key::game_view_model";
    private GameViewModel mGameViewModel;

    public PreGameFragment() {
    }

    @NonNull
    public static PreGameFragment newInstance(GameViewModel gameViewModel) {
        PreGameFragment fragment = new PreGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_GAME_VM, gameViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameViewModel = (GameViewModel) getArguments().getSerializable(KEY_GAME_VM);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_pre_game, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.pre_game_play_button)
    void onNextClicked() {
        ((VegasActivity) getActivity()).continueFlow();
    }




}
