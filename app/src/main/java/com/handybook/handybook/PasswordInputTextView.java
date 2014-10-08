package com.handybook.handybook;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

final class PasswordInputTextView extends InputTextField {

    public PasswordInputTextView(final Context context) {
        super(context);
        init();
    }

    public PasswordInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setTypeface(Typeface.DEFAULT);
    }

    boolean validate() {
        if (this.getText().toString().length() <= 3) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }
}
