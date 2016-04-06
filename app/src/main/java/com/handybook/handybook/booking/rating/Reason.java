package com.handybook.handybook.booking.rating;

/**
 * Created by jtse on 4/1/16.
 */

import android.support.annotation.DrawableRes;

import com.handybook.handybook.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Please forgive me for doing this. The reasons come back from the server, but it has to be
 * manually mapped to icons that must already exist in the system.
 * <p>
 * For certain types (i.e. quality_of_service), there is a distinction between handyman and cleaning
 * services. Those keys needs to use the prefix to access the correct image.
 */
public class Reason implements Serializable
{

    private static final Map<String, Integer> ICONS;
    public static final String CLEAN_PREFIX = "clean_";
    public static final String HANDYMAN_PREFIX = "handyman_";
    public static final String QUALITY_OF_SERVICE = "quality_of_service";
    public static final String ARRIVED_LATE = "arrived_late";
    public static final String DEFAULT = "default";

    static
    {
        ICONS = new HashMap<>();
        ICONS.put("professionalism", R.drawable.ic_rating_professionalism);
        ICONS.put("left_early", R.drawable.ic_rating_left_early);
        ICONS.put("arrived_late", R.drawable.ic_rating_arrived_late);

        ICONS.put(CLEAN_PREFIX + QUALITY_OF_SERVICE, R.drawable.ic_rating_clean_quality);
        ICONS.put(HANDYMAN_PREFIX + QUALITY_OF_SERVICE, R.drawable.ic_rating_handyman_quality);
        ICONS.put(CLEAN_PREFIX + "supplies", R.drawable.ic_rating_clean_supplies);
        ICONS.put(HANDYMAN_PREFIX + "supplies", R.drawable.ic_rating_handyman_supplies);

        ICONS.put("kitchen", R.drawable.ic_rating_kitchen);
        ICONS.put("bathroom", R.drawable.ic_rating_bathroom);
        ICONS.put("bedroom", R.drawable.ic_rating_bedroom);
        ICONS.put("living_room", R.drawable.ic_rating_living_room);
        ICONS.put("floors", R.drawable.ic_rating_floors);
        ICONS.put("extras", R.drawable.ic_rating_extras);

        ICONS.put("default", R.drawable.ic_rating_no_complaints);
    }

    /**
     * The value as represented by the server
     */
    public String key;

    /**
     * The value as represented to the user
     */
    public String value;

    public boolean isCleaning;

    public Reasons subReasons;

    public Reason(final String key, final String value, Reasons subReasons, boolean isCleaning)
    {
        this.key = key;
        this.value = value;
        this.isCleaning = isCleaning;
        this.subReasons = subReasons;
    }

    @DrawableRes
    public int getDrawableRes()
    {
        String prefix;
        if (isCleaning)
        {
            prefix = CLEAN_PREFIX;
        }
        else
        {
            prefix = HANDYMAN_PREFIX;
        }

        Integer temp = ICONS.get(key);
        if (temp != null)
        {
            return temp;
        }
        else
        {
            temp = ICONS.get(prefix + key);
            if (temp == null)
            {
                return ICONS.get(DEFAULT);
            }
            else
            {
                return temp;
            }
        }
    }
}
