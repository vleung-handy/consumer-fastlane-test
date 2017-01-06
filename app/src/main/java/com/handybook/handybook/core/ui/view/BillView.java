package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
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
        setSaveEnabled(true);
        inflate(getContext(), R.layout.layout_bill_view, this);
        ButterKnife.bind(this);
        collapse();
        if (isInEditMode())
        {
            setBill(Bill.fromJson(Bill.EXAMPLE_JSON));
        }
    }

    public void setBill(@NonNull final Bill bill)
    {
        mBill = bill;
        update();
    }

    private void update()
    {
        getRootView().setVisibility(VISIBLE);
        mHeaderTitle.setText(mBill.getHeaderTitle());
        mHeaderText.setText(mBill.getHeaderText());
        mHeaderPrice.setCurrencySymbol(mBill.getCurrencySymbol());
        mHeaderPrice.setPrice(mBill.getFinalPriceValueCents());
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
        mExpandTargetView.setVisibility(GONE);
        mIsExpanded = true;
    }

    public void collapse()
    {
        mSectionContainer.setVisibility(GONE);
        mExpandTargetView.setVisibility(VISIBLE);
        mIsExpanded = false;
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.setBill(mBill);
        return savedState;


    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state)
    {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mBill = savedState.getBill();

    }

    private static class SavedState extends BaseSavedState
    {

        private Bill mBill;

        SavedState(final Parcelable superState)
        {
            super(superState);
        }

        SavedState(final Parcel source)
        {
            super(source);
            mBill = (Bill) source.readSerializable();
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags)
        {
            super.writeToParcel(out, flags);
            out.writeSerializable(mBill);
        }

        void setBill(@NonNull final Bill bill)
        {
            mBill = bill;
        }

        @NonNull
        Bill getBill()
        {
            return mBill;
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(final Parcel source)
            {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(final int size)
            {
                return new SavedState[size];
            }
        };
    }
}
