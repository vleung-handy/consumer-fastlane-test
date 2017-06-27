package com.handybook.handybook.vegas.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VegasGame implements Serializable {

    @SerializedName("type")
    public Type type;
    @SerializedName("id")
    public long id;
    @SerializedName("pre_game_info")
    public PreGameInfo preGameInfo;
    @SerializedName("game_info")
    public GameInfo gameInfo;
    @SerializedName("result")
    public Result result;
    @SerializedName("claim_info")
    public ClaimInfo claimInfo;

    @NonNull
    public static VegasGame from(final JsonObject jsonGame) {
        return new Gson().fromJson(jsonGame, VegasGame.class);
    }

    public boolean isValid() {
        return type == Type.SCRATCH_WINDOW;
    }

    /**
     * Sugar method for nicer if statements
     * @return ! isValid()
     */
    public boolean isInvalid() {
        return !isValid();
    }

    public enum Type implements Serializable {
        @SerializedName("scratch_window")SCRATCH_WINDOW
    }


    public static class PreGameInfo {

        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("reward_title")
        public String rewardTitle;
        @SerializedName("reward_description")
        public String rewardDescription;
        @SerializedName("button_title")
        public String buttonTitle;

    }


    public static class GameInfo implements Serializable {

        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("footer_text")
        public String footerText;
    }


    public static class Result implements Serializable {

        @SerializedName("symbols")
        public GameSymbol[] symbols;
        @SerializedName("is_winner")
        public boolean isWinner;
    }


    public static class ClaimInfo implements Serializable {

        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("reward")
        public Reward reward;
        @SerializedName("button_title")
        public String buttonTitle;


        public static class Reward implements Serializable {

            @SerializedName("title")
            public String title;
            @SerializedName("symbol")
            public GameSymbol symbol;
            @SerializedName("symbol_chip")
            public String symbolChip;
            @SerializedName("summary")
            public String summary;
            @SerializedName("description")
            public String description;

        }
    }

    @NonNull
    public static VegasGame demo() {
        final VegasGame game = new VegasGame();
        game.type = Type.SCRATCH_WINDOW;
        game.id = 123456;

        game.preGameInfo = new PreGameInfo();
        game.preGameInfo.title = "Wipe out!";
        game.preGameInfo.description
                = "Now every time you complete a\nbooking you'll have the chance to\nclaim great rewards.";
        game.preGameInfo.rewardTitle = "Possible rewards include...";
        game.preGameInfo.rewardDescription = "Free Cleanings\nfor 1 year!";
        game.preGameInfo.buttonTitle = "Play Now!";

        game.gameInfo = new GameInfo();
        game.gameInfo.title = "Wipe out!";
        game.gameInfo.description = "Use the sponge to clean the window and reveal rewards.";
        game.gameInfo.footerText = "The more icons you match, the better the reward!";

        game.result = new Result();
        game.result.symbols = new GameSymbol[]{
                GameSymbol.BUCKET_GREEN,
                GameSymbol.SOAP_BOTTLE,
                GameSymbol.HEART,
                GameSymbol.BUCKET_GREEN
        };
        game.result.isWinner = true;

        game.claimInfo = new ClaimInfo();
        game.claimInfo.title = "CONGRATS!";
        game.claimInfo.description = "Thanks for cleaning! We'll see\nyou after your next booking.";
        game.claimInfo.buttonTitle = "Done";

        game.claimInfo.reward = new ClaimInfo.Reward();
        game.claimInfo.reward.symbol = GameSymbol.BUCKET_GREEN;
        game.claimInfo.reward.symbolChip = "x2";
        game.claimInfo.reward.summary = "$5 in Handy credit";
        game.claimInfo.reward.description = "Check your email to redeem your reward.";
        return game;
    }
}

