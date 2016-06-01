package com.handybook.handybook.module.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
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


    public static class ProTeamCategory implements Parcelable
    {

        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_PREFERRED)
        private List<ProTeamPro> mPreferred;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_INDIFFERENT)
        private List<ProTeamPro> mIndifferent;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_NEVER)
        private List<ProTeamPro> mNever;

        protected ProTeamCategory(Parcel in)
        {
            mPreferred = new ArrayList<ProTeamPro>();
            in.readList(mPreferred, ProTeamPro.class.getClassLoader());
            mIndifferent = new ArrayList<ProTeamPro>();
            in.readList(mIndifferent, ProTeamPro.class.getClassLoader());
            mNever = new ArrayList<ProTeamPro>();
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
        List<ProTeamPro> getIndifferent()
        {
            return mIndifferent;
        }

        @Nullable
        List<ProTeamPro> getNever()
        {
            return mNever;
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


    /**
     * Passed to the API to move a group of providers to preferred, indifferent or never buckets
     */
    static class ProTeamEdit
    {
        @SerializedName(ProTeamCategoryType.Constants.CLEANING)
        private List<Integer> mCleaningIds;
        @SerializedName(ProTeamCategoryType.Constants.HANDYMEN)
        private List<Integer> mHandymenIds;
        @SerializedName("match_preference")
        private ProviderMatchPreference mMatchPreference;

        public ProTeamEdit(final ProviderMatchPreference matchPreference)
        {
            mMatchPreference = matchPreference;
        }

        //FIXME: Add accessors memthods as needed while keeping this as closed as possible

    }
}
