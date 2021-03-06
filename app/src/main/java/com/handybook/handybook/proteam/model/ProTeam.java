package com.handybook.handybook.proteam.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.INDIFFERENT;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class ProTeam implements Parcelable {

    @SerializedName(ProTeamCategoryType.Constants.CLEANING)
    private ProTeamCategory mCleaning;
    @SerializedName(ProTeamCategoryType.Constants.HANDYMEN)
    private ProTeamCategory mHandymen;
    private ProTeamCategory mAllCategories;

    protected ProTeam(Parcel in) {
        mCleaning = in.readParcelable(ProTeamCategory.class.getClassLoader());
        mHandymen = in.readParcelable(ProTeamCategory.class.getClassLoader());
    }

    public static final Creator<ProTeam> CREATOR = new Creator<ProTeam>() {
        @Override
        public ProTeam createFromParcel(Parcel in) {
            return new ProTeam(in);
        }

        @Override
        public ProTeam[] newArray(int size) {
            return new ProTeam[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(mCleaning, flags);
        dest.writeParcelable(mHandymen, flags);
    }

    @Nullable
    public static String toJson(@Nullable final ProTeam proTeam) {
        return new GsonBuilder()
                .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                .create()
                .toJson(proTeam, ProTeam.class);
    }

    @Nullable
    public static ProTeam fromJson(@Nullable final String json) {
        if (json == null) {
            return null;
        }
        return new GsonBuilder()
                .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                .create()
                .fromJson(json, ProTeam.class);
    }

    public int getCount(
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            ProviderMatchPreference preference
    ) {
        final ProTeamCategory category = getCategory(proTeamCategoryType);
        if (category != null) {
            List<Provider> proTeamPros = category.get(preference);
            if (proTeamPros != null) {
                return proTeamPros.size();
            }
        }
        return 0;
    }

    @Nullable
    public ProTeamCategory getCategory(@NonNull final ProTeamCategoryType proTeamCategoryType) {
        switch (proTeamCategoryType) {
            case CLEANING:
                return mCleaning;
            case HANDYMEN:
                return mHandymen;
        }
        return null;
    }

    public ProTeamCategory getAllCategories() {
        if (mAllCategories != null) {
            return mAllCategories;
        }
        final List<Provider> preferredPros = new ArrayList<>();
        final List<Provider> indifferentPros = new ArrayList<>();
        if (mCleaning != null) {
            mergeList(preferredPros, mCleaning.getPreferred());
            mergeList(indifferentPros, mCleaning.getIndifferent());
        }
        if (mHandymen != null) {
            mergeList(preferredPros, mHandymen.getPreferred());
            mergeList(indifferentPros, mHandymen.getIndifferent());
        }
        Collections.sort(preferredPros);
        Collections.sort(indifferentPros);
        mAllCategories = new ProTeamCategory.Builder()
                .withPreference(PREFERRED, preferredPros)
                .withPreference(INDIFFERENT, indifferentPros)
                .build();
        return mAllCategories;
    }

    private void mergeList(
            @NonNull final List<Provider> to,
            @Nullable final List<Provider> from
    ) {
        if (from != null) {
            to.addAll(from);
        }
    }

    public boolean isEmpty() {
        return mCleaning.isEmpty() && mHandymen.isEmpty();
    }

    public static class ProTeamCategory implements Parcelable {

        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_PREFERRED)
        private List<Provider> mPreferred;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_INDIFFERENT)
        private List<Provider> mIndifferent;
        @SerializedName(ProviderMatchPreference.Constants.STRING_VALUE_NEVER)
        private List<Provider> mNever;

        ProTeamCategory() { }

        ProTeamCategory(Parcel in) {
            mPreferred = new ArrayList<>();
            in.readList(mPreferred, Provider.class.getClassLoader());
            mIndifferent = new ArrayList<>();
            in.readList(mIndifferent, Provider.class.getClassLoader());
            mNever = new ArrayList<>();
            in.readList(mNever, Provider.class.getClassLoader());
        }

        public void filterFavorPros(@NonNull final List<Provider> pros) {
            Set<String> ids = new HashSet<>();
            for (Provider pro : pros) {
                ids.add(pro.getId());
            }
            filterFavorPros(ids);
        }

        public void filterFavorPros(@NonNull final Set<String> ids) {
            // Filtering mPreferred
            if (mPreferred != null) {
                List<Provider> preferred = new ArrayList<>();
                for (Provider pro : mPreferred) {
                    if (ids.contains(pro.getId())) {
                        preferred.add(pro);
                    }
                }
                mPreferred = preferred;
            }

            // Filtering mIndifferent
            if (mIndifferent != null) {
                List<Provider> indifferent = new ArrayList<>();
                for (Provider pro : mIndifferent) {
                    if (ids.contains(pro.getId())) {
                        indifferent.add(pro);
                    }
                }
                mIndifferent = indifferent;
            }

            // Filtering mNever
            if (mNever != null) {
                List<Provider> never = new ArrayList<>();
                for (Provider pro : mNever) {
                    if (ids.contains(pro.getId())) {
                        never.add(pro);
                    }
                }
                mNever = never;
            }
        }

        public static final Creator<ProTeamCategory> CREATOR = new Creator<ProTeamCategory>() {
            @Override
            public ProTeamCategory createFromParcel(Parcel in) {
                return new ProTeamCategory(in);
            }

            @Override
            public ProTeamCategory[] newArray(int size) {
                return new ProTeamCategory[size];
            }
        };

        @Nullable
        public List<Provider> getPreferred() {
            return mPreferred;
        }

        @Nullable
        public List<Provider> getIndifferent() {
            return mIndifferent;
        }

        @Nullable
        public List<Provider> getNever() {
            return mNever;
        }

        @Nullable
        public List<Provider> get(@NonNull ProviderMatchPreference providerMatchPreference) {
            switch (providerMatchPreference) {
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
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeList(mPreferred);
            dest.writeList(mIndifferent);
            dest.writeList(mNever);
        }

        public boolean isEmpty() {
            if (mIndifferent != null && !mIndifferent.isEmpty()) {
                return false;
            }
            return !(mPreferred != null && !mPreferred.isEmpty());
        }

        @Nullable
        public Provider getFavoritePro() {
            for (final Provider proTeamPro : mPreferred) {
                if (proTeamPro.isFavorite()) {
                    return proTeamPro;
                }
            }
            return null;
        }

        public static class Builder {

            private final ProTeamCategory mProTeamCategory;

            public Builder() {
                mProTeamCategory = new ProTeamCategory();
            }

            public Builder withPreference(
                    @NonNull final ProviderMatchPreference preference,
                    @Nullable List<Provider> pros
            ) {
                switch (preference) {
                    case PREFERRED:
                        mProTeamCategory.mPreferred = pros;
                        break;
                    case INDIFFERENT:
                        mProTeamCategory.mIndifferent = pros;
                        break;
                    case NEVER:
                        mProTeamCategory.mNever = pros;
                        break;
                }
                return this;
            }

            public ProTeamCategory build() {
                return mProTeamCategory;
            }
        }
    }
}
