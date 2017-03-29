package com.handybook.handybook.core.model.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.model.ProTimeInterval;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.handybook.handybook.library.util.DateTimeUtils.YEAR_MONTH_DATE_FORMATTER;

public class ProAvailabilityResponse implements Serializable {

    @SerializedName("time_zone")
    private String mTimZone;
    @SerializedName("start_time_stride")
    private int mStartTimeStride;
    @SerializedName("availability")
    private List<AvailableDay> mAvailableDays;

    public ProAvailabilityResponse(
            String timZone, int startTimeStride, List<AvailableDay> availableDays
    ) {
        mTimZone = timZone;
        mStartTimeStride = startTimeStride;
        mAvailableDays = availableDays;
    }

    @NonNull
    public String getTimZone() {
        return mTimZone;
    }

    @NonNull
    public int getStartTimeStride() {
        return mStartTimeStride;
    }

    @NonNull
    public List<AvailableDay> getAvailableDays() {
        return mAvailableDays;
    }

    @Nullable
    public AvailableDay findAvailableDay(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        for (AvailableDay day : getAvailableDays()) {
            try {
                Date date = YEAR_MONTH_DATE_FORMATTER.parse(day.getDate());
                calendar.setTime(date);
                if (calendar.get(Calendar.YEAR) == year &&
                    calendar.get(Calendar.MONTH) == monthOfYear &&
                    calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                    return day;
                }
            }
            catch (ParseException e) {
                Crashlytics.log(e.getMessage());
            }

        }
        if (getAvailableDays().size() > 0) {
            return getAvailableDays().get(0);
        }
        return null;
    }

    public static class AvailableDay implements Serializable {

        @SerializedName("date")
        private String mDate;
        @SerializedName("start_time_intervals")
        private List<ProTimeInterval> mTimeIntervals;

        public AvailableDay(
                final String date, final List<ProTimeInterval> timeIntervals
        ) {
            mDate = date;
            mTimeIntervals = timeIntervals;
        }

        @NonNull
        public String getDate() {
            return mDate;
        }

        @NonNull
        public List<ProTimeInterval> getTimeIntervals() {
            return mTimeIntervals;
        }
    }
}
