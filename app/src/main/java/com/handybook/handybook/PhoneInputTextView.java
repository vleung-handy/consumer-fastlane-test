package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

final class PhoneInputTextView extends InputTextField {
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

                String shortFormat = "(%s) %s", longFormat = "(%s) %s-%s";
                if (countryCode != null && countryCode.equals("+44")) {
                    shortFormat = "%s %s";
                    longFormat = "%s %s %s";
                }

                final String raw = editable.toString().replaceAll("\\D+", "");

                if (raw.length() < 4) PhoneInputTextView.this.setText(raw);

                else if (raw.length() >= 4 && raw.length() <= 6) PhoneInputTextView.this
                        .setText(String.format(shortFormat, raw.substring(0, 3), raw.substring(3)));

                else if (raw.length() >= 7 && raw.length() <= 10) PhoneInputTextView.this
                        .setText(String.format(longFormat, raw.substring(0, 3),
                                raw.substring(3, 6), raw.substring(6)));

                else PhoneInputTextView.this.setText(raw);

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

    boolean validate() {
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
}
