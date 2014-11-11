package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;

public final class StreetAddressInputTextView extends InputTextField {

    public StreetAddressInputTextView(final Context context) {
        super(context);
    }

    public StreetAddressInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public StreetAddressInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    final boolean validate() {
        final String address = this.getText().toString().trim();
        if (address.length() < 2) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    final String getAddress() {
        return this.getText().toString().trim();
    }
}
