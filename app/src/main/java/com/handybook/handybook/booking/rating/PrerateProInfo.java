package com.handybook.handybook.booking.rating;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.referral.model.ProReferral;

import java.io.Serializable;
import java.util.Map;

/**
 * Sample JSON:
 * <p>
 * <p>
 * "reasons": {
 * "title": "What could your professional do to improve?",
 * "professionalism": {},
 * "left_early": {},
 * "quality_of_service": {
 * "kitchen": {},
 * "bathroom": {},
 * "bedroom": {},
 * "living_room": {},
 * "floors": {},
 * "extras": {},
 * "title": "Which areas were not cleaned properly?"
 * },
 * "supplies": {},
 * "arrived_late": {
 * "less_than_30_minutes_late": {},
 * "more_than_30_minutes_late": {},
 * "title": "How late was your professional?",
 * "type": "single"
 * }
 * },
 * <p>
 * <p>
 * Beware as there can be many levels of nested objects
 * <p>
 * <p>
 * <p>
 * Created by jtse on 3/17/16.
 */
public class PrerateProInfo implements Serializable {

    @SerializedName("placeholder_text")
    private String mPlaceHolderText;

    @SerializedName("home_cleaning")
    private boolean mIsCleaning;

    @SerializedName("match_preference")
    private ProviderMatchPreference mProviderMatchPreference;

    /**
     * The presence of this info suggests that we can share this specific pro
     */
    @SerializedName("pro_referral_info")
    private ProReferral mProReferralInfo;

    /**
     * This is tricky as hell. Do a request on a browser to prerate_pro_info to see how the response looks like.
     */
    @SerializedName("reasons")
    private Map<String, Object> mRawReasons;

    private Reasons mReasons;

    public static PrerateProInfo fromJson(final String json) {
        return new GsonBuilder().setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT).create()
                                .fromJson(json, PrerateProInfo.class);
    }

    @NonNull
    public Reasons getReasons() {
        if (mReasons == null) {
            //build it.
            mReasons = new Reasons(mRawReasons, mIsCleaning, null);
        }

        return mReasons;
    }

    public boolean isCleaning() {
        return mIsCleaning;
    }

    @Nullable
    public ProReferral getProReferralInfo() {
        return mProReferralInfo;
    }

    @Nullable
    public ProviderMatchPreference getProviderMatchPreference() {
        return mProviderMatchPreference;
    }
}
