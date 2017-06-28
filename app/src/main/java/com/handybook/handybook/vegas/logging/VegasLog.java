package com.handybook.handybook.vegas.logging;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.vegas.model.VegasGame;

public abstract class VegasLog extends EventLog {

    private static final String EVENT_CONTEXT = "game_flow";

    @SerializedName("game_type")
    private String mGameType;
    @SerializedName("is_winner")
    private boolean mIsWinner;

    private VegasLog(final String eventType, VegasGame game) {
        super(eventType, EVENT_CONTEXT);
        try {
            mGameType = game.type.toString();
            mIsWinner = game.result.isWinner;
        }
        catch (Exception e) {
            //Do nothing - Info noise bad
        }
    }

    public static class PromptScreenShown extends VegasLog {

        private static final String EVENT_TYPE = "prompt_screen_shown";

        private PromptScreenShown(final VegasGame game) {
            super(EVENT_TYPE, game);
        }
    }


    public static class PromptScreenDismissed extends VegasLog {

        private static final String EVENT_TYPE = "prompt_screen_dismissed";

        private PromptScreenDismissed(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class PlayNowSelected extends VegasLog {

        private static final String EVENT_TYPE = "play_now_selected";

        private PlayNowSelected(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class GameScreenShown extends VegasLog {

        private static final String EVENT_TYPE = "game_screen_shown";

        private GameScreenShown(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class GameScreenDismissed extends VegasLog {

        private static final String EVENT_TYPE = "game_screen_dismissed";

        private GameScreenDismissed(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class GamePlayStarted extends VegasLog {

        private static final String EVENT_TYPE = "game_play_started";

        private GamePlayStarted(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class RewardClaimShown extends VegasLog {

        private static final String EVENT_TYPE = "reward_claim_shown";

        private RewardClaimShown(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class RewardClaimSelected extends VegasLog {

        private static final String EVENT_TYPE = "reward_claim_selected";

        private RewardClaimSelected(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }

    // Network =====================================================================================


    public static class GameRequestSubmitted extends VegasLog {

        private static final String EVENT_TYPE = "game_request_submitted";

        private GameRequestSubmitted(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class GameRequestSuccess extends VegasLog {

        private static final String EVENT_TYPE = "game_request_success";

        private GameRequestSuccess(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class GameRequestError extends VegasLog {

        private static final String EVENT_TYPE = "game_request_error";
        @SerializedName("error_info")
        private String mErrorInfo;

        private GameRequestError(
                final String eventType,
                final VegasGame game,
                final String errorInfo
        ) {
            super(eventType, game);
            mErrorInfo = errorInfo;
        }
    }


    public static class ClaimRequestSubmitted extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_submitted";

        private ClaimRequestSubmitted(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class ClaimRequestSuccess extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_success";

        private ClaimRequestSuccess(final String eventType, final VegasGame game) {
            super(eventType, game);
        }
    }


    public static class ClaimRequestError extends VegasLog {

        private static final String EVENT_TYPE = "claim_request_error";

        @SerializedName("error_info")
        private String mErrorInfo;

        private ClaimRequestError(
                final String eventType,
                final VegasGame game,
                final String errorInfo
        ) {
            super(eventType, game);
            mErrorInfo = errorInfo;
        }
    }


}
