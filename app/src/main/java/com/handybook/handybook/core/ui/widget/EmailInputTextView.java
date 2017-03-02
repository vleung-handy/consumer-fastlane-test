package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handybook.handybook.library.ui.view.InputTextField;
import com.handybook.handybook.library.util.Utils;

public final class EmailInputTextView extends InputTextField {

    public EmailInputTextView(final Context context) {
        super(context);
    }

    public EmailInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public final boolean validate() {
        final String email = this.getText().toString().trim();
        if (email == null || !email.matches(Utils.EMAIL_VALIDATION_REGEX)) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final String getEmail() {
        return this.getText().toString().trim();
    }
}
