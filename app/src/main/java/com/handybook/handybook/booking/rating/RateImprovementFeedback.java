package com.handybook.handybook.booking.rating;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the class that holds the user response/selections from PrerateProInfo. Here is a sample json:
 * <p>
 * "rating_attributes": {
 * "professionalism": {},
 * "left_early": {},
 * "arrived_late": ["more_than_30_minutes_late"],
 * "supplies": {},
 * "quality_of_service": ["living_room", "kitchen", "bedroom"]
 * }
 * <p>
 * The above means the user have selected "professionalism", "left_early", "arrived_late", "supplies",
 * and "quality_of_service" as reasons
 * <p>
 * Created by jtse on 3/31/16.
 */
public class RateImprovementFeedback implements Serializable {

    @SerializedName("rating_attributes")
    private HashMap<String, ArrayList<String>> mSelectedOptions;
    private String mBookingId;

    public RateImprovementFeedback(String bookingId) {
        mSelectedOptions = new HashMap<>();
        mBookingId = bookingId;
    }

    public void putAll(HashMap<String, ArrayList<String>> optionsToAdd) {
        mSelectedOptions.putAll(optionsToAdd);
    }

    public String getBookingId() {
        return mBookingId;
    }

    public HashMap<String, ArrayList<String>> getSelectedOptions() {
        return mSelectedOptions;
    }
}
