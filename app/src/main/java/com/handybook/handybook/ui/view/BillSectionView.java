package com.handybook.handybook.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.model.bill.Bill;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BillSectionView extends FrameLayout
{

    private Bill.BillSection mBillSection;

    @Bind(R.id.bill_view_section_line_item_container)
    LinearLayout mLineItemContainer;
    @Bind(R.id.bill_view_section_horizontal_separator)
    View mSeparator;

    public BillSectionView(final Context context)
    {
        super(context);
        init(null, 0, 0);
    }

    public BillSectionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public BillSectionView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BillSectionView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        inflate(getContext(), R.layout.layout_bill_view_section, this);
        ButterKnife.bind(this);
        update();
    }

    public void setBillSection(final Bill.BillSection billSection)
    {
        mBillSection = billSection;
        update();
    }

    private void update()
    {
        if (mBillSection == null)
        {
            getRootView().setVisibility(GONE);
            return;
        }
        getRootView().setVisibility(VISIBLE);
        mLineItemContainer.removeAllViews();
        for (Bill.BillLineItem eBillLineItem : mBillSection.getLineItems())
        {
            BillLineItemView billLineItemView = new BillLineItemView(getContext());
            billLineItemView.setBillLineItem(eBillLineItem);
            mLineItemContainer.addView(billLineItemView);
        }
    }

}
