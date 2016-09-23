package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.handybook.handybook.library.ui.view.InputTextField;
import com.stripe.android.model.Card;

public final class CreditCardCVCInputTextView extends InputTextField
{

    private static final int MAX_INPUT_LENGTH = 4;

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

    protected void init() {
        super.init();
        InputFilter[] filterArray = new InputFilter[]{ new InputFilter.LengthFilter(MAX_INPUT_LENGTH)};
        this.setFilters(filterArray);
    }

    public final boolean validate() {
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

    public final String getCVC() {
        return this.getText().toString().trim();
    }
}
