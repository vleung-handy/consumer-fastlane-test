package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * a time picker with just one spinner, so consecutive values would be
 * something like "5:30 pm", "6:00 pm", etc
 * (hour, minute and am/pm cannot be specified individually)
 */
public class SingleSpinnerTimePicker extends NumberPicker {

    private Adapter mAdapter;

    public SingleSpinnerTimePicker(final Context context) {
        super(context);
    }

    public SingleSpinnerTimePicker(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelectedHourAndMinute(int minuteOfDay) {
        Integer index = mAdapter.getCalculatedIndexForUnderlyingValue(minuteOfDay);
        if (index != null) {
            setValue(index);
        }
    }

    public void setSelectedHourAndMinute(int hour, int minute) {
        setSelectedHourAndMinute((int) TimeUnit.HOURS.toMinutes(hour) + minute);
    }

    /**
     * Assumes that all of the given params are within the noted ranges
     *
     * @see Adapter
     */
    public void initialize(
            int minHourOfDay,
            int minMinuteOfMinHour,
            int maxHourOfDay,
            int maxMinuteOfMaxHour,
            int minuteInterval,
            @NonNull SimpleDateFormat timeFormatter
    ) {
        mAdapter = new Adapter(
                minHourOfDay,
                minMinuteOfMinHour,
                maxHourOfDay,
                maxMinuteOfMaxHour,
                minuteInterval,
                timeFormatter
        );

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setDisplayedValues(mAdapter.getDisplayValues());
        setWrapSelectorWheel(true);
        setMinValue(0);
        setMaxValue(mAdapter.getDisplayValues().length - 1);
    }

    /**
     * @return [0-23] hour of the 24-hour day, i.e. if the time is 18:45, this will return 18
     */
    public int getCurrentHourOfDay() {
        int selectedValue = getValue();
        return (int) TimeUnit.MINUTES.toHours(mAdapter.getUnderlyingValueAtIndex(selectedValue));
    }

    /**
     * @return minute of the hour, i.e. if the time is 18:45, this will return 45
     */
    public int getCurrentMinuteOfHour() {
        int selectedValue = getValue();
        return mAdapter.getUnderlyingValueAtIndex(selectedValue) %
               ((int) TimeUnit.HOURS.toMinutes(1));
    }

    class Adapter extends BaseDataAdapter<String, Integer> {

        private String[] mDisplayValues;

        private final int mMinMinuteOfDay;
        private final int mMaxMinuteOfDay;
        private final int mMinuteInterval;

        /**
         * Assumes that all of the given params are within the noted ranges
         *
         * @param minHourOfDay [0-23] used to determine what the min time value should be
         * @param minMinuteOfMinHour [0-59] used to determine what the min time value should be
         * @param maxHourOfDay [0-23] used to determine what the max time value should be
         * @param maxMinuteOfMaxHour [0-59] used to determine what the max time value should be
         * @param minuteInterval >=1. the minute interval between each time option
         * @param timeFormatter the time format used for the display values
         */
        Adapter(
                int minHourOfDay,
                int minMinuteOfMinHour,
                int maxHourOfDay,
                int maxMinuteOfMaxHour,
                int minuteInterval,
                @NonNull SimpleDateFormat timeFormatter
        ) {
            mMaxMinuteOfDay = (int) TimeUnit.HOURS.toMinutes(maxHourOfDay) + maxMinuteOfMaxHour;
            mMinMinuteOfDay = (int) TimeUnit.HOURS.toMinutes(minHourOfDay) + minMinuteOfMinHour;
            mMinuteInterval = minuteInterval;

            int minutesBetween = mMaxMinuteOfDay - mMinMinuteOfDay;
            int totalNumValues = minutesBetween / minuteInterval + 1;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(timeFormatter.getTimeZone());
            calendar.set(Calendar.HOUR_OF_DAY, minHourOfDay);
            calendar.set(Calendar.MINUTE, minMinuteOfMinHour);
            mDisplayValues = new String[totalNumValues];
            for (int i = 0; i < totalNumValues; i++) {
                String displayString = timeFormatter.format(calendar.getTime()).toLowerCase();
                mDisplayValues[i] = displayString;
                calendar.add(Calendar.MINUTE, minuteInterval);
            }

        }

        @Override
        public String[] getDisplayValues() {
            return mDisplayValues;
        }

        @Override
        public int getNumItems() {
            return mDisplayValues.length;
        }

        /**
         * @return the minute of day that represents the time option at the given index
         */
        @Override
        public Integer getUnderlyingValueAtIndex(final int index) {
            if (!isIndexValid(index)) { return null; }
            return mMinMinuteOfDay + (index * mMinuteInterval);
        }

        /**
         * used for pre-selecting a value.
         * note that a rounded index will be provided
         * for given values in between picker values
         * @param minuteOfDay
         * @return null if the minuteOfDay is out of range of this picker
         */
        @Nullable
        @Override
        public Integer getCalculatedIndexForUnderlyingValue(Integer minuteOfDay) {
            if (minuteOfDay == null) { return null; }

            int minutesPastMinMinute = minuteOfDay - mMinMinuteOfDay;
            int index = minutesPastMinMinute / mMinuteInterval;
            return isIndexValid(index) ? index : null;
        }
    }


    /**
     * making an interface in case we want to have a slightly different implementation
     * of this time picker later
     * i.e. return a View object instead of a String to display
     *
     * but not bothering to allow outsiders to specify a custom adapter for this picker right now
     * because we currently don't need that functionality so it is not worth the effort
     *
     * @param <DisplayValueClass>
     * @param <UnderlyingValueClass>
     */
    abstract static class BaseDataAdapter<DisplayValueClass, UnderlyingValueClass> {

        abstract DisplayValueClass[] getDisplayValues();

        abstract int getNumItems();

        abstract UnderlyingValueClass getUnderlyingValueAtIndex(int index);

        abstract Integer getCalculatedIndexForUnderlyingValue(UnderlyingValueClass underlyingValue);

        boolean isIndexValid(int index) {
            return index >= 0 && index < getNumItems();
        }

        boolean isItemEnabled(int index) {
            return true;
        }
    }
}
