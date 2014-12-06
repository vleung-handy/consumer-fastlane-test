package com.handybook.handybook;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

final class OptionsAdapter<T> extends ArrayWheelAdapter<T> {
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

        final Paint paint = new Paint();
        paint.set(text.getPaint());
        paint.setTextSize(text.getTextSize());

        // set default width to length of textView using '10'
        final int minWidth = (int)paint.measureText("10");

        for (final T item : items) {
            maxWidth = Math.max(maxWidth, (int)paint.measureText(item.toString()));
            maxWidth = Math.max(maxWidth, minWidth);
        }

        padding = Utils.toDP(PADDING, context);
        maxWidth += padding;
        maxHeight =  minWidth + padding;
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
