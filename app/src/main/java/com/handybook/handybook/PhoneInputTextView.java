package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;

final class PhoneInputTextView extends InputTextField {

    public PhoneInputTextView(final Context context) {
        super(context);
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    boolean validate() {
        String phone = this.getText().toString();
        if (phone.replaceAll("\\D+","").length() != 10) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }
}
