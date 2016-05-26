package com.handybook.handybook.module.proteam.event;

import android.support.annotation.Nullable;

import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.analytics.annotation.TrackField;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamEditWrapper;

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


    public static class RequestProTeamEdit
    {
        private ProTeamEditWrapper mProTeamEditWrapper;

        public RequestProTeamEdit(ProTeamEditWrapper proTeamEditWrapper)
        {
            mProTeamEditWrapper = proTeamEditWrapper;
        }

        @Nullable
        public ProTeamEditWrapper getProTeamEditWrapper()
        {
            return mProTeamEditWrapper;
        }
    }


    public static class ReceiveProTeamEditSuccess
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
