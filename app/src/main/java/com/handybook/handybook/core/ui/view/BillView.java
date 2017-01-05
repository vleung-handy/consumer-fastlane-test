package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.model.bill.Bill;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * UNFINISHED DO NOT USE
 * TODO: FINISH THIS
 */
public class BillView extends FrameLayout
{

    private boolean mIsExpanded;
    private Bill mBill;

    @Bind(R.id.bill_view_header_title)
    TextView mHeaderTitle;
    @Bind(R.id.bill_view_header_text)
    TextView mHeaderText;
    @Bind(R.id.bill_view_header_price)
    PriceView mHeaderPrice;
    @Bind(R.id.bill_view_section_container)
    LinearLayout mSectionContainer;
    @Bind(R.id.bill_view_final_line_container)
    ViewGroup mFinalLineContainer;
    @Bind(R.id.bill_view_final_line_label)
    TextView mFinalLineLabel;
    @Bind(R.id.bill_view_final_line_price)
    PriceView mFinalLinePrice;
    @Bind(R.id.bill_view_expand_target)
    TextView mExpandTargetView;

    public BillView(final Context context)
    {
        super(context);
        init(null, 0);
    }

    public BillView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public BillView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyle)
    {
        inflate(getContext(), R.layout.layout_bill_view, this);
        ButterKnife.bind(this);
        collapse();
    }

    public void setBill(final Bill bill)
    {
        mBill = bill;
        update();
    }

    private void update()
    {
        if (mBill == null)
        {
            getRootView().setVisibility(GONE);
            return;
        }
        getRootView().setVisibility(VISIBLE);
        mHeaderTitle.setText(mBill.getHeaderTitle());
        mHeaderText.setText(mBill.getHeaderText());
        mHeaderPrice.setCurrencySymbol(mBill.getCurrencySymbol());
        mHeaderPrice.setPrice(mBill.getFinalPriceValueCents().getAmountCents());
        mFinalLineLabel.setText(mBill.getFinalPriceValueCents().getLabel());
        mFinalLinePrice.setPrice(mBill.getFinalPriceValueCents().getAmountCents());
        mSectionContainer.removeAllViews();
        for (Bill.BillSection eBillSection : mBill.getSections())
        {
            BillSectionView billSectionView = new BillSectionView(getContext());
            billSectionView.setBillSection(eBillSection);
            mSectionContainer.addView(billSectionView);
        }

    }

    public boolean isExpanded()
    {
        return mIsExpanded;
    }

    @OnClick(R.id.bill_view_root)
    void toggleExpand()
    {
        if (mIsExpanded)
        {
            collapse();
        }
        else
        {
            expand();
        }
    }

    public void expand()
    {
        mSectionContainer.setVisibility(VISIBLE);
        mFinalLineContainer.setVisibility(VISIBLE);
        mExpandTargetView.setVisibility(GONE);
        mIsExpanded = true;
    }

    public void collapse()
    {
        mSectionContainer.setVisibility(GONE);
        mFinalLineContainer.setVisibility(GONE);
        mExpandTargetView.setVisibility(VISIBLE);
        mIsExpanded = false;
    }
}
