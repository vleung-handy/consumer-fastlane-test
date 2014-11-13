package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.stripe.android.model.Card;

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
                CreditCardInputTextView.this.removeTextChangedListener(this);

                final TextUtils.CreditCardType type;
                final String cardNumber = CreditCardInputTextView.this.getText().toString();
                final Card dummyCard = new Card("", 0, 0, "");
                dummyCard.setNumber(cardNumber);

                if (dummyCard.getType() == null) type = TextUtils.CreditCardType.OTHER;
                else {
                    switch (dummyCard.getType()) {
                        case Card.AMERICAN_EXPRESS:
                            type = TextUtils.CreditCardType.AMEX;
                            break;

                        case Card.DISCOVER:
                            type = TextUtils.CreditCardType.DISCOVER;
                            break;

                        case Card.MASTERCARD:
                            type = TextUtils.CreditCardType.MASTERCARD;
                            break;

                        case Card.VISA:
                            type = TextUtils.CreditCardType.VISA;
                            break;

                        default:
                            type = TextUtils.CreditCardType.OTHER;
                    }
                }

                CreditCardInputTextView.this.setText(TextUtils
                        .formatCreditCardNumber(type, editable.toString()));

                CreditCardInputTextView.this
                        .setSelection(CreditCardInputTextView.this.getText().length());

                CreditCardInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    final boolean validate() {
        final String cardNumber = this.getText().toString().trim();
        highlight();
        unHighlight();
        return true;
    }

    final String getCardNumber() {
        return this.getText().toString().trim();
    }
}
