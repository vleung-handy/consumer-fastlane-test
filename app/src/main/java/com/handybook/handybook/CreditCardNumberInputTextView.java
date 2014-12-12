package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.stripe.android.model.Card;

public final class CreditCardNumberInputTextView extends FreezableInputTextView {
    private CreditCard.Type cardType;

    public CreditCardNumberInputTextView(final Context context) {
        super(context);
        init();
    }

    public CreditCardNumberInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardNumberInputTextView(final Context context, final AttributeSet attrs,
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
                CreditCardNumberInputTextView.this.removeTextChangedListener(this);

                final String cardNumber = CreditCardNumberInputTextView.this.getText().toString();
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

                CreditCardNumberInputTextView.this.setText(TextUtils
                        .formatCreditCardNumber(cardType, editable.toString()));

                CreditCardNumberInputTextView.this
                        .setSelection(CreditCardNumberInputTextView.this.getText().length());

                CreditCardNumberInputTextView.this.addTextChangedListener(this);
            }
        });
    }

    final boolean validate() {
        final Card card = new Card(getCardNumber(), -1, -1, "");
        
        if (!card.validateNumber()) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    final String getCardNumber() {
        return this.getText().toString().replaceAll("\\D+","");
    }

    final CreditCard.Type getCardType() {
        return cardType;
    }
}
