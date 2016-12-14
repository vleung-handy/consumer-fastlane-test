package com.handybook.handybook.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PriceView extends FrameLayout
{


    @Bind(R.id.price_view_cardinal_tv)
    TextView mCardinal;
    @Bind(R.id.price_view_decimal_tv)
    TextView mDecimal;

    private String mCurrencySymbol;
    private String mCardinalText;
    private String mDecimalText;
    private boolean shouldDisplayEmptyDecimals; // If false, zero cents will be hidden

    public PriceView(Context context)
    {
        super(context);
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        inflate(getContext(), R.layout.layout_price_view, this);
        ButterKnife.bind(this);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PriceView, 0, 0);
        try
        {
            shouldDisplayEmptyDecimals = ta
                    .getBoolean(R.styleable.PriceView_priceShowZeroCents, false);
            setCurrencySymbol(ta.getString(R.styleable.PriceView_priceCurrencySymbol));
            setCardinal(ta.getInt(R.styleable.PriceView_priceCardinal, 0));
            setDecimal(ta.getInt(R.styleable.PriceView_priceDecimal, 0));
        }
        finally
        {
            ta.recycle();
        }
        updateUi();
    }

    private void updateUi()
    {
        if (mCurrencySymbol == null)
        {
            mCurrencySymbol = "";
        }
        else
        {
            mDecimal.setVisibility(VISIBLE);
        }
        mCardinal.setText(mCardinalText);
        mDecimal.setText(mDecimalText);
        if (TextUtils.isEmpty(mDecimalText))
        {
            mDecimal.setVisibility(GONE);
        }
        else
        {
            mDecimal.setVisibility(VISIBLE);
        }
    }


    public PriceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public PriceView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    public void setCurrencySymbol(final String currencySymbol)
    {
        mCurrencySymbol = currencySymbol;
        updateUi();
    }

    public void setCardinal(final int cardinalValue)
    {
        mCardinalText = String.format(Locale.getDefault(), "%s%d", mCurrencySymbol, cardinalValue);
        updateUi();
    }

    public void setDecimal(final int decimalValue)
    {
        if (decimalValue == 0 && !shouldDisplayEmptyDecimals)
        {
            setDecimal("");
        }
        else
        {
            setDecimal(String.format(Locale.getDefault(), "%02d", decimalValue));
        }
        updateUi();
    }

    public String getCardinalText()
    {
        return mCardinalText;
    }

    public void setCardinal(final String cardinalText)
    {
        mCardinalText = cardinalText;
        updateUi();
    }

    public String getDecimalText()
    {
        return mDecimalText;
    }

    public void setDecimal(final String decimalText)
    {
        mDecimalText = decimalText;
        updateUi();
    }

    public void setPrice(final float price)
    {
        setCardinal((int) price);
        setDecimal((int) ((price - (int) price) / 100));
    }
}
