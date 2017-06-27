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
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.vegas.VegasManager;
import com.handybook.handybook.vegas.model.GameSymbol;
import com.handybook.handybook.vegas.model.VegasGame;
import com.handybook.handybook.vegas.ui.view.GameSymbolView;
import com.handybook.handybook.vegas.ui.view.MaybeScrollView;
import com.handybook.handybook.vegas.ui.view.ScratchOffView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.HashSet;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameFragment extends InjectedFragment {

    public static final String TAG = GameFragment.class.getName();

    private static final String KEY_GAME_VM = "key::game_view_model";
    public static final double RATIO_TO_REVEAL = .65;
    public static final int DELAY_SHADE_DOWN_MS = 3000;

    @Inject
    VegasManager mVegasManager;

    private VegasGame mVegasGame;
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

    private boolean mIsResultSheetVisible;
    private float mSpongeStartX;
    private float mSpongeStartY;

    @BindView(R.id.rfgf_background_image) ImageView mBackground;
    @BindView(R.id.rfgf_scroll_container) MaybeScrollView mScrollView;
    @BindView(R.id.rfgf_scratch_symbol_top_left) GameSymbolView mSymbolTL;
    @BindView(R.id.rfgf_scratch_symbol_top_right) GameSymbolView mSymbolTR;
    @BindView(R.id.rfgf_scratch_symbol_bottom_left) GameSymbolView mSymbolBL;
    @BindView(R.id.rfgf_scratch_symbol_bottom_right) GameSymbolView mSymbolBR;
    @BindView(R.id.rfgf_scratchoff_view) ScratchOffView mScratchOffView;
    @BindView(R.id.rfgf_bucket) ImageView mBucket;
    @BindView(R.id.rfgf_sponge_actor) ImageView mSpongeActor;
    @BindView(R.id.rfgf_result_sheet) ViewGroup mResultSheet;
    @BindView(R.id.rfgf_result_title) TextView mResultTitle;
    @BindView(R.id.rfgf_result_subtitle) TextView mResultSubtitle;
    @BindView(R.id.rfgf_result_header) TextView mResultHeader;
    @BindView(R.id.rfgf_result_symbol) GameSymbolView mResultSymbol;
    @BindView(R.id.rfgf_banner_bottom_container) ViewGroup mBottomBannerContainer;
    @BindView(R.id.rfgf_banner_bottom_text) TextView mBottomBannerText;

    //TODO: Remove below
    @BindView(R.id.rfgf_percentage) TextView mPercentage;

    public GameFragment() {
    }

    @NonNull
    public static GameFragment newInstance(VegasGame vegasGame) {
        GameFragment fragment = new GameFragment();
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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_rating_flow_game, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        initScratchOffView();
        initSponge();
        initSymbols();
        updatePercentage(0);
    }

    private void initSymbols() {
        mSymbolTL.setSymbol(mVegasGame.result.symbols[0]);
        mSymbolTR.setSymbol(mVegasGame.result.symbols[1]);
        mSymbolBL.setSymbol(mVegasGame.result.symbols[2]);
        mSymbolBR.setSymbol(mVegasGame.result.symbols[3]);

    }

    private void initSponge() {
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
    }

    private void initScratchOffView() {
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
    }

    private float getSpongeX(final float x) {
        final float newX = x - mSpongeActor.getWidth() * 0.65f;
        return newX - mScratchOffView.getLeft();
    }

    private float getSpongeY(final float y) {
        final float newY = y - mSpongeActor.getHeight() * 0.75f;
        return newY - mScratchOffView.getTop();
    }

    private void attachSponge(final float x, final float y) {
        mScrollView.setScrollingEnabled(false);
        mSpongeActor.animate()
                    .setDuration(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .x(getSpongeX(x))
                    .y(getSpongeY(y))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(final Animator animation) {
                            mSpongeActor.clearAnimation();
                            mSpongeActor.setAlpha(1f);
                            mSpongeActor.setImageAlpha(255);
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
        mSpongeActor.setX(getSpongeX(x));
        mSpongeActor.setY(getSpongeY(y));
    }

    private void detachSponge(final float x, final float y) {
        updatePercentage(mScratchOffView.getScratchedOffRatio(10));
        mScrollView.setScrollingEnabled(true);
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

    protected void updatePercentage(double ratio) {
        String txt = String.format(Locale.getDefault(), "%.2f", ratio);
        mPercentage.setText(txt);
        if (ratio > RATIO_TO_REVEAL) {
            revealClaim();
        }
    }

    private void revealClaim() {

        mScratchOffView.scratchOffAll();
        mScratchOffView.setOnScratchListener(null);
        animateWinningSymbols();
        blastConfetti();
        Runnable delayedTask = new Runnable() {
            @Override
            public void run() {
                rollDownShades();
                swipeRightBucket();
                swipeRightSponge();
                swipeDownBottomBanner();
            }
        };
        mScratchOffView.postDelayed(delayedTask, DELAY_SHADE_DOWN_MS);
    }

    private void animateWinningSymbols() {
        final HashSet<GameSymbol> existingSymbols = new HashSet<>();
        GameSymbol winningSymbol = null;
        for (GameSymbol symbol : mVegasGame.result.symbols) {
            if (existingSymbols.contains(symbol)) {
                winningSymbol = symbol;
                break;
            }
            existingSymbols.add(symbol);
        }
        if (winningSymbol == null) { return;}
        for (GameSymbolView symbolView : new GameSymbolView[]{
                mSymbolTL,
                mSymbolTR,
                mSymbolBL,
                mSymbolBR
        }) {
            if (winningSymbol.equals(symbolView.getSymbol())) {
                animateView(symbolView, R.animator.vegas_symbol_winning);
            }
        }
    }

    private void swipeRightBucket() {
        animateView(mBucket, R.animator.vegas_swipe_out_right);
    }

    private void swipeRightSponge() {
        animateView(mSpongeActor, R.animator.vegas_swipe_out_right);
    }

    private void swipeDownBottomBanner() {
        animateView(mBottomBannerContainer, R.animator.vegas_swipe_out_down);
    }

    @OnClick(R.id.rfgf_reset_button)
    void onReset() {
        mScratchOffView.reset();
        updatePercentage(0);
    }

    @OnClick(R.id.rfgf_blast_button)
    void blastConfetti() {
        int partsPerSec = 6;
        int emitTime = 1000;
        int maxParts = 300;
        int timeToLive = 5000;
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int scrW = metrics.widthPixels;
        for (final int drawableId : mParticleIds) {
            for (int x = 0; x <= scrW; x += scrW / 5) {
                new ParticleSystem(getActivity(), maxParts, drawableId, timeToLive)
                        .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                        .setRotationSpeed(144)
                        .setAcceleration(0.000785f, 90)
                        .setScaleRange(0.3f, 0.5f)
                        .emit(x, -10, partsPerSec, emitTime);
            }
        }
    }

    @OnClick(R.id.rfgf_shades_button)
    void toggleShades() {
        if (mIsResultSheetVisible) {
            rollUpShades();
        }
        else {
            rollDownShades();
        }
    }

    @OnClick(R.id.rfgf_submit_button)
    void submitRewardClaim() {
        ((VegasActivity) getActivity()).continueFlow();
    }

    private void rollDownShades() {
        expandView(mResultSheet);
        mIsResultSheetVisible = true;
    }

    private void rollUpShades() {
        collapseView(mResultSheet);
        mIsResultSheetVisible = false;
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

    public void expandView(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, -view.getHeight(), 0.0f);
        anim.setDuration(500);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        view.startAnimation(anim);
    }

    public void collapseView(final View view) {
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -view.getHeight());
        anim.setDuration(500);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rfgf_button_close:
                getActivity().finish();
                break;
            case R.id.rfgf_submit_button:
                showUiBlockers();
                mVegasManager.claimReward(mVegasGame.id, new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        removeUiBlockers();
                        ((VegasActivity) getActivity()).continueFlow();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
                break;
        }
    }

}

