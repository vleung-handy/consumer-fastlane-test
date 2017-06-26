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
    @SerializedName("pre_game")
    public PreGame preGame;
    @SerializedName("info")
    public Info info;
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


    public static class PreGame {

        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("prize_title")
        public String prizeTitle;
        @SerializedName("prize_description")
        public String prizeDescription;
        @SerializedName("button_title")
        public String buttonTitle;

    }


    public static class Info implements Serializable {

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
        @SerializedName("symbol")
        public GameSymbol symbol;
        @SerializedName("symbol_chip")
        public String symbolChip;
        @SerializedName("reward_description")
        public String rewardDescription;
        @SerializedName("claim_text")
        public String claimText;
        @SerializedName("button_title")
        public String buttonTitle;

    }

    @NonNull
    public static VegasGame demo() {
        final VegasGame game = new VegasGame();
        game.type = Type.SCRATCH_WINDOW;
        game.id = 123456;

        game.preGame = new PreGame();
        game.preGame.title = "You've unlocked a game!";
        game.preGame.description
                = "Who says cleaning isn't fun?\nWe've got lots of great rewards\nwaiting to be revealed.";
        game.preGame.prizeTitle = "YOU COULD WIN...";
        game.preGame.prizeDescription = "Free Cleanings for 1 year";
        game.preGame.buttonTitle = "Play Now!";

        game.info = new Info();
        game.info.title = "Clean to Win";
        game.info.description = "Use the sponge to clean the window and reveal rewards.";
        game.info.footerText = "The more icons you match, the better the reward!";

        game.result = new Result();
        game.result.symbols = new GameSymbol[]{
                GameSymbol.SOAP_BOTTLE,
                GameSymbol.SOAP_BOTTLE,
                GameSymbol.HEART,
                GameSymbol.PIGGY_BANK
        };
        game.result.isWinner = true;

        game.claimInfo = new ClaimInfo();
        game.claimInfo.title = "WINNER!";
        game.claimInfo.symbol = GameSymbol.SOAP_BOTTLE;
        game.claimInfo.symbolChip = "x2";
        game.claimInfo.rewardDescription = "$5 in Handy credit";
        game.claimInfo.claimText = "Tap claim to have this prize credited to your account.";
        game.claimInfo.buttonTitle = "Claim";

        return game;
    }
}

