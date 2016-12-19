package com.handybook.handybook.core.ui.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.library.ui.view.FreezableInputTextView;
import com.handybook.handybook.library.util.TextUtils;
import com.stripe.android.model.Card;

public final class CreditCardNumberInputTextView extends FreezableInputTextView
{
    private CreditCard.Type mCardType;

    public CreditCardNumberInputTextView(final Context context)
    {
        super(context);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    public CreditCardNumberInputTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    public CreditCardNumberInputTextView(final Context context, final AttributeSet attrs,
                                         final int defStyle)
    {
        super(context, attrs, defStyle);
        addTextChangedListener(new CreditCardInputTextWatcher());
    }

    private class CreditCardInputTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                                      final int count, final int after)
        {
        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                                  final int before, final int count)
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            if (!CreditCardNumberInputTextView.this.isEnabled())
            {
                return;
            }

            final String cardNumber = CreditCardNumberInputTextView.this.getText().toString();
            final Card dummyCard = new Card("", 0, 0, "");
            dummyCard.setNumber(cardNumber);

            if (dummyCard.getType() == null) { mCardType = CreditCard.Type.OTHER; }
            else
            {
                switch (dummyCard.getType())
                {
                    case Card.AMERICAN_EXPRESS:
                        mCardType = CreditCard.Type.AMEX;
                        break;

                    case Card.DISCOVER:
                        mCardType = CreditCard.Type.DISCOVER;
                        break;

                    case Card.MASTERCARD:
                        mCardType = CreditCard.Type.MASTERCARD;
                        break;

                    case Card.VISA:
                        mCardType = CreditCard.Type.VISA;
                        break;

                    default:
                        mCardType = CreditCard.Type.OTHER;
                }
            }

            new AsyncTask<String, Void, String>()
            {
                @Override
                protected String doInBackground(final String... params)
                {
                    final String cardNumber = params[0];
                    return TextUtils.formatCreditCardNumber(mCardType, cardNumber);
                }

                @Override
                protected void onPostExecute(final String formattedText)
                {
                    CreditCardNumberInputTextView.this.removeTextChangedListener(
                            CreditCardInputTextWatcher.this);
                    CreditCardNumberInputTextView.this.setText(formattedText);
                    CreditCardNumberInputTextView.this.setSelection(formattedText.length());
                    CreditCardNumberInputTextView.this
                            .addTextChangedListener(CreditCardInputTextWatcher.this);
                }
            }.execute(editable.toString());

        }
    }

    public final boolean validate()
    {
        final Card card = new Card(getCardNumber(), -1, -1, "");

        if (!card.validateNumber())
        {
            highlight();
            return false;
        }
        else
        {
            unHighlight();
            return true;
        }
    }

    public final String getCardNumber()
    {
        return getText().toString().replaceAll("\\D+", "");
    }

    public final CreditCard.Type getCardType()
    {
        return mCardType;
    }
}
