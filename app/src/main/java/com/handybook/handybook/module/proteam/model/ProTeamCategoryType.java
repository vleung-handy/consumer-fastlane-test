package com.handybook.handybook.module.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Holds all the available ProTeam types, currently CLEANING or HANDYMEN
 */
public enum ProTeamCategoryType implements Parcelable
{
    @SerializedName(Constants.CLEANING)
    CLEANING(Constants.CLEANING),
    @SerializedName(Constants.HANDYMEN)
    HANDYMEN(Constants.HANDYMEN);

    private final String mValue;

    ProTeamCategoryType(String value)
    {
        mValue = value;
    }

    ProTeamCategoryType(Parcel in)
    {
        mValue = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mValue);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<ProTeamCategoryType> CREATOR = new Creator<ProTeamCategoryType>()
    {
        @Nullable
        @Override
        public ProTeamCategoryType createFromParcel(Parcel in)
        {
            final String value = in.readString();
            switch (value)
            {
                case Constants.HANDYMEN:
                    return HANDYMEN;
                case Constants.CLEANING:
                    return CLEANING;
            }
            return null;
        }

        @Override
        public ProTeamCategoryType[] newArray(int size)
        {
            return new ProTeamCategoryType[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return mValue;
    }

    /**
     * @param categoryType ProTeamCategoryType to be converted to it's String representation
     * @return String representation of provided ProTeamCategoryType or null
     */
    @Nullable
    public static String asString(@Nullable ProTeamCategoryType categoryType)
    {
        return categoryType == null ? null : categoryType.toString();
    }


    /**
     * Holds the String constants that represent/can be converted to/from the ProTeamCategory enum
     */
    public static class Constants
    {
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
