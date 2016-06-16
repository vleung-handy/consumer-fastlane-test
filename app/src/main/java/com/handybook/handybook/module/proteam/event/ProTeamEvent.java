package com.handybook.handybook.module.proteam.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamEdit;
import com.handybook.handybook.module.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

public abstract class ProTeamEvent
{
    public static class RequestProTeam extends HandyEvent.RequestEvent
    {
    }


    public static class ReceiveProTeamSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final ProTeam mProTeam;

        public ReceiveProTeamSuccess(final ProTeam proTeam)
        {
            mProTeam = proTeam;
        }

        @Nullable
        public ProTeam getProTeam()
        {
            return mProTeam;
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
        private ProTeamEditWrapper mProTeamEditWrapper;

        private RequestProTeamEdit()
        {
        }

        public RequestProTeamEdit(
                @NonNull final ProviderMatchPreference providerMatchPreference,
                @NonNull final ProTeamPro proTeamPro,
                @NonNull final ProTeamCategoryType proTeamCategoryType
        )
        {
            final ProTeamEdit proTeamEdit = new ProTeamEdit(providerMatchPreference);
            switch (proTeamCategoryType)
            {
                case CLEANING:
                    proTeamEdit.addCleaningId(proTeamPro.getId());
                    break;
                case HANDYMEN:
                    proTeamEdit.addHandymenId(proTeamPro.getId());
                    break;
            }
            mProTeamEditWrapper = new ProTeamEditWrapper(proTeamEdit);
        }

        public RequestProTeamEdit(
                @NonNull final ProviderMatchPreference providerMatchPreference,
                @Nullable final Iterable<ProTeamPro> cleaningPros,
                @Nullable final Iterable<ProTeamPro> handymenPros
        )
        {
            final ProTeamEdit proTeamEdit = new ProTeamEdit(providerMatchPreference);
            if (cleaningPros != null)
            {
                for (ProTeamPro ePro : cleaningPros)
                {
                    proTeamEdit.addCleaningId(ePro.getId());
                }
            }
            if (handymenPros != null)
            {
                for (ProTeamPro ePro : handymenPros)
                {
                    proTeamEdit.addHandymenId(ePro.getId());
                }
            }
            mProTeamEditWrapper = new ProTeamEditWrapper(proTeamEdit);
        }

        @Nullable
        public ProTeamEditWrapper getProTeamEditWrapper()
        {
            return mProTeamEditWrapper;
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


    @Track("proteam screen shown")
    public static class ProTeamScreenShown {}


    @Track("proteam add_pro clicked")
    public static class ProTeamAddProClicked {}


    @Track("proteam remove_pro clicked")
    public static class ProTeamRemoveProClicked
    {
        @TrackField("pro")
        private int mProId;

        public ProTeamRemoveProClicked(final int proId)
        {
            mProId = proId;
        }
    }


    @Track("proteam_add screen shown")
    public static class ProTeamAddScreenShown
    {
    }

}
