package com.handybook.handybook.proteam.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Holds all the available ProTeam types, currently CLEANING or HANDYMEN
 */
public enum ProTeamCategoryType {
    @SerializedName(Constants.CLEANING)
    CLEANING(Constants.CLEANING),
    @SerializedName(Constants.HANDYMEN)
    HANDYMEN(Constants.HANDYMEN);

    private final String mValue;

    ProTeamCategoryType(String value) {
        mValue = value;
    }

    @NonNull
    @Override
    public String toString() {
        return mValue;
    }

    /**
     * @param categoryType ProTeamCategoryType to be converted to it's String representation
     * @return String representation of provided ProTeamCategoryType or null
     */
    @Nullable
    public static String asString(@Nullable ProTeamCategoryType categoryType) {
        return categoryType == null ? null : categoryType.toString();
    }

    /**
     * Holds the String constants that represent/can be converted to/from the ProTeamCategory enum
     */
    public static class Constants {

        /**
         * "cleaning"
         */
        static final String CLEANING = "cleaning";
        /**
         * "handymen"
         */
        static final String HANDYMEN = "handymen";
    }
}
