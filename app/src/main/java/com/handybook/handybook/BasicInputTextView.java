package com.handybook.handybook;

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

    final void setMinLength(final int minLength) {
        this.minLength = minLength;
    }

    final boolean validate() {
        if (getInput().length() < minLength) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }
    
    final String getInput() {
        return this.getText().toString().trim();
    }
}
