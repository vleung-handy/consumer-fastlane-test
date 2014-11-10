package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

final class BookingOptionsSelectView extends BookingOptionsIndexView {
    private RadioGroup radioGroup;

    BookingOptionsSelectView(final Context context, final BookingOption option,
                             final OnUpdatedListener updateListener) {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        init(context);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context) {
        if (!option.getType().equals("option")) return;

        radioGroup = (RadioGroup)this.findViewById(R.id.radio_group);

        for (final String optionText : optionsList) {
            final RadioButton radioButton = (RadioButton)LayoutInflater.from(context)
                    .inflate(R.layout.view_booking_select_option, null);
            radioButton.setText(optionText);
            radioGroup.addView(radioButton);
        }

        radioGroup.check(radioGroup.getChildAt(Integer.parseInt(option.getDefaultValue())).getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                if (!warningsMap.isEmpty()) handleWarnings(getCurrentIndex());
                if (!childMap.isEmpty()) handleChildren(getCurrentIndex());
                if (updateListener != null) updateListener
                        .onUpdate(BookingOptionsSelectView.this);
            }
        });

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    final String getCurrentValue() {
        return optionsList[radioGroup
                .indexOfChild(this.findViewById(radioGroup.getCheckedRadioButtonId()))];
    }

    final void setCurrentIndex(final int index) {
        radioGroup.check(radioGroup.getChildAt(index).getId());
        if (updateListener != null) updateListener
                .onUpdate(BookingOptionsSelectView.this);
        invalidate();
        requestLayout();
    }

    final int getCurrentIndex() {
        return radioGroup
                .indexOfChild(this.findViewById(radioGroup.getCheckedRadioButtonId()));
    }

    public final void hideSeparator() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource((R.drawable.booking_cell_last));
        invalidate();
        requestLayout();
    }
}
