package com.handybook.handybook.vegas.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VegasGame implements Serializable {

    @SerializedName("type")
    public Type type;
    @SerializedName("reward_offer_id")
    public long rewardOfferId;
    @SerializedName("pre_game_info")
    public PreGameInfo preGameInfo;
    @SerializedName("game_info")
    public GameInfo gameInfo;
    @SerializedName("claim_info")
    public ClaimInfo claimInfo;

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


    public static class PreGameInfo implements Serializable {

        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("reward_title")
        public String rewardTitle;
        @SerializedName("reward_description")
        public String rewardDescription;
        @SerializedName("terms")
        public String terms;
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
        @SerializedName("button_title")
        public String buttonTitle;
        @SerializedName("reward_info")
        public RewardInfo rewardInfo;


        public static class RewardInfo implements Serializable {

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
        game.rewardOfferId = 123456;

        game.preGameInfo = new PreGameInfo();
        game.preGameInfo.title = "_Wipe out!";
        game.preGameInfo.description
                = "_Now every time you complete a\nbooking you'll have the chance to\nclaim great rewards.";
        game.preGameInfo.rewardTitle = "_Possible rewards include...";
        game.preGameInfo.rewardDescription = "_Free Cleanings\nfor 1 year!";
        game.preGameInfo.terms = "_By clicking the button below, I agree to<br>Handy's <a href=\"https://handy.com/terms?hide_header=1\">Terms &amp; Conditions";
        game.preGameInfo.buttonTitle = "_Play Now!";

        game.gameInfo = new GameInfo();
        game.gameInfo.title = "_Wipe out!";
        game.gameInfo.description = "_Use the sponge to clean the window and reveal rewards.";
        game.gameInfo.footerText = "_The more icons you match, the better the rewardInfo!";
        game.gameInfo.isWinner = true;
        game.gameInfo.symbols = new GameSymbol[]{
                GameSymbol.BUCKET_GREEN,
                GameSymbol.SOAP_BOTTLE,
                GameSymbol.HEART,
                GameSymbol.BUCKET_GREEN
        };

        game.claimInfo = new ClaimInfo();
        game.claimInfo.title = "_CONGRATS!";
        game.claimInfo.description = "_Thanks for cleaning! We'll see\nyou after your next booking.";
        game.claimInfo.buttonTitle = "_Done";

        game.claimInfo.rewardInfo = new ClaimInfo.RewardInfo();
        game.claimInfo.rewardInfo.symbol = GameSymbol.BUCKET_GREEN;
        game.claimInfo.rewardInfo.symbolChip = "x2";
        game.claimInfo.rewardInfo.summary = "$5 in Handy credit";
        game.claimInfo.rewardInfo.description = "Check your email to redeem your rewardInfo.";
        return game;
    }
}

