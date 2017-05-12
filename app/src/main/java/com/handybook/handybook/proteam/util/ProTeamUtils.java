package com.handybook.handybook.proteam.util;

import com.handybook.handybook.proteam.model.ProviderMatchPreference;

/**
 * utils for common pro team business logic that we don't want to put in models
 */
public class ProTeamUtils {

    /**
     * we need this because the server does not tell us whether the pro is on the customer's pro team
     * (ex. {@link com.handybook.handybook.booking.model.Provider}, {@link com.handybook.handybook.proprofiles.model.ProProfile}
     * @param proMatchPreference
     * @return true if the given match preference indicates that the pro is on the pro team
     */
    public static boolean isProOnProTeam(ProviderMatchPreference proMatchPreference) {
        return ProviderMatchPreference.FAVORITE == proMatchPreference
               || ProviderMatchPreference.PREFERRED == proMatchPreference;
    }
}
