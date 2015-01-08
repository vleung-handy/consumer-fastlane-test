package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

public final class FullNameInputTextView extends InputTextField {

    public FullNameInputTextView(final Context context) {
        super(context);
    }

    public FullNameInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FullNameInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public final boolean validate() {
        final String name = this.getText().toString().trim();
        if (name.split(" ").length < 2) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final String getFirstName() {
        final String[] parts = this.getText().toString().trim().split(" ");
        if (parts.length > 0) return parts[0];
        else return "";
    }

    public final String getLastName() {
        final String[] parts = this.getText().toString().trim().split(" ");
        if (parts.length > 1) return parts[1];
        else return "";
    }
}
