package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
import com.handybook.handybook.core.model.bill.Bill;

import butterknife.Bind;

public class LargeBillLineItemView extends AbstractBillLineItemView
{

    private Bill.BillLineItem mBillLineItem;

    @Bind(R.id.bill_view_line_item_label)
    TextView mLabelText;
    @Bind(R.id.bill_view_line_item_question_mark)
    ImageView mQuestionMarkImage;
    @Bind(R.id.bill_view_line_item_amount)
    TextView mAmountText;

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
        if (mBillLineItem == null)
        {
            getRootView().setVisibility(GONE);
        }
        else
        {
            getRootView().setVisibility(VISIBLE);
            updateLabel();
            updatePrice();
            updateHelpText();
        }
    }

    private void updateLabel()
    {
        mLabelText.setText(mBillLineItem.getLabel());
    }

    private void updatePrice()
    {
        mAmountText.setText(String.valueOf(mBillLineItem.getAmountCents()));
    }

    private void updateHelpText()
    {
        if (mBillLineItem.hasHelpText())
        {
            mQuestionMarkImage.setVisibility(VISIBLE);
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
                                .newInstance(mBillLineItem.getHelpText())
                                .show(
                                        fm,
                                        BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG
                                );
                    }
                }
            });

        }
        else
        {
            mQuestionMarkImage.setVisibility(GONE);
            getRootView().setOnClickListener(null);
        }

    }

}
