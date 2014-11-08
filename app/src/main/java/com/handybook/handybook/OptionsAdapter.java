package com.handybook.handybook;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

final class OptionsAdapter<T> extends ArrayWheelAdapter<T> {
    private float max = 0;

    OptionsAdapter(final Context context, final T items[], final int itemRes, final int textRes) {
        super(context, items);
        this.setItemResource(itemRes);
        this.setItemTextResource(textRes);

        for (final T item : items) {
            final View v = inflater.inflate(itemRes, null);
            final TextView text = (TextView)v.findViewById(textRes);

            Paint p = new Paint();
            p.set(text.getPaint());
            p.setTextSize(text.getTextSize());

            max = Math.max(max, p.measureText(item.toString()));
        }
    }

    @Override
    protected void configureTextView(TextView view) {}

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        final View view = super.getItem(index, convertView, parent);
        int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)(max / 2.5),
                context.getResources().getDisplayMetrics());

        final int defaultWidth = 160;
        if (width <= defaultWidth) width += (defaultWidth - width);

        view.setLayoutParams(new ViewGroup.LayoutParams(width, view.getLayoutParams().height));
        return view;
    }
}
