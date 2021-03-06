package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.handybook.handybook.library.ui.view.InputTextField;

public final class PasswordInputTextView extends InputTextField {

    public PasswordInputTextView(final Context context) {
        super(context);
        init();
    }

    public PasswordInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordInputTextView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setTypeface(Typeface.DEFAULT);
    }

    public final String getPassword() {
        return this.getText().toString().trim();
    }

    public final boolean validate() {
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
