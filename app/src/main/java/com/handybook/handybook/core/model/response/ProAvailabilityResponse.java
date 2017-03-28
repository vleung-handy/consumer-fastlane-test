package com.handybook.handybook.core.model.response;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProAvailabilityResponse implements Serializable {

    @SerializedName("time_zone")
    private String mTimZone;
    @SerializedName("start_time_stride")
    private int mStartTimeStride;
    @SerializedName("availability")
    private List<Availability> mAvailabilities;

    public ProAvailabilityResponse(
            String timZone, int startTimeStride, List<Availability> availabilities
    ) {
        mTimZone = timZone;
        mStartTimeStride = startTimeStride;
        mAvailabilities = availabilities;
    }

    public String getTimZone() {
        return mTimZone;
    }

    public int getStartTimeStride() {
        return mStartTimeStride;
    }

    @NonNull
    public List<Availability> getAvailabilities() {
        return mAvailabilities;
    }

    public static class Availability implements Serializable {

        @SerializedName("date")
        private String mDate;
        @SerializedName("start_time_intervals")
        private List<TimeInterval> mTimeIntervals;

        public Availability(
                final String date, final List<TimeInterval> timeIntervals
        ) {
            mDate = date;
            mTimeIntervals = timeIntervals;
        }

        public String getDate() {
            return mDate;
        }

        public List<TimeInterval> getTimeIntervals() {
            return mTimeIntervals;
        }
    }


    public static class TimeInterval implements Serializable {

        @SerializedName("interval_start")
        private String mIntervalStart;
        @SerializedName("interval_end")
        private String mIntervalEnd;

        public TimeInterval(final String intervalStart, final String intervalEnd) {
            mIntervalStart = intervalStart;
            mIntervalEnd = intervalEnd;
        }

        public String getIntervalStart() {
            return mIntervalStart;
        }

        public String getIntervalEnd() {
            return mIntervalEnd;
        }
    }
}
