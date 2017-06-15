package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.databinding.FragmentPreGameBinding;
import com.handybook.handybook.vegas.model.VegasGame;

public class PreGameFragment extends Fragment {

    public static final String TAG = PreGameFragment.class.getSimpleName();
    private static final String KEY_GAME_VM = "key::game_view_model";

    private VegasGame mVegasGame;

    public PreGameFragment() {
    }

    @NonNull
    public static PreGameFragment newInstance(VegasGame vegasGame) {
        PreGameFragment fragment = new PreGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_GAME_VM, vegasGame);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVegasGame = (VegasGame) getArguments().getSerializable(KEY_GAME_VM);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        final FragmentPreGameBinding binding = FragmentPreGameBinding
                .inflate(inflater, container, false);
        final View view = binding.getRoot();
        binding.setFragment(this);
        binding.setPreGame(mVegasGame.preGame);
        init();
        return view;
    }

    private void init() {

    }

    void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre_game_x_button:
                ((VegasActivity) getActivity()).finish();
                break;
            case R.id.pre_game_play_button:
                ((VegasActivity) getActivity()).continueFlow();
                break;
        }
    }

}
