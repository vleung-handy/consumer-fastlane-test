package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.library.ui.view.InputTextField;
import com.handybook.handybook.library.util.CreditCardUtils;
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

    public CreditCardExpDateInputTextView(
            final Context context, final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * formats the given month and year values and sets it as the view's text
     * @param expMonth expected range: 1-12
     * @param expYear the full year (ex. expects 2016, not 16)
     */
    public void setTextFromMonthYear(int expMonth, int expYear) {
        //the text for the month is expected to be in XX format
        String expMonthString = (expMonth >= 10) ? String.valueOf(expMonth) : ("0" + expMonth);

        //the text for the year is expected to be in XX format
        String expYearString = String.valueOf(expYear);

        int lastNDigits = 2; //must be > 0
        if (expYearString.length() > lastNDigits) {
            expYearString = expYearString.substring(expYearString.length() - lastNDigits);
        }

        //text is expected to be in MM/YY format
        setText(expMonthString + "/" + expYearString);
    }

    private void init() {
        InputFilter[] filterArray = new InputFilter[]{new InputFilter.LengthFilter(5)};
        setFilters(filterArray);

        addTextChangedListener(new HandyTextWatcher() {

            @Override
            public void afterTextChanged(final Editable editable) {
                super.afterTextChanged(editable);

                if (isDeletingFromEnd()) { return; }

                final String date = editable.toString();
                final String formattedDate = CreditCardUtils.formatCreditCardExpDate(date);

                if (!date.equals(formattedDate)) {
                    changeText(CreditCardExpDateInputTextView.this, formattedDate);
                }
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

    private int[] getExpDate() {
        final String expDate = this.getText().toString().trim();
        int expMonth = -1;
        int expYear = -1;

        try {
            final String[] date = expDate.split("/");
            if (date.length == 2) {
                expMonth = Integer.parseInt(date[0]);
                expYear = Integer.parseInt(date[1]);
            }
        }
        catch (NumberFormatException e) {
            Crashlytics.logException(e);
        }

        return new int[]{expMonth, expYear};
    }
}
