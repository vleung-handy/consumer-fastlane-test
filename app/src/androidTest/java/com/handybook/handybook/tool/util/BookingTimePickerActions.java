package com.handybook.handybook.tool.util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import com.handybook.handybook.library.ui.view.SingleSpinnerTimePicker;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

public class BookingTimePickerActions {

    /**
     * Returns a {@link ViewAction} that sets a time on a {@link SingleSpinnerTimePicker}.
     */
    public static ViewAction setTime(final int hours, final int minutes) {

        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                final SingleSpinnerTimePicker timePicker = (SingleSpinnerTimePicker) view;
                timePicker.setSelectedHourAndMinute(hours, minutes);
            }

            @Override
            public String getDescription() {
                return "set time";
            }

            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(SingleSpinnerTimePicker.class), isDisplayed());
            }
        };

    }
}
