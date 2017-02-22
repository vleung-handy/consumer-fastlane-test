package com.handybook.handybook.proteam.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import java.util.ArrayList;
import java.util.Set;

public abstract class ProTeamEvent
{
    public static class RequestProTeam extends HandyEvent.RequestEvent
    {
    }


    public static class ReceiveProTeamSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final ProTeam mProTeam;
        private final String mProTeamHelpCenterUrl;

        public ReceiveProTeamSuccess(final ProTeam proTeam, final String proTeamHelpCenterUrl)
        {
            mProTeam = proTeam;
            mProTeamHelpCenterUrl = proTeamHelpCenterUrl;
        }

        @Nullable
        public ProTeam getProTeam()
        {
            return mProTeam;
        }

        public String getProTeamHelpCenterUrl()
        {
            return mProTeamHelpCenterUrl;
        }
    }


    public static class ReceiveProTeamError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveProTeamError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestProTeamEdit extends HandyEvent.RequestEvent
    {
        private ArrayList<ProTeamEdit> mProTeamEdits;
        private Source mSource;


        public RequestProTeamEdit(
                @NonNull final ProTeamPro proTeamPro,
                @NonNull final ProTeamCategoryType proTeamCategoryType,
                @NonNull final ProviderMatchPreference providerMatchPreference,
                @NonNull final Source source
        )
        {
            this(proTeamPro.getId(), proTeamCategoryType, providerMatchPreference, source);
        }

        public RequestProTeamEdit(
                final int proTeamProId,
                @NonNull final ProTeamCategoryType proTeamCategoryType,
                @NonNull final ProviderMatchPreference providerMatchPreference,
                @NonNull final Source source
        )
        {
            mProTeamEdits = new ArrayList<>();
            mSource = source;
            final ProTeamEdit proTeamEdit = new ProTeamEdit(providerMatchPreference);
            proTeamEdit.addId(proTeamProId, proTeamCategoryType);
            mProTeamEdits.add(proTeamEdit);
        }

        public RequestProTeamEdit(
                @Nullable final Set<ProTeamPro> cleaningProsToAdd,
                @Nullable final Set<ProTeamPro> handymenProsToAdd,
                @Nullable final Set<ProTeamPro> cleaningProsToRemove,
                @Nullable final Set<ProTeamPro> handymenProsToRemove,
                @NonNull final Source source
        )
        {
            mProTeamEdits = new ArrayList<>();
            mSource = source;
            addProTeamEdit(
                    cleaningProsToAdd,
                    ProTeamCategoryType.CLEANING,
                    ProviderMatchPreference.PREFERRED
            );
            addProTeamEdit(
                    cleaningProsToRemove,
                    ProTeamCategoryType.CLEANING,
                    ProviderMatchPreference.INDIFFERENT
            );
            addProTeamEdit(
                    handymenProsToAdd,
                    ProTeamCategoryType.HANDYMEN,
                    ProviderMatchPreference.PREFERRED
            );
            addProTeamEdit(
                    handymenProsToRemove,
                    ProTeamCategoryType.HANDYMEN,
                    ProviderMatchPreference.INDIFFERENT
            );
        }

        @Nullable
        public ProTeamEditWrapper getProTeamEditWrapper()
        {
            return new ProTeamEditWrapper(mProTeamEdits, mSource.toString());
        }

        private void addProTeamEdit(
                @Nullable final Set<ProTeamPro> proTeamPros,
                @NonNull final ProTeamCategoryType proTeamCategoryType,
                @NonNull final ProviderMatchPreference providerMatchPreference
        )
        {
            final ProTeamEdit proTeamEdit = new ProTeamEdit(providerMatchPreference);
            if (proTeamPros != null && !proTeamPros.isEmpty())
            {
                for (ProTeamPro ePro : proTeamPros)
                {
                    proTeamEdit.addId(ePro.getId(), proTeamCategoryType);
                }
                mProTeamEdits.add(proTeamEdit);
            }
        }
    }


    public static class ReceiveProTeamEditSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private ProTeam mProTeam;

        public ReceiveProTeamEditSuccess(ProTeam proTeam)
        {
            mProTeam = proTeam;
        }

        @Nullable
        public ProTeam getProTeam()
        {
            return mProTeam;
        }
    }


    public static class ReceiveProTeamEditError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveProTeamEditError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class ReceiveBookingProTeamSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final ProTeam.ProTeamCategory mProTeamCategory;

        public ReceiveBookingProTeamSuccess(final ProTeam.ProTeamCategory proTeamCategory)
        {
            mProTeamCategory = proTeamCategory;
        }

        @Nullable
        public ProTeam.ProTeamCategory getProTeamCategory()
        {
            return mProTeamCategory;
        }

    }


    public static class ReceiveBookingProTeamError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveBookingProTeamError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class ProTeamUpdated
    {
        private ProTeam mUpdatedProTeam;

        public ProTeamUpdated(final ProTeam updatedProTeam)
        {
            mUpdatedProTeam = updatedProTeam;
        }

        public ProTeam getUpdatedProTeam()
        {
            return mUpdatedProTeam;
        }
    }


    public enum Source
    {
        PRO_MANAGEMENT("pro_management"),
        BOOKING_FLOW("booking_flow");

        private final String mSource;

        Source(final String source)
        {
            mSource = source;
        }

        @Override
        public String toString()
        {
            return mSource;
        }
    }
}
