package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.library.ui.view.FreezableInputTextView;
import com.handybook.handybook.library.util.CreditCardUtils;
import com.stripe.android.model.Card;

public final class CreditCardNumberInputTextView extends FreezableInputTextView {

    public CreditCardNumberInputTextView(final Context context) {
        super(context);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    public CreditCardNumberInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    public CreditCardNumberInputTextView(
            final Context context, final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    private class CreditCardInputTextWatcher extends HandyTextWatcher {

        @Override
        public void afterTextChanged(final Editable editable) {
            super.afterTextChanged(editable);

            if (isDeletingFromEnd()) { return; }

            final String number = editable.toString();
            final String formattedNumber =
                    isAddingLastChar()
                    ? CreditCardUtils.formatCreditCardNumberAfterEndingDigitAdded(number)
                    : CreditCardUtils.formatCreditCardNumber(number);

            if (!number.equals(formattedNumber)) {
                changeText(CreditCardNumberInputTextView.this, formattedNumber);
            }
        }
    }

    public final boolean validate() {
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

    public final String getCardNumber() {
        return getText().toString().replaceAll("\\D+", "");
    }

    public final CreditCard.Type getCardType() {
        return CreditCardUtils.getCreditCardType(getText().toString());
    }
}
