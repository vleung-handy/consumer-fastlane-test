package com.handybook.handybook.vegas.model;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class GameViewModel implements Serializable {

    private static final String VALUE_SCRATCH_WINDOW = "scratch_window";
    private static final String KEY_TYPE = "type";

    private final JsonElement mGameResponse;

    private GameViewModel(final JsonElement jsonGame) {
        mGameResponse = jsonGame;
    }

    @NonNull
    public static GameViewModel from(final JsonElement jsonGame) {
        return new GameViewModel(jsonGame);
    }

    public boolean isValid() {
        if (mGameResponse == null) { return false; }
        final JsonElement jsonType = mGameResponse.getAsJsonObject().get(KEY_TYPE);
        if (jsonType == null || !jsonType.isJsonPrimitive()) { return false; }
        String strType = null;
        try {
            strType = jsonType.getAsString();
        }
        catch (Exception e) {
            Crashlytics.log("Invalid game");
            Crashlytics.log(mGameResponse.toString());
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        //this version can only handle 'scratch_window' type of a game
        return strType != null && strType.equals(VALUE_SCRATCH_WINDOW);
    }

    /**
     * Sugar method for nicer if statements
     * @return ! isValid()
     */
    public boolean isInvalid() {
        return !isValid();
    }

    @NonNull
    public static GameViewModel demo() {
        JsonObject json = new JsonObject();
        json.addProperty(KEY_TYPE, VALUE_SCRATCH_WINDOW);
        GameViewModel gameViewModel = new GameViewModel(json);
        return gameViewModel;
    }
}

