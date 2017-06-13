package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.vegas.model.GameResponse;
import com.handybook.handybook.vegas.ui.view.ScratchOffView;
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
    @Bind(R.id.rfgf_scratchoff_view) ScratchOffView mScratchOffView;
    @Bind(R.id.rfgf_sponge) ImageView mSponge;
    private boolean mIsSpongeAttached;
    private TranslateAnimation mSpongeAnimation;
    private int[] mSpongeLoc = {0, 0};

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
        mScratchOffView.setOnScratchListener(new ScratchOffView.OnScratchListener() {
            @Override
            public void onScratchStart(final float x, final float y) {
                attachSponge(x, y);
            }

            @Override
            public void onScratchMove(final float x, final float y) {
                moveSponge(x, y);
            }

            @Override
            public void onScratchStop(final float x, final float y) {
                detachSponge(x, y);
            }
        });
        mSponge.getLocationOnScreen(mSpongeLoc);
        updatePercentage(0);
        return view;
    }

    private float getSpongeX(final float x) {return x - mSponge.getWidth() / 2;}

    private float getSpongeY(final float y) {return y + mSponge.getHeight() / 2;}

    private void attachSponge(final float x, final float y) {
        mSponge.bringToFront();
        mSpongeAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.ABSOLUTE,
                getSpongeX(x),
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.ABSOLUTE,
                getSpongeY(y)
        );
        mSpongeAnimation.setDuration(1000);
        mSpongeAnimation.setFillAfter(true);
        mSpongeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                mIsSpongeAttached = true;
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        mSponge.startAnimation(mSpongeAnimation);
    }

    private void moveSponge(final float x, final float y) {
        if (mIsSpongeAttached) {
            mSponge.setX(getSpongeX(x));
            mSponge.setY(getSpongeY(y));
        }
    }

    private void detachSponge(final float x, final float y) {
        mSpongeAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.ABSOLUTE,
                mSpongeLoc[0],
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.ABSOLUTE,
                mSpongeLoc[1]
        );
        mSpongeAnimation.setDuration(1000);
        mSpongeAnimation.setFillAfter(true);
        mSpongeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                mIsSpongeAttached = false;
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        mSponge.startAnimation(mSpongeAnimation);

    }

    protected void updatePercentage(double percentage) {
        mRevealedPercentage = percentage;
        if (percentage > 60) {
            mScratchOffView.scratchOffAll();
            rollDownShades();
        }
        String percentage2decimal = String.format(Locale.getDefault(), "%.2f", percentage) + " %";
        mPercentage.setText(percentage2decimal);
    }

    private void rollDownShades() {

    }

    @OnClick(R.id.rfgf_reset_button)
    void onReset() {
        mScratchOffView.reset();
        updatePercentage(0);
    }

    @OnClick(R.id.rfgf_blast_button)
    void blastConfetti() {
        int partsPerSec = 4;
        int emitTime = 2000;
        int maxParts = 400;
        int timeToLive = 10000;
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int scrW = metrics.widthPixels;
        for (final int drawableId : mParticleIds) {
            for (int x = 0; x <= scrW; x += scrW / 5) {
                emitFromXY(x, -50, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            }
        }
    }

    private void emitFromXY(
            final int x,
            final int y,
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
                .emit(x, y, partNumPerSecond, emitTime);
    }

}

