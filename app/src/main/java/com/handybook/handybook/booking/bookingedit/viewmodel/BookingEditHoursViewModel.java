package com.handybook.handybook.booking.bookingedit.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.model.PriceInfo;
import com.handybook.handybook.library.util.MathUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.ValidationUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class BookingEditHoursViewModel {

    //TODO: create a view model for other edit screens and move some of these methods to a super
    private final BookingEditHoursInfoResponse mEditHoursInfo;
    /**
     * A comparator that compares strings as numbers
     */
    private static final Comparator<String> COMPARATOR_STRING_NUMERIC_ASCENDING
            = new Comparator<String>() {
        @Override
        public int compare(final String lhs, final String rhs) {
            //note that this is less computationally efficient than other methods, but more readable
            Float f1 = Float.parseFloat(lhs);
            Float f2 = Float.parseFloat(rhs);
            return f1.compareTo(f2);
        }
    };

    private BookingEditHoursViewModel(@NonNull final BookingEditHoursInfoResponse editHoursInfo) {
        mEditHoursInfo = editHoursInfo;
    }

    @NonNull
    public static BookingEditHoursViewModel from(
            @NonNull final BookingEditHoursInfoResponse editHoursInfo
    ) {
        return new BookingEditHoursViewModel(editHoursInfo);
    }

    /**
     * @return A formatted string that indicates the number of base hours in the original booking
     */
    @NonNull
    public String getBaseHoursFormatted() {
        return getHoursFormatted(mEditHoursInfo.getBaseHours());
    }

    /**
     * @return A formatted string that indicates the base price of the original booking
     */
    @Nullable
    public String getBasePriceFormatted() {
        return getTotalDuePriceFormatted(getBaseHoursFormatted());
    }

    /**
     * @return A formatted string that indicates the number of hours allocated for service extras in the original booking
     */
    @NonNull
    public String getExtrasHoursFormatted() {
        return getHoursFormatted(mEditHoursInfo.getExtrasHours());
    }

    /**
     * @return A formatted string that indicates the total price of the service extras in the original booking
     */
    @Nullable
    public String getExtrasPriceFormatted() {
        return mEditHoursInfo.getExtrasPrice().getFormattedPrice();
    }

    public boolean isSelectedHoursLessThanBaseHours(final double selectedHours) {
        return selectedHours < mEditHoursInfo.getBaseHours();
    }

    /**
     *
     * @return A formatted string that indicates the number of hours the user has selected
     */
    @NonNull
    public String getSelectedHoursFormatted(final double selectedHours) {
        return getHoursFormatted(selectedHours);
    }

    /**
     *
     * @return A formatted string that indicates the difference between the selected hours and the base hours in the original booking
     */
    @Nullable
    public String getAddedHoursPriceFormatted(final double selectedHours) {
        return getPriceDifferenceFormatted(getSelectedHoursFormatted(selectedHours));
    }

    /**
     * @return A formatted string that indicates the new total price of the booking
     */
    @Nullable
    public String getTotalDuePriceFormatted(final double selectedHours) {
        return getTotalDuePriceFormatted(getTotalHoursFormatted(selectedHours));
    }

    /**
     * @return A formatted string that indicates the number of hours that the user is adding to the original booking
     */
    @NonNull
    public String getAddedHoursFormatted(final double selectedHours) {
        return getHoursFormatted(getAddedHours(selectedHours));
    }

    /**
     * @return A formatted string that indicates the new total hours of the booking
     */
    @NonNull
    public String getTotalHoursFormatted(final double selectedHours) {
        return getHoursFormatted(
                mEditHoursInfo.getBaseHours()
                + mEditHoursInfo.getExtrasHours()
                + getAddedHours(selectedHours));
    }

    @Nullable
    public String getTotalSubtext() {
        return mEditHoursInfo.getTotalSubtext();
    }

    /**
     * @return A formatted string that indicates the date that the booking will be billed for
     */
    @Nullable
    public String getFutureBillDateFormatted() {
        return mEditHoursInfo.getPaidStatus().getFutureBillDateFormatted();
    }

    /**
     *
     * @return The difference in hours between the base hours in the original booking, and the number of hours that the user has selected
     */
    private double getAddedHours(final double selectedHours) {
        return selectedHours - mEditHoursInfo.getBaseHours();
    }

    /**
     *
     * @return The number of base hours (this excludes extras) in the original booking
     */
    public double getBaseHours() {
        return mEditHoursInfo.getBaseHours();
    }

    //used to create the options array

    /**
     *
     * @return A string array that contains numerically sorted hours that the user can select from
     */
    @NonNull
    public String[] getSelectableHoursArray() {
        String optionHourStrings[] = mEditHoursInfo.getPriceMap().keySet().toArray(new String[]{});
        //BookingOption.setOptions() only accepts an array of strings
        Arrays.sort(optionHourStrings, COMPARATOR_STRING_NUMERIC_ASCENDING);
        return optionHourStrings;
    }

    //TODO: will rename these later
    // Easter egg

    /**
     * @return A string that represents the given number of hours, with 0-1 decimal points. For example, 3.0 becomes 3
     */
    @NonNull
    private String getHoursFormatted(double hours) {
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc
        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatToAtMostOneDecimalPlaceUSLocale(hours);
    }

    @Nullable
    private String getTotalDuePriceFormatted(final String key) {
        Map<String, PriceInfo> priceMap = mEditHoursInfo.getTotalPriceMap();
        return ValidationUtils.isMapKeyEntryValid(key, priceMap) ?
               priceMap.get(key).getTotalDueFormatted() : null;
    }

    @Nullable
    private String getPriceDifferenceFormatted(final String key) {
        Map<String, PriceInfo> priceMap = mEditHoursInfo.getPriceMap();
        return ValidationUtils.isMapKeyEntryValid(key, priceMap) ?
               priceMap.get(key).getPriceDifferenceFormatted() : null;
    }

    public boolean hasFutureBillDate() {
        return mEditHoursInfo.getPaidStatus().hasFutureBillDate();
    }
}
