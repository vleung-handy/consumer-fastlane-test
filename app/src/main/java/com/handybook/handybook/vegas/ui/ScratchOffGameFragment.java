package com.handybook.handybook.vegas.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.vegas.model.GameResponse;
import com.handybook.handybook.vegas.model.ScratchOffViewModel;
import com.handybook.handybook.vegas.ui.view.GameSymbolView;
import com.handybook.handybook.vegas.ui.view.ScratchOffView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScratchOffGameFragment extends InjectedFragment {

    public static final String TAG = ScratchOffGameFragment.class.getName();

    private static final String KEY_GAME_VM = "key::game_view_model";

    private ScratchOffViewModel mGameViewModel;
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

    private boolean mIsSpongeAttached;
    private boolean mIsResultSheetVisible;
    private TranslateAnimation mSpongeAnimation;

    @Bind(R.id.rfgf_background_image) ImageView mBackground;
    @Bind(R.id.rfgf_scratch_symbol_top_left) GameSymbolView mSymbolTL;
    @Bind(R.id.rfgf_scratch_symbol_top_right) GameSymbolView mSymbolTR;
    @Bind(R.id.rfgf_scratch_symbol_bottom_left) GameSymbolView mSymbolBL;
    @Bind(R.id.rfgf_scratch_symbol_bottom_right) GameSymbolView mSymbolBR;
    @Bind(R.id.rfgf_scratchoff_view) ScratchOffView mScratchOffView;
    @Bind(R.id.rfgf_bucket) ImageView mBucket;
    @Bind(R.id.rfgf_sponge_actor) ImageView mSpongeActor;
    @Bind(R.id.rfgf_result_sheet) ViewGroup mResultSheet;
    @Bind(R.id.rfgf_result_title) TextView mResultTitle;
    @Bind(R.id.rfgf_result_subtitle) TextView mResultSubtitle;
    @Bind(R.id.rfgf_result_header) TextView mResultHeader;
    @Bind(R.id.rfgf_result_symbol) GameSymbolView mResultSymbol;

    //TODO: Remove below
    @Bind(R.id.rfgf_percentage) TextView mPercentage;
    private float mSpongeStartX;
    private float mSpongeStartY;

    public ScratchOffGameFragment() {
    }

    @NonNull
    public static ScratchOffGameFragment newInstance(GameResponse game) {
        ScratchOffGameFragment fragment = new ScratchOffGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_GAME_VM, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameViewModel = (ScratchOffViewModel) getArguments().getSerializable(KEY_GAME_VM);
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
        mSpongeActor.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mSpongeActor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mSpongeStartX = mSpongeActor.getX();
                            mSpongeStartY = mSpongeActor.getY();
                            animateView(mSpongeActor, R.animator.vegas_sponge_restless);
                        }
                    });
        animateView(mSymbolTR, R.animator.vegas_symbol_winning);
        animateView(mSymbolBR, R.animator.vegas_symbol_winning);
        updatePercentage(0);
        return view;
    }

    private float getSpongeX(final float x) {
        final float newX = x - mSpongeActor.getWidth() * 0.65f;
        return newX - mScratchOffView.getX();
    }

    private float getSpongeY(final float y) {
        final float newY = y - mSpongeActor.getHeight() * 0.75f;
        return newY - mScratchOffView.getY();
    }

    private void attachSponge(final float x, final float y) {
        mSpongeActor.animate()
                    .setDuration(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .x(getSpongeX(x))
                    .y(getSpongeY(y))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(final Animator animation) {
                            mSpongeActor.setAlpha(1f);
                            mSpongeActor.setTranslationY(0f);
                            final AnimatorSet set = (AnimatorSet) mSpongeActor.getTag();
                            if (set != null) {
                                set.end();
                            }
                        }

                        @Override
                        public void onAnimationEnd(final Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(final Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(final Animator animation) {

                        }
                    })
                    .start();
    }

    private void moveSponge(final float x, final float y) {
        //        if (mIsSpongeAttached) {
        mSpongeActor.setX(getSpongeX(x));
        mSpongeActor.setY(getSpongeY(y));
        //        }
    }

    private void detachSponge(final float x, final float y) {
        mSpongeActor.animate()
                    .setDuration(100)
                    .x(mSpongeStartX)
                    .y(mSpongeStartY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(final Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            animateView(mSpongeActor, R.animator.vegas_sponge_restless);
                        }

                        @Override
                        public void onAnimationCancel(final Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(final Animator animation) {

                        }
                    })
                    .start();
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
        int timeToLive = 5000;
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int scrW = metrics.widthPixels;
        for (final int drawableId : mParticleIds) {
            for (int x = 0; x <= scrW; x += scrW / 5) {
                emitFromXY(x, -10, partsPerSec, emitTime, maxParts, timeToLive, drawableId);
            }
        }
    }

    @OnClick(R.id.rfgf_shades_button)
    void toggleShades() {
        if (mIsResultSheetVisible) {
            collapseView(mResultSheet);
            mIsResultSheetVisible = false;
        }
        else {
            expandView(mResultSheet);
            mIsResultSheetVisible = true;
        }
    }

    @NonNull
    private AnimatorSet animateView(final View view, final @AnimatorRes int resId) {
        final AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                resId
        );
        set.setTarget(view);
        set.start();
        view.setTag(set);
        return set;
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
                .setAcceleration(0.000785f, 90)
                .setScaleRange(0.3f, 0.5f)
                .emit(x, y, partNumPerSecond, emitTime);
    }

    public void expandView(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, -view.getHeight(), 0.0f);
        anim.setDuration(300);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        view.startAnimation(anim);
    }

    public void collapseView(final View view) {
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -view.getHeight());
        anim.setDuration(300);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(anim);
    }

}

