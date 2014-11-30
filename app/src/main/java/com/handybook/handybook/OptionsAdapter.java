package com.handybook.handybook;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

final class OptionsAdapter<T> extends ArrayWheelAdapter<T> {
    private static final int MIN_WIDTH = 50;
    private static final int PADDING = 32;

    private int maxWidth;
    private int maxHeight;
    private int padding;

    OptionsAdapter(final Context context, final T items[], final int itemRes, final int textRes) {
        super(context, items);
        this.setItemResource(itemRes);
        this.setItemTextResource(textRes);

        final View view = inflater.inflate(itemRes, null);
        final TextView text = (TextView)view.findViewById(textRes);

        for (final T item : items) {
            Paint p = new Paint();
            p.set(text.getPaint());
            p.setTextSize(text.getTextSize());

            maxWidth = Math.max(maxWidth, (int)p.measureText(item.toString()));
            maxWidth = Math.max(maxWidth, MIN_WIDTH);
        }

        padding = Utils.toDP(PADDING, context);
        maxWidth += padding;
        maxHeight = MIN_WIDTH + padding;
    }

    @Override
    protected void configureTextView(TextView view) {}

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        final View view = super.getItem(index, convertView, parent);
        view.setLayoutParams(new ViewGroup.LayoutParams(maxWidth, view.getLayoutParams().height));
        return view;
    }

    int getMaxItemWidth() {
        return maxWidth;
    }

    int getMaxItemHeight() {
        return maxHeight;
    }
}
