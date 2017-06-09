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
import com.plattysoft.leonids.ParticleSystem;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowGameFragment extends InjectedFragment {

    public static final String TAG = RatingFlowGameFragment.class.getName();

    private static final String KEY_GAME = "key::game";

    private String mGame;
    private double mRevealedPercentage = 0;
    private int[] mParticleIds = {
            R.drawable.confetti_1,
            R.drawable.confetti_2,
            R.drawable.confetti_3,
            R.drawable.confetti_4,
            R.drawable.confetti_5,
            R.drawable.confetti_6,
            R.drawable.confetti_7,
            R.drawable.confetti_8,
            R.drawable.confetti_9,
            R.drawable.confetti_10,
            R.drawable.confetti_11,
            R.drawable.confetti_12

    };

    @Bind(R.id.rfgf_percentage) TextView mPercentage;
    @Bind(R.id.rfgf_scratch_view) ScratchView mScratchView;
    @Bind(R.id.particle_emitter_a) View mEmitterA;
    @Bind(R.id.particle_emitter_b) View mEmitterB;
    @Bind(R.id.particle_emitter_c) View mEmitterC;
    @Bind(R.id.particle_emitter_d) View mEmitterD;
    @Bind(R.id.particle_emitter_e) View mEmitterE;

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
            }
        });

        updatePercentage(0);
        return view;
    }

    protected void updatePercentage(double percentage) {
        mRevealedPercentage = percentage;
        if (percentage > 60) {
            mScratchView.revealAll();
            rollDownShades();
        }
        String percentage2decimal = String.format(Locale.getDefault(), "%.2f", percentage) + " %";
        mPercentage.setText(percentage2decimal);
    }

    private void rollDownShades() {

    }

    @OnClick(R.id.rfgf_reset_button)
    void onReset() {
        mScratchView.resetView();
        updatePercentage(0);
    }

    @OnClick(R.id.rfgf_blast_button)
    void blastConfetti() {
        int partsPerSec = 4;
        int emitTime = 2000;
        int maxParts = 400;
        int timeToLive = 10000;
        for (final int drawableId : mParticleIds) {
            emitFromView(mEmitterA, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            emitFromView(mEmitterB, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            emitFromView(mEmitterC, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            emitFromView(mEmitterD, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            emitFromView(mEmitterE, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
        }
    }

    private void emitFromView(
            final View sourceView,
            final int partNumPerSecond,
            final int emitTime,
            final int maxParticles,
            final int timeToLive,
            final int drawableId
    ) {
        new ParticleSystem(getActivity(), maxParticles, drawableId, timeToLive)
                .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                .setRotationSpeed(144)
                .setAcceleration(0.000685f, 90)
                .setScaleRange(0.3f, 0.5f)
                .emit(sourceView, partNumPerSecond, emitTime);
    }

}

