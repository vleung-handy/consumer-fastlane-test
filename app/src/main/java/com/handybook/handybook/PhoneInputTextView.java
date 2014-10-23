package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

final class PhoneInputTextView extends InputTextField {
    private String countryCode;
    private User user;

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
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start,
                                          final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start,
                                      final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                PhoneInputTextView.this.removeTextChangedListener(this);
                final String prefix = user != null ? user.getPhonePrefix() : null;
                PhoneInputTextView.this.setText(TextUtils.formatPhone(editable.toString(), prefix));
                PhoneInputTextView.this.setSelection(PhoneInputTextView.this.getText().length());
                PhoneInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    final String getCountryCode() {
        return countryCode;
    }

    final void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    final User getUser() {
        return user;
    }

    final void setUser(final User user) {
        this.user = user;
    }

    final boolean validate() {
        final String phone = this.getText().toString();
        if (phone.replaceAll("\\D+","").length() != 10) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    final String getPhoneNumber() {
        return this.countryCode + this.getText().toString().replaceAll("\\D+","");
    }
}
