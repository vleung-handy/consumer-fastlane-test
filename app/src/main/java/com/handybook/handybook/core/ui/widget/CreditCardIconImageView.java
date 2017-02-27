package com.handybook.handybook.core.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.CreditCard;
import com.stripe.android.model.Card;

public class CreditCardIconImageView extends ImageView {

    public CreditCardIconImageView(final Context context) {
        super(context);
    }

    public CreditCardIconImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CreditCardIconImageView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CreditCardIconImageView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCardIcon(final String type) {
        if (type == null) {
            this.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type) {
            case Card.AMERICAN_EXPRESS:
                this.setImageResource(R.drawable.ic_card_amex);
                break;

            case Card.DISCOVER:
                this.setImageResource(R.drawable.ic_card_discover);
                break;

            case Card.MASTERCARD:
                this.setImageResource(R.drawable.ic_card_mc);
                break;

            case Card.VISA:
                this.setImageResource(R.drawable.ic_card_visa);
                break;

            default:
                this.setImageResource(R.drawable.ic_card_blank);
        }
    }

    public void setCardIcon(final CreditCard.Type type) {
        if (type == null) {
            this.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type) {
            case AMEX:
                this.setImageResource(R.drawable.ic_card_amex);
                break;

            case DISCOVER:
                this.setImageResource(R.drawable.ic_card_discover);
                break;

            case MASTERCARD:
                this.setImageResource(R.drawable.ic_card_mc);
                break;

            case VISA:
                this.setImageResource(R.drawable.ic_card_visa);
                break;

            case ANDROID_PAY:
                this.setImageResource(R.drawable.ic_android_pay);
                break;

            default:
                this.setImageResource(R.drawable.ic_card_blank);
        }
    }
}
