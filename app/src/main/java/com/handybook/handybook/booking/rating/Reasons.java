package com.handybook.handybook.booking.rating;

import com.handybook.handybook.library.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sample RAW reasons:
 * <p/>
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
 * }
 * <p/>
 * Created by jtse on 4/1/16.
 */
public class Reasons implements Serializable
{
    private String mTitle;
    private List<Reason> mReasons;


    /**
     * key is used as the parent key of the list. For example, quality_of_service is the
     * key for the list [kitchen, bathroom, bedroom, etc.]
     */
    private String mKey;


    /**
     * Beware, this complicated. Reasons is a hash map. Each key is a "title" that will be displayed to the
     * user. For example, The map can look like this:
     * title: "What could your professional do to improve?"
     * left_early: {}
     * quality_of_service: {}
     * <p/>
     * We have to ignore the "title" key. For the rest of the keys, we have to remove the underscore
     * and capitalize the first word. For example:
     * <p/>
     * quality_of_service will be Quality of service
     */
    public Reasons(Map<String, Object> mRawReasons, boolean isCleaning, String key)
    {
        mKey = key;
        mReasons = new ArrayList<>();
        for (String s : mRawReasons.keySet())
        {
            if ("title".equalsIgnoreCase(s))
            {
                mTitle = (String) mRawReasons.get(s);
            }
            else if (!"type".equalsIgnoreCase(s))
            {
                //Each reason can potentially have subreasons. Look above for the sample JSON, quality_of_service.
                Object object = mRawReasons.get(s);
                Reasons subReasons = null;

                if (object instanceof Map)
                {
                    Map<String, Object> rawSubReasons = (Map<String, Object>) object;
                    if (!rawSubReasons.isEmpty())
                    {
                        subReasons = new Reasons(rawSubReasons, isCleaning, s);
                    }
                }

                //capitalize the first letter
                mReasons.add(new Reason(s, StringUtils.capitalizeFirstCharacter(s).replace("_", " "), subReasons, isCleaning));
            }
        }
    }

    public String getTitle()
    {
        return mTitle;
    }

    public List<Reason> getReasons()
    {
        return mReasons;
    }

    public String getKey()
    {
        return mKey;
    }
}
