package com.handybook.handybook.booking.rating;

import com.handybook.handybook.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sample RAW reasons:
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
 * }
 * <p>
 * Created by jtse on 4/1/16.
 */
public class Reasons implements Serializable
{
    public String mTitle;
    public List<Reason> mReasons;


    /**
     * key is used as the parent key of the list. For example, quality_of_service is the
     * key for the list [kitchen, bathroom, bedroom, etc.]
     */
    public String mKey;

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
            else if ("type".equalsIgnoreCase(s))
            {
                continue;
            }
            else
            {
                //Each reason can potentially have subreasons. Look above for the sample JSON, quality_of_service.
                Object object = mRawReasons.get(s);
                Reasons subReasons = null;

                if (object instanceof Map)
                {
                    Map<String, Object> rawSubReasons = (Map<String, Object>) object;
                    if (rawSubReasons != null && !rawSubReasons.isEmpty())
                    {
                        subReasons = new Reasons(rawSubReasons, isCleaning, s);
                    }
                }

                //capitalize the first letter
                mReasons.add(new Reason(s, StringUtils.capitalizeFirstCharacter(s).replace("_", " "), subReasons, isCleaning));
            }
        }
    }
}
