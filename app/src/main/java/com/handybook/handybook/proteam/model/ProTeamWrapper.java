package com.handybook.handybook.proteam.model;

import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.referral.model.ProReferral;

import java.util.Map;

public class ProTeamWrapper {

    @SerializedName("pro_team")
    private ProTeam mProTeam;
    @SerializedName("pro_team_help_center_url")
    private String mProTeamHelpCenterUrl;

    /**
     * The presence of this info suggests that we can share this specific pro
     */
    @SerializedName("pro_referral_info")
    private Map<String, ProReferral> mProReferral;

    @Nullable
    public ProTeam getProTeam() {
        return mProTeam;
    }

    public String getProTeamHelpCenterUrl() {
        return mProTeamHelpCenterUrl;
    }

    /**
     * String is the provider_id
     * @return
     */
    public Map<String, ProReferral> getProReferral() {
        return mProReferral;
    }

    @Nullable
    public static ProTeamWrapper fromJson(@Nullable final String json) {
        if (json == null) { return null; }

        ProTeamWrapper wrapper
                = new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                                   .fromJson(json, ProTeamWrapper.class);

        //there are times when we get back a Map of mProReferral, but there isn't a Provider within
        //them. If it's not available, then populate it there.
        if (wrapper.getProReferral() != null && !wrapper.getProReferral().isEmpty()) {
            for (String providerId : wrapper.getProReferral().keySet()) {
                ProReferral referral = wrapper.getProReferral().get(providerId);
                if (referral.getProvider() == null) {
                    referral.setProvider(wrapper.getProvider(providerId));
                }
            }
        }

        return wrapper;
    }

    public Provider getProvider(String providerId) {
        if (mProTeam == null) {
            return null;
        }

        for (Provider p : mProTeam.getAllCategories().getPreferred()) {
            if (p.getId().equals(providerId)) {
                return p;
            }
        }
        for (Provider p : mProTeam.getAllCategories().getIndifferent()) {
            if (p.getId().equals(providerId)) {
                return p;
            }
        }
        for (Provider p : mProTeam.getAllCategories().getNever()) {
            if (p.getId().equals(providerId)) {
                return p;
            }
        }

        return null;
    }
}
