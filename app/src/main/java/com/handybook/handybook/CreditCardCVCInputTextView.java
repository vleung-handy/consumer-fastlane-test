package com.handybook.handybook;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.stripe.android.model.Card;

public final class CreditCardCVCInputTextView extends InputTextField {

    public CreditCardCVCInputTextView(final Context context) {
        super(context);
        init();
    }

    public CreditCardCVCInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardCVCInputTextView(final Context context, final AttributeSet attrs,
                                      final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        super.init();
        InputFilter[] filterArray = new InputFilter[]{ new InputFilter.LengthFilter(3)};
        this.setFilters(filterArray);
    }

    final boolean validate() {
        final Card card = new Card("", -1, -1, getCVC());

        if (!card.validateCVC()) {
            highlight();
            return false;
        }
        else {
            unHighlight();
            return true;
        }
    }

    final String getCVC() {
        return this.getText().toString().trim();
    }
}
