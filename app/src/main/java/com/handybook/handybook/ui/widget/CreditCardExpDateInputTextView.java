package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.handybook.handybook.util.TextUtils;
import com.stripe.android.model.Card;

public final class CreditCardExpDateInputTextView extends InputTextField {

    public CreditCardExpDateInputTextView(final Context context) {
        super(context);
        init();
    }

    public CreditCardExpDateInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardExpDateInputTextView(final Context context, final AttributeSet attrs,
                                          final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        super.init();

        InputFilter[] filterArray = new InputFilter[]{ new InputFilter.LengthFilter(5)};
        this.setFilters(filterArray);

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
                CreditCardExpDateInputTextView.this.removeTextChangedListener(this);

                CreditCardExpDateInputTextView.this.setText(TextUtils
                        .formatCreditCardExpDate(editable.toString()));

                CreditCardExpDateInputTextView.this
                        .setSelection(CreditCardExpDateInputTextView.this.getText().length());

                CreditCardExpDateInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    public final boolean validate() {
        final int[] expDate = getExpDate();
        final Card card = new Card("", expDate[0], expDate[1], "");

        if (!card.validateExpiryDate()) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    public final int getExpMonth() {
        return getExpDate()[0];
    }

    public final int getExpYear() {
        return getExpDate()[1];
    }

    private final int[] getExpDate() {
        final String expDate = this.getText().toString().trim();
        int expMonth = -1;
        int expYear = -1;

        try {
            final String[] date = expDate.split("/");
            if (date.length == 2) {
                expMonth = Integer.parseInt(date[0]);
                expYear = Integer.parseInt(date[1]);
            }
        } catch (NumberFormatException e) {}

        return new int[]{expMonth, expYear};
    }
}
