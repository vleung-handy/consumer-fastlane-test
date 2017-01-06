package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.model.bill.Bill;

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
        setSaveEnabled(true);
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
            DefaultBillLineItemView billLineItemView = new DefaultBillLineItemView(getContext());
            billLineItemView.setBillLineItem(eBillLineItem);
            mLineItemContainer.addView(billLineItemView);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.setBillSection(mBillSection);
        return savedState;


    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state)
    {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mBillSection = savedState.getBillSection();

    }

    private static class SavedState extends BaseSavedState
    {

        private Bill.BillSection mBillSection;

        SavedState(final Parcelable superState)
        {
            super(superState);
        }

        SavedState(final Parcel source)
        {
            super(source);
            mBillSection = (Bill.BillSection) source.readSerializable();
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags)
        {
            super.writeToParcel(out, flags);
            out.writeSerializable(mBillSection);
        }

        void setBillSection(@NonNull final Bill.BillSection billSection)
        {
            mBillSection = billSection;
        }

        @NonNull
        Bill.BillSection getBillSection()
        {
            return mBillSection;
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
