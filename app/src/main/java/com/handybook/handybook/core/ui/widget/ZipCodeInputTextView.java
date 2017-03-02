package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.library.ui.view.InputTextField;

public final class ZipCodeInputTextView extends InputTextField {

    public ZipCodeInputTextView(final Context context) {
        super(context);
    }

    public ZipCodeInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ZipCodeInputTextView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
    }

    public final boolean validate() {
        if (this.getText().toString().trim().length() < 1) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final String getZipCode() {
        return this.getText().toString().trim();
    }
}
