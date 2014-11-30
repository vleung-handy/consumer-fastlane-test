package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelHorizontalView;

final class BookingOptionsSpinnerView extends BookingOptionsIndexView {
    private WheelHorizontalView optionsSpinner;

    BookingOptionsSpinnerView(final Context context, final BookingOption option,
                              final OnUpdatedListener updateListener) {
        super(context, R.layout.view_booking_options_spinner, option, updateListener);
        init(context);
    }

    BookingOptionsSpinnerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsSpinnerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context) {
        final String type = option.getType();
        if (!type.equals("quantity") && !type.equals("option_picker")) return;

        optionsSpinner = (WheelHorizontalView)this.findViewById(R.id.options_spinner);

        final OptionsAdapter adapter = new OptionsAdapter<>(context, optionsList,
                R.layout.view_spinner_option, R.id.text);

        optionsSpinner.setViewAdapter(adapter);
        optionsSpinner.setCurrentItem(Integer.parseInt(option.getDefaultValue()));
        optionsSpinner.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!warningsMap.isEmpty()) handleWarnings(getCurrentIndex());
                if (!childMap.isEmpty()) handleChildren(getCurrentIndex());
                if (updateListener != null) updateListener
                        .onUpdate(BookingOptionsSpinnerView.this);
            }
        });

        optionsSpinner.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final View circleView = BookingOptionsSpinnerView.this.findViewById(R.id.circle_view);
                        circleView.getLayoutParams().width = adapter.getMaxItemWidth();
                        circleView.getLayoutParams().height = adapter.getMaxItemHeight();
                    }
                });

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    final String getCurrentValue() {
        return optionsList[optionsSpinner.getCurrentItem()];
    }

    final void setCurrentIndex(final int index) {
        if (index < 0) return;

        optionsSpinner.setCurrentItem(index);
        if (updateListener != null) updateListener
                .onUpdate(BookingOptionsSpinnerView.this);
        invalidate();
        requestLayout();
    }

    final int getCurrentIndex() {
        return optionsSpinner.getCurrentItem();
    }

    public final void hideSeparator() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource((R.drawable.booking_cell_last));
        invalidate();
        requestLayout();
    }
}
