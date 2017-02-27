package com.handybook.handybook.account.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handybook.handybook.library.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.handybook.handybook.booking.constant.BookingRecurrence.BookingRecurrenceCode;

public class FrequencySelectionsView extends LinearLayout {

    private List<FrequencyOptionView> mFrequencyOptionViews = new LinkedList<>();

    public FrequencySelectionsView(final Context context) {
        super(context);
    }

    public FrequencySelectionsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void addOption(@BookingRecurrenceCode int frequency, String price, boolean isCurrent) {
        FrequencyOptionView view = new FrequencyOptionView(getContext());
        String frequencyText = StringUtils.getFrequencyText(getContext(), frequency);
        view.setProperty(mFrequencyOptionViews, frequency, frequencyText, price, isCurrent);
        mFrequencyOptionViews.add(view);
        addView(view);
    }

    public int getCurrentlySelectedFrequency() {
        for (FrequencyOptionView view : mFrequencyOptionViews) {
            if (view.isChecked()) {
                return view.getFrequency();
            }
        }
        return 0;
    }
}
