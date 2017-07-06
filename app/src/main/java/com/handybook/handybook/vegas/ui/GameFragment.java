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
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.databinding.VegasGameFragmentBinding;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.vegas.logging.VegasLog;
import com.handybook.handybook.vegas.model.GameSymbol;
import com.handybook.handybook.vegas.model.VegasGame;
import com.handybook.handybook.vegas.ui.view.GameSymbolView;
import com.handybook.handybook.vegas.ui.view.LockableScrollView;
import com.handybook.handybook.vegas.ui.view.ScratchOffView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.HashSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameFragment extends InjectedFragment {

    public static final String TAG = GameFragment.class.getName();

    private static final String KEY_GAME_VM = "key::game_view_model";
    public static final double RATIO_TO_REVEAL = .65;
    public static final int DELAY_SHADE_DOWN_MS = 2200;

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

    private float mSpongeStartX;
    private float mSpongeStartY;
    private boolean mIsFirstInteraction = true;

    @BindView(R.id.rfgf_background_image) ImageView mBackground;
    @BindView(R.id.rfgf_scroll_container) LockableScrollView mScrollView;
    @BindView(R.id.rfgf_subtitle) TextView mDescription;
    @BindView(R.id.rfgf_button_dismiss) ImageButton mDismissButton;
    @BindView(R.id.rfgf_button_submit) Button mSubmitButton;
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
    @BindView(R.id.rfgf_result_symbol) GameSymbolView mSymbolClaim;
    @BindView(R.id.rfgf_banner_bottom_container) ViewGroup mBottomBannerContainer;
    @BindView(R.id.rfgf_banner_bottom_text) TextView mBottomBannerText;

    public GameFragment() {
    }

    @NonNull
    public static GameFragment newInstance(@NonNull VegasGame vegasGame) {
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
        final VegasGameFragmentBinding binding = VegasGameFragmentBinding
                .inflate(inflater, container, false);
        final View view = binding.getRoot();
        binding.setFragment(this);
        binding.setGame(mVegasGame);
        ButterKnife.bind(this, view);
        init();
        bus.post(new LogEvent.AddLogEvent(new VegasLog.GameScreenShown(mVegasGame)));
        return view;
    }

    private void init() {
        initScratchOffView();
        initSponge();
        initSymbols();
    }

    private void initSymbols() {
        mSymbolTL.setSymbol(mVegasGame.gameInfo.symbols[0]);
        mSymbolTR.setSymbol(mVegasGame.gameInfo.symbols[1]);
        mSymbolBL.setSymbol(mVegasGame.gameInfo.symbols[2]);
        mSymbolBR.setSymbol(mVegasGame.gameInfo.symbols[3]);
        mSymbolClaim.setSymbol(mVegasGame.claimInfo.rewardInfo.symbol);
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
            public void onScratchStart(final float rawX, final float rawY) {
                if (mIsFirstInteraction) {
                    bus.post(new LogEvent.AddLogEvent(new VegasLog.GamePlayStarted(mVegasGame)));
                    mIsFirstInteraction = false;
                }
                attachSponge(rawX, rawY);
            }

            @Override
            public void onScratchMove(final float rawX, final float rawY) {
                moveSponge(rawX, rawY);
            }

            @Override
            public void onScratchStop(final float rawX, final float rawY) {
                detachSponge(rawX, rawY);
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

    private void attachSponge(final float rawX, final float rawY) {
        mScrollView.setScrollingEnabled(false);
        mSpongeActor.animate()
                    .setDuration(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .x(getSpongeX(rawX))
                    .y(getSpongeY(rawY))
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

    private void moveSponge(final float rawX, final float rawY) {
        mSpongeActor.setX(getSpongeX(rawX));
        mSpongeActor.setY(getSpongeY(rawY));
    }

    private void detachSponge(final float rawX, final float rawY) {
        double ratio = mScratchOffView.getScratchedOffRatio(10);
        if (ratio > RATIO_TO_REVEAL) {
            revealClaim();
        }
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

    private void revealClaim() {
        bus.post(new LogEvent.AddLogEvent(new VegasLog.RewardClaimShown(mVegasGame)));
        mScratchOffView.scratchOffAll();
        mScratchOffView.setOnScratchListener(null);
        mDescription.setText(mVegasGame.claimInfo.description);
        mDismissButton.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.VISIBLE);
        animateWinningSymbols();
        if (mVegasGame.gameInfo.isWinner) {
            blastConfetti();
        }
        Runnable delayedTask = new Runnable() {
            @Override
            public void run() {
                expandView(mResultSheet);
                animateView(mBucket, R.animator.vegas_swipe_out_right);
                animateView(mSpongeActor, R.animator.vegas_swipe_out_right);
                animateView(mBottomBannerContainer, R.animator.vegas_swipe_out_down);
            }
        };
        mScratchOffView.postDelayed(delayedTask, DELAY_SHADE_DOWN_MS);
    }

    private void animateWinningSymbols() {
        final HashSet<GameSymbol> existingSymbols = new HashSet<>();
        GameSymbol winningSymbol = null;
        for (GameSymbol symbol : mVegasGame.gameInfo.symbols) {
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

    void blastConfetti() {
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int scrW = metrics.widthPixels;
        for (final int drawableId : mParticleIds) {
            for (int x = 0; x <= scrW; x += scrW / 5) {
                new ParticleSystem(getActivity(), 300, drawableId, 5000)
                        .setSpeedModuleAndAngleRange(0.1f, 0.3f, 225, 315)
                        .setRotationSpeed(144)
                        .setAcceleration(0.000785f, 90)
                        .setScaleRange(0.3f, 0.5f)
                        .emit(x, -10, 6, 1000);
            }
        }
    }

    @NonNull
    private AnimatorSet animateView(@NonNull final View view, final @AnimatorRes int resId) {
        final AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                resId
        );
        set.setTarget(view);
        set.start();
        view.setTag(set);
        return set;
    }

    public void expandView(@NonNull final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, -view.getHeight(), 0.0f);
        anim.setDuration(500);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        view.startAnimation(anim);
    }

    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.rfgf_button_dismiss:
                bus.post(new LogEvent.AddLogEvent(new VegasLog.GameScreenDismissed(mVegasGame)));
                getActivity().finish();
                break;
            case R.id.rfgf_button_submit:
                bus.post(new LogEvent.AddLogEvent(new VegasLog.RewardClaimSelected(mVegasGame)));
                bus.post(new LogEvent.AddLogEvent(new VegasLog.ClaimRequestSubmitted(mVegasGame)));
                showUiBlockers();
                mVegasManager.claimReward(mVegasGame.rewardOfferId, new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        bus.post(new LogEvent.AddLogEvent(new VegasLog.ClaimRequestSuccess(
                                mVegasGame)));
                        removeUiBlockers();
                        ((VegasActivity) getActivity()).continueFlow();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        bus.post(new LogEvent.AddLogEvent(new VegasLog.ClaimRequestError(
                                mVegasGame,
                                error.getMessage()
                        )));
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
                break;
        }
    }

}

