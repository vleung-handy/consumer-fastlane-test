package com.handybook.handybook.module.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProTeam implements Parcelable
{

    @SerializedName(ProTeamCategoryType.Constants.CLEANING)
    private ProTeamCategory mCleaning;
    @SerializedName(ProTeamCategoryType.Constants.HANDYMEN)
    private ProTeamCategory mHandymen;

    protected ProTeam(Parcel in)
    {
        mCleaning = in.readParcelable(ProTeamCategory.class.getClassLoader());
        mHandymen = in.readParcelable(ProTeamCategory.class.getClassLoader());
    }

    public static final Creator<ProTeam> CREATOR = new Creator<ProTeam>()
    {
        @Override
        public ProTeam createFromParcel(Parcel in)
        {
            return new ProTeam(in);
        }

        @Override
        public ProTeam[] newArray(int size)
        {
            return new ProTeam[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeParcelable(mCleaning, flags);
        dest.writeParcelable(mHandymen, flags);
    }

    public boolean hasAvailableProsInCategory(@Nullable final ProTeamCategoryType proTeamCategoryType)
    {
        if (proTeamCategoryType == null)
        {
            return false;
        }
        final ProTeamCategory category;
        switch (proTeamCategoryType) // Which category are we dealing with?
        {
            case CLEANING:
                category = mCleaning;
                break;
            case HANDYMEN:
                category = mHandymen;
                break;
            default:
                category = null;
        }
        if (category == null)
        {
            return false;
        }
        if (category.getIndifferent() == null)
        {
            return false;
        }
        return !category.getIndifferent().isEmpty();
    }

    @Nullable
    public ProTeamCategory getCategory(@NonNull final ProTeamCategoryType proTeamCategoryType)
    {
        switch (proTeamCategoryType)
        {
            case CLEANING:
                return mCleaning;
            case HANDYMEN:
                return mHandymen;
        }
        return null;
    }


    public static class ProTeamCategory implements Parcelable
    {

        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_PREFERRED)
        private List<ProTeamPro> mPreferred;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_INDIFFERENT)
        private List<ProTeamPro> mIndifferent;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_NEVER)
        private List<ProTeamPro> mNever;

        ProTeamCategory(Parcel in)
        {
            mPreferred = new ArrayList<>();
            in.readList(mPreferred, ProTeamPro.class.getClassLoader());
            mIndifferent = new ArrayList<>();
            in.readList(mIndifferent, ProTeamPro.class.getClassLoader());
            mNever = new ArrayList<>();
            in.readList(mNever, ProTeamPro.class.getClassLoader());
        }

        public static final Creator<ProTeamCategory> CREATOR = new Creator<ProTeamCategory>()
        {
            @Override
            public ProTeamCategory createFromParcel(Parcel in)
            {
                return new ProTeamCategory(in);
            }

            @Override
            public ProTeamCategory[] newArray(int size)
            {
                return new ProTeamCategory[size];
            }
        };

        @Nullable
        public List<ProTeamPro> getPreferred()
        {
            return mPreferred;
        }

        @Nullable
        public List<ProTeamPro> getIndifferent()
        {
            return mIndifferent;
        }

        @Nullable
        public List<ProTeamPro> getNever()
        {
            return mNever;
        }

        @Nullable
        public List<ProTeamPro> get(@NonNull ProviderMatchPreference providerMatchPreference)
        {
            switch (providerMatchPreference)
            {
                case INDIFFERENT:
                    return getIndifferent();
                case PREFERRED:
                    return getPreferred();
                case NEVER:
                    return getNever();
            }
            return null;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags)
        {
            dest.writeList(mPreferred);
            dest.writeList(mIndifferent);
            dest.writeList(mNever);
        }
    }


}
