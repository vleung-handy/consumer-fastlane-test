package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public final class CreditCardInputTextView extends InputTextField {

    public CreditCardInputTextView(final Context context) {
        super(context);
        init();
    }

    public CreditCardInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardInputTextView(final Context context, final AttributeSet attrs,
                                   final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        super.init();
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

            }
        });
    }

    final boolean validate() {
        final String cardNumber = this.getText().toString().trim();
        return true;
    }

    final String getCardNumber() {
        return this.getText().toString().trim();
    }
}
