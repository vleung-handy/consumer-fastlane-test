package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.stripe.android.model.Card;

public final class CreditCardInputTextView extends InputTextField {
    private CreditCard.Type cardType;
    private int defaultInputType;

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
        defaultInputType = this.getInputType();

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

                cardType = CreditCard.Type.OTHER;
                final String cardNumber = CreditCardInputTextView.this.getText().toString();
                final Card dummyCard = new Card("", 0, 0, "");
                dummyCard.setNumber(cardNumber);

                if (dummyCard.getType() == null) cardType = CreditCard.Type.OTHER;
                else {
                    switch (dummyCard.getType()) {
                        case Card.AMERICAN_EXPRESS:
                            cardType = CreditCard.Type.AMEX;
                            break;

                        case Card.DISCOVER:
                            cardType = CreditCard.Type.DISCOVER;
                            break;

                        case Card.MASTERCARD:
                            cardType = CreditCard.Type.MASTERCARD;
                            break;

                        case Card.VISA:
                            cardType = CreditCard.Type.VISA;
                            break;

                        default:
                            cardType = CreditCard.Type.OTHER;
                    }
                }

                CreditCardInputTextView.this.setText(TextUtils
                        .formatCreditCardNumber(cardType, editable.toString()));

                CreditCardInputTextView.this
                        .setSelection(CreditCardInputTextView.this.getText().length());

                CreditCardInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    final boolean validate() {
        final String cardNumber = this.getText().toString().trim();
        final Card card = new Card(cardNumber, -1, -1, "");

        if (cardNumber.length() < 1 || !card.validateNumber()) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    final String getCardNumber() {
        return this.getText().toString().trim();
    }

    final CreditCard.Type getCardType() {
        return cardType;
    }

    void setDisabled(final boolean disabled, final String hint) {
        if (disabled) {
            setHint(hint);
            setInputType(InputType.TYPE_NULL);
            setEnabled(false);
        }
        else {
            setHint(hint);
            setInputType(defaultInputType);
            setEnabled(true);
        }
    }
}
