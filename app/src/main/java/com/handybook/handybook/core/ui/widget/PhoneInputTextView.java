package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import com.handybook.handybook.library.ui.view.InputTextField;
import com.handybook.handybook.library.util.TextUtils;

public final class PhoneInputTextView extends InputTextField {

    private String countryCode;

    public PhoneInputTextView(final Context context) {
        super(context);
        init();
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhoneInputTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.addTextChangedListener(new HandyTextWatcher() {
            @Override
            public void afterTextChanged(final Editable editable) {
                super.afterTextChanged(editable);
                if (isDeletingFromEnd()) { return; }

                final String phone = editable.toString();
                final String formattedPhone =
                        TextUtils.formatPhone(editable.toString(), getCountryCode());
                if (!phone.equals(formattedPhone)) {
                    changeText(PhoneInputTextView.this, formattedPhone);
                }
            }
        });
    }

    final String getCountryCode() {
        return countryCode;
    }

    public final void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public final boolean validate() {
        final String phone = this.getText().toString();
        if (phone.replaceAll("\\D+", "").length() != 10) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final String getPhoneNumber() {
        return this.countryCode + this.getText().toString().replaceAll("\\D+", "");
    }
}
