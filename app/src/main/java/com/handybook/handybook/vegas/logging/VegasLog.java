package com.handybook.handybook.vegas.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.vegas.model.VegasGame;

public abstract class VegasLog extends EventLog {

    private static final String EVENT_CONTEXT = "game_flow";

    @SerializedName("game_type")
    private String mGameType;
    @SerializedName("is_winner")
    private Boolean mIsWinner;
    @SerializedName("reward_id")
    private Long mRewardId;

    public VegasLog(@NonNull final String eventType, @Nullable VegasGame game) {
        this(eventType);
        try {
            mGameType = game.type.toString();
            mRewardId = game.id;
            mIsWinner = game.gameInfo.isWinner;
        }
        catch (Exception e) {
            //Do nothing - Info noise bad
        }
    }

    public VegasLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class PromptScreenShown extends VegasLog {

        private static final String EVENT_TYPE = "prompt_screen_shown";

        public PromptScreenShown(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class PromptScreenDismissed extends VegasLog {

        private static final String EVENT_TYPE = "prompt_screen_dismissed";

        public PromptScreenDismissed(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class PlayNowSelected extends VegasLog {

        private static final String EVENT_TYPE = "play_now_selected";

        public PlayNowSelected(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class GameScreenShown extends VegasLog {

        private static final String EVENT_TYPE = "game_screen_shown";

        public GameScreenShown(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class GameScreenDismissed extends VegasLog {

        private static final String EVENT_TYPE = "game_screen_dismissed";

        public GameScreenDismissed(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class GamePlayStarted extends VegasLog {

        private static final String EVENT_TYPE = "game_play_started";

        public GamePlayStarted(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class RewardClaimShown extends VegasLog {

        private static final String EVENT_TYPE = "reward_claim_shown";

        public RewardClaimShown(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class RewardClaimSelected extends VegasLog {

        private static final String EVENT_TYPE = "reward_claim_selected";

        public RewardClaimSelected(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }

    // Network =====================================================================================


    public static class GameRequestSubmitted extends VegasLog {

        private static final String EVENT_TYPE = "game_request_submitted";

        public GameRequestSubmitted() {
            super(EVENT_TYPE);
        }
    }


    public static class GameRequestSuccess extends VegasLog {

        public static final String EVENT_TYPE = "game_request_success";

        public GameRequestSuccess() {
            super(EVENT_TYPE);
        }
    }


    public static class GameRequestError extends VegasLog {

        private static final String EVENT_TYPE = "game_request_error";
        @SerializedName("error_info")
        private String mErrorInfo;

        public GameRequestError(final String errorInfo) {
            super(EVENT_TYPE);
            mErrorInfo = errorInfo;
        }
    }


    public static class ClaimRequestSubmitted extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_submitted";

        public ClaimRequestSubmitted(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class ClaimRequestSuccess extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_success";

        public ClaimRequestSuccess(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class ClaimRequestError extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_error";

        @SerializedName("error_info")
        private String mErrorInfo;

        public ClaimRequestError(final VegasGame game, final String errorInfo) {
            super(EVENT_TYPE, game);
            mErrorInfo = errorInfo;
        }
    }


}
