package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;

final class FullNameInputTextView extends InputTextField {

    public FullNameInputTextView(final Context context) {
        super(context);
    }

    public FullNameInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FullNameInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    boolean validate() {
        final String name = this.getText().toString();
        if (name.split(" ").length < 2) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    String getFirstName() {
        final String[] parts = this.getText().toString().trim().split(" ");
        if (parts.length > 0) return parts[0];
        else return "";
    }

    String getLastName() {
        final String[] parts = this.getText().toString().trim().split(" ");
        if (parts.length > 1) return parts[1];
        else return "";
    }
}
