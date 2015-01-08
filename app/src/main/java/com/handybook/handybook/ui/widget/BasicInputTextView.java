package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

public final class BasicInputTextView extends InputTextField {
    private int minLength;

    public BasicInputTextView(final Context context, final int minLength) {
        super(context);
        init(minLength);
    }

    public BasicInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final int minLength) {
        this.minLength = minLength;
    }

    public final void setMinLength(final int minLength) {
        this.minLength = minLength;
    }

    public final boolean validate() {
        if (getInput().length() < minLength) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }
    
    public final String getInput() {
        return this.getText().toString().trim();
    }
}
