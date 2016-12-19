package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
import com.handybook.handybook.core.model.bill.Bill;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  DO NOT USE IT IS NOT FINSISHED VIEW
 *  TODO: FINISH IT
 */
public class BillLineItemView extends FrameLayout
{

    private Bill.BillLineItem mBillLineItem;

    @Bind(R.id.bill_view_line_item_label)
    TextView mLabelText;
    @Bind(R.id.bill_view_line_item_question_mark)
    ImageView mQuestionMarkImage;
    @Bind(R.id.bill_view_line_item_amount)
    TextView mAmountText;

    public BillLineItemView(final Context context)
    {
        super(context);
    }

    public BillLineItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BillLineItemView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BillLineItemView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        inflate(getContext(), R.layout.layout_bill_view_section, this);
        ButterKnife.bind(this);
        update();
    }

    public void setBillLineItem(final Bill.BillLineItem billLineItem)
    {
        mBillLineItem = billLineItem;
        update();
    }

    private void update()
    {
        if (mBillLineItem == null)
        {
            getRootView().setVisibility(GONE);
            return;
        }
        getRootView().setVisibility(VISIBLE);
        mLabelText.setText(mBillLineItem.getLabel());
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
                            BookingDetailSectionPaymentView.PriceLineHelpTextDialog.TAG) == null)
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
        //TODO: Think about passing currency and rendering prices...
        mAmountText.setText(String.valueOf(mBillLineItem.getAmountCents()));
    }
}
