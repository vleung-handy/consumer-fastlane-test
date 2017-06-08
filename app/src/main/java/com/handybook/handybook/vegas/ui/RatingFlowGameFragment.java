package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.vegas.model.GameResponse;
import com.handybook.handybook.vegas.ui.view.ScratchView;
import com.handybook.handybook.vegas.ui.view.ScratchableInterface;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowGameFragment extends InjectedFragment {

    public static final String TAG = RatingFlowGameFragment.class.getName();

    private static final String KEY_GAME = "key::game";

    private String mGame;
    private double mRevealedPercentage = 0;

    @Bind(R.id.rfgf_percentage) TextView mPercentage;
    @Bind(R.id.rfgf_scratch_view) ScratchView mScratchView;

    public RatingFlowGameFragment() {
    }

    @NonNull
    public static RatingFlowGameFragment newInstance(GameResponse game) {
        RatingFlowGameFragment fragment = new RatingFlowGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame = getArguments().getString(KEY_GAME);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_rating_flow_game, container, false);
        ButterKnife.bind(this, view);
        mScratchView.setOnScratchCallback(new ScratchableInterface.OnScratchCallback() {

            @Override
            public void onScratch(float percentage) {
                updatePercentage(percentage);
            }

            @Override
            public void onDetach(boolean fingerDetach) {
                if (mRevealedPercentage > 60) {
                    mScratchView.setScratchAll(true);
                    updatePercentage(100);
                }
            }
        });

        updatePercentage(0);
        return view;
    }

    protected void updatePercentage(double percentage) {
        mRevealedPercentage = percentage;
        String percentage2decimal = String.format(Locale.getDefault(), "%.2f", percentage) + " %";
        mPercentage.setText(percentage2decimal);
    }

    @OnClick(R.id.rfgf_reset_button)
    void onReset() {
        mScratchView.resetView();
        mScratchView.setScratchAll(false);
        updatePercentage(0f);
    }

}
