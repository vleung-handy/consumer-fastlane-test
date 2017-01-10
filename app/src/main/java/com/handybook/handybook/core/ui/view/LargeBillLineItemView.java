package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;

import butterknife.Bind;

public class LargeBillLineItemView extends AbstractBillLineItemView
{
    private static final String TAG = "LargeBillLineItemView";


    @Bind(R.id.bill_view_line_item_label)
    TextView mLabel;
    @Bind(R.id.bill_view_line_item_question_mark)
    ImageView mQuestionMark;
    @Bind(R.id.bill_view_line_item_amount)
    PriceView mPrice;
    @Bind(R.id.bill_view_line_item_amount_override)
    TextView mPriceOverride;

    public LargeBillLineItemView(final Context context)
    {
        super(context);
    }

    protected int getLayout()
    {
        return R.layout.layout_bill_view_line_item_large;
    }

    protected void update()
    {
        if (getBillLineItem() == null)
        {
            getRootView().setVisibility(GONE);
            return;
        }
        getRootView().setVisibility(VISIBLE);
        updateLabel();
        updatePrice();
        updateHelpText();
    }

    private void updateLabel()
    {
        mLabel.setText(getBillLineItem().getLabel());
    }

    private void updatePrice()
    {
        if (getBillLineItem().hasAmountText() || getBillLineItem().getAmountCents() == null)
        {
            mPrice.setVisibility(GONE);
            mPriceOverride.setVisibility(VISIBLE);
            mPriceOverride.setText(getBillLineItem().getAmountText());
        } else
        {
            mPrice.setVisibility(VISIBLE);
            mPriceOverride.setVisibility(GONE);
            mPrice.setPrice(getBillLineItem().getAmountCents());

        }
    }

    private void updateHelpText()
    {
        if (getBillLineItem().hasHelpText())
        {
            mQuestionMark.setVisibility(VISIBLE);
            getRootView().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    final FragmentManager fm = ((AppCompatActivity) getContext())
                            .getSupportFragmentManager();
                    if (fm.findFragmentByTag(
                            BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG
                    ) == null)
                    {
                        BookingDetailSectionPaymentView.PriceLineHelpTextDialog
                                .newInstance(getBillLineItem().getHelpText())
                                .show(
                                        fm,
                                        BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG
                                );
                    }
                }
            });

        } else
        {
            mQuestionMark.setVisibility(GONE);
            getRootView().setOnClickListener(null);
        }

    }

}
