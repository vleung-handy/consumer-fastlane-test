package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;

import butterknife.BindView;

/**
 * Large line item usees bigger fonts and PriceView widget to show price
 */
public class LargeBillLineItemView extends AbstractBillLineItemView {

    private static final String TAG = "LargeBillLineItemView";

    @BindView(R.id.bill_view_large_line_item_label)
    TextView mLabel;
    @BindView(R.id.bill_view_large_line_item_question_mark)
    ImageView mQuestionMark;
    @BindView(R.id.bill_view_large_line_item_amount)
    PriceView mPrice;
    @BindView(R.id.bill_view_large_line_item_amount_override)
    TextView mPriceOverride;
    @BindView(R.id.bill_view_large_line_item_amount_override_sub_text)
    TextView mAmountOverrideSubText;

    public LargeBillLineItemView(final Context context) {
        super(context);
    }

    protected int getLayout() {
        return R.layout.layout_bill_view_line_item_large;
    }

    protected void update() {
        if (getBillLineItem() == null) {
            getRootView().setVisibility(GONE);
            return;
        }
        getRootView().setVisibility(VISIBLE);
        updateLabel();
        updatePrice();
        updateHelpText();
        updateAmountSubtext();
        mLabel.setId(hashCode());
        mPrice.setId(hashCode());
        mPriceOverride.setId(hashCode());
    }

    public void updateAmountSubtext() {
        mPrice.setSubText(getBillLineItem().getAmountSubText());
        mAmountOverrideSubText.setText(getBillLineItem().getAmountSubText());
    }

    private void updateLabel() {
        mLabel.setText(getBillLineItem().getLabel());
    }

    private void updatePrice() {
        if (getBillLineItem().hasAmountText() || getBillLineItem().getAmountCents() == null) {
            mPrice.setVisibility(GONE);
            mPriceOverride.setVisibility(VISIBLE);
            mPriceOverride.setText(getBillLineItem().getAmountText());
        }
        else {
            mPrice.setVisibility(VISIBLE);
            mPriceOverride.setVisibility(GONE);
            mPrice.setCurrencySymbol(getCurrencySymbol());
            mPrice.setPriceCents(getBillLineItem().getAmountCents());

        }
    }

    private void updateHelpText() {
        if (getBillLineItem().hasHelpText()) {
            mQuestionMark.setVisibility(VISIBLE);
            getRootView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final FragmentManager fm = ((AppCompatActivity) getContext())
                            .getSupportFragmentManager();
                    if (fm.findFragmentByTag(
                            BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG
                    ) == null) {
                        BookingDetailSectionPaymentView.PriceLineHelpTextDialog
                                .newInstance(getBillLineItem().getHelpText())
                                .show(
                                        fm,
                                        BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG
                                );
                    }
                }
            });

        }
        else {
            mQuestionMark.setVisibility(GONE);
            getRootView().setOnClickListener(null);
        }

    }

}
