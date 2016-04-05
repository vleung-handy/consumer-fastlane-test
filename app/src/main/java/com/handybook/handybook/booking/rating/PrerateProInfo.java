package com.handybook.handybook.booking.rating;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

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
public class PrerateProInfo implements Serializable
{

    @SerializedName("placeholder_text")
    private String placeHolderText;

    @SerializedName("home_cleaning")
    private boolean isCleaning;

    /**
     * This is tricky as hell. Do a request on a browser to prerate_pro_info to see how the response looks like.
     */
    @SerializedName("reasons")
    private Map<String, Object> mRawReasons;

    private Reasons mReasons;

    public static PrerateProInfo fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, PrerateProInfo.class);
    }

    /**
     * Beware, this complicated. Reasons is a hash map. Each key is a "title" that will be displayed to the
     * user. For example, The map can look like this:
     * title: "What could your professional do to improve?"
     * left_early: {}
     * quality_of_service: {}
     * <p>
     * We have to ignore the "title" key. For the rest of the keys, we have to remove the underscore
     * and capitalize the first word. For example:
     * <p>
     * quality_of_service will be Quality of service
     *
     * @return
     */
    @NonNull
    public Reasons getReasons()
    {
        if (mReasons == null)
        {
            //build it.
            mReasons = new Reasons(mRawReasons, isCleaning, null);
        }

        return mReasons;
    }

}
