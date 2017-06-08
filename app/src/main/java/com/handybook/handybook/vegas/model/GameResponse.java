package com.handybook.handybook.vegas.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class GameResponse implements Serializable {

    @SerializedName("type")
    @Type
    String mType;

    public static GameResponse demo() {
        return null;
    }

    @Retention(SOURCE)
    @StringDef({Type.SCRATCH_WINDOW})
    public @interface Type {

        String SCRATCH_WINDOW = "scratch_window";
    }


}
