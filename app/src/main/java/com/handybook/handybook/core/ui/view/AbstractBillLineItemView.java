package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handybook.handybook.core.model.bill.Bill;

import butterknife.ButterKnife;

public abstract class AbstractBillLineItemView extends LinearLayout {

    private Bill.BillLineItem mBillLineItem;
    private String mCurrencySymbol = "";

    public AbstractBillLineItemView(final Context context) {
        super(context);
        init(null, 0, 0);
    }

    public AbstractBillLineItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public AbstractBillLineItemView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractBillLineItemView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    protected void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        setSaveEnabled(true);
        inflate(getContext(), getLayout(), this);
        ButterKnife.bind(this, getRootView());
        update();
    }

    public void setData(
            @NonNull final Bill.BillLineItem billLineItem,
            @NonNull final String currencySymbol
    ) {

        if (billLineItem.equals(mBillLineItem)) {
            return;
        }
        mBillLineItem = billLineItem;
        mCurrencySymbol = currencySymbol;
        update();
    }

    public Bill.BillLineItem getBillLineItem() {
        return mBillLineItem;
    }

    public void setCurrencySymbol(@NonNull final String currencySymbol) {
        if (currencySymbol.equals(mCurrencySymbol)) {
            return;
        }
        mCurrencySymbol = currencySymbol;
        update();
    }

    @NonNull
    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    protected abstract int getLayout();

    protected abstract void update();

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.setBillLineItem(getBillLineItem());
        savedState.setCurrencySymbol(getCurrencySymbol());
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setData(savedState.getBillLineItem(), savedState.getCurrencySymbol());
    }

    private static class SavedState extends BaseSavedState {

        private Bill.BillLineItem mBillLineItem;
        private String mCurrencySymbol;

        SavedState(final Parcelable superState) {
            super(superState);
        }

        SavedState(final Parcel source) {
            super(source);
            mBillLineItem = (Bill.BillLineItem) source.readSerializable();
            mCurrencySymbol = source.readString();
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeSerializable(mBillLineItem);
            out.writeString(mCurrencySymbol);
        }

        void setBillLineItem(@NonNull final Bill.BillLineItem billLineItem) {
            mBillLineItem = billLineItem;
        }

        @NonNull
        Bill.BillLineItem getBillLineItem() {
            return mBillLineItem;
        }

        void setCurrencySymbol(@NonNull final String currencySymbol) {
            mCurrencySymbol = currencySymbol;
        }

        @NonNull
        String getCurrencySymbol() {
            return mCurrencySymbol;
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(final Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        };
    }


    public static class Factory {

        public static AbstractBillLineItemView from(
                @NonNull final Context ctx,
                @NonNull final Bill.BillLineItem billLineItem,
                @NonNull String currencySymbol
        ) {

            AbstractBillLineItemView lineItemView;
            switch (billLineItem.getType()) {
                case LARGE_PRICE:
                    lineItemView = new LargeBillLineItemView(ctx);
                    break;
                default:
                    lineItemView = new DefaultBillLineItemView(ctx);
            }
            lineItemView.setData(billLineItem, currencySymbol);
            return lineItemView;
        }
    }
}
