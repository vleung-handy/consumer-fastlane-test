package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.databinding.VegasPreGameFragmentBinding;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.vegas.logging.VegasLog;
import com.handybook.handybook.vegas.model.VegasGame;

public class PreGameFragment extends InjectedFragment {

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
        final VegasPreGameFragmentBinding binding = VegasPreGameFragmentBinding
                .inflate(inflater, container, false);
        final View view = binding.getRoot();
        binding.setFragment(this);
        binding.setPreGameInfo(mVegasGame.preGameInfo);
        bus.post(new LogEvent.AddLogEvent(new VegasLog.PromptScreenShown(mVegasGame)));
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre_game_play_button:
                bus.post(new LogEvent.AddLogEvent(new VegasLog.PlayNowSelected(mVegasGame)));
                ((VegasActivity) getActivity()).continueFlow();
                break;
        }
    }

}
