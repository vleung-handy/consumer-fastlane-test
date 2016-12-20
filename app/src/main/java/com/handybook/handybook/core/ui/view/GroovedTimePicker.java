package com.handybook.handybook.core.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class GroovedTimePicker extends TimePicker
{
    private static final DecimalFormat FORMATTER = new DecimalFormat("00");
    private int mInterval = 1; // 1 minute by default
    private NumberPicker mMinutePicker;

    public GroovedTimePicker(final Context context)
    {
        super(context);
    }

    public GroovedTimePicker(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GroovedTimePicker(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    )
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GroovedTimePicker(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setInterval(final int interval)
    {
        mInterval = interval;
        setMinutePicker();
    }

    private void setMinutePicker()
    {
        int numValues = (int) TimeUnit.HOURS.toMinutes(1) / mInterval;
        String[] displayedValues = new String[numValues];
        for (int i = 0; i < numValues; i++)
        {
            displayedValues[i] = FORMATTER.format(i * mInterval);
        }
        View minute = findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        if (minute != null && minute instanceof NumberPicker)
        {
            mMinutePicker = (NumberPicker) minute;
            mMinutePicker.setMinValue(0);
            mMinutePicker.setMaxValue(numValues - 1);
            mMinutePicker.setDisplayedValues(displayedValues);
            mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
            {
                @Override
                public void onValueChange(final NumberPicker np, final int oldVal, final int newVal)
                {
                    assert true;
                    // DO NOTHING AT ALL
                    // Fixes issue with listener updating hours when minutes overflow(ish)
                    // https://github.com/android/platform_frameworks_base/blob/master/ ...
                    // ... core/java/android/widget/TimePickerSpinnerDelegate.java#L120-L142
                }
            });
        }
    }

    @NonNull
    @Override
    public Integer getCurrentMinute()
    {
        Integer result = super.getCurrentMinute();
        if (mMinutePicker != null)
        {
            return mMinutePicker.getValue() * mInterval;
        } else
        {
            return result;
        }
    }
}
