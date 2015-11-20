package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.ui.adapter.OptionsAdapter;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelHorizontalView;

public final class BookingOptionsSpinnerView extends BookingOptionsIndexView {
    private WheelHorizontalView optionsSpinner;
    private OptionsAdapter adapter;
    private View circleView;

    public BookingOptionsSpinnerView(final Context context, final BookingOption option,
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
        if (!type.equals(BookingOption.TYPE_QUANTITY) && !type.equals(BookingOption.TYPE_OPTION_PICKER)) return;

        mainLayout = (RelativeLayout)this.findViewById(R.id.rel_layout);
        circleView = BookingOptionsSpinnerView.this.findViewById(R.id.circle_view);
        optionsSpinner = (WheelHorizontalView)this.findViewById(R.id.options_spinner);

        adapter = new OptionsAdapter<>(context, optionsList,
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
                        resizeIndicators();
                    }
                });

        // calling again to handle 'onback' case
        resizeIndicators();

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    public final String getCurrentValue() {
        return optionsList[optionsSpinner.getCurrentItem()];
    }

    public final void setCurrentIndex(final int index) {
        if (index < 0) return;

        optionsSpinner.setCurrentItem(index);
        if (updateListener != null) updateListener
                .onUpdate(BookingOptionsSpinnerView.this);
        invalidate();
        requestLayout();
    }

    public final int getCurrentIndex() {
        return optionsSpinner.getCurrentItem();
    }

    private void resizeIndicators() {
        circleView.getLayoutParams().width = adapter.getMaxItemWidth();
        circleView.getLayoutParams().height = adapter.getMaxItemHeight();
    }
}
