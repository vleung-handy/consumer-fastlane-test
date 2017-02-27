package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.util.TextUtils;

import butterknife.Bind;

/***
 * Standard issue line item in BillSectionView, small title small value
 */
public class DefaultBillLineItemView extends AbstractBillLineItemView {

    private static final String TAG = "DefaultBillLineItemView";

    @Bind(R.id.bill_view_default_line_item_label)
    TextView mLabel;
    @Bind(R.id.bill_view_default_line_item_question_mark)
    ImageView mQuestionMarkImage;
    @Bind(R.id.bill_view_default_line_item_amount)
    TextView mAmount;

    public DefaultBillLineItemView(final Context context) {
        super(context);
    }

    protected int getLayout() {
        return R.layout.layout_bill_view_line_item_default;
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
        mLabel.setId(hashCode());
        mAmount.setId(hashCode());
    }

    private void updateLabel() {
        mLabel.setText(getBillLineItem().getLabel());
    }

    private void updatePrice() {
        assert getBillLineItem() != null;
        final Integer amountCents = getBillLineItem().getAmountCents();
        final String amountText;
        if (getBillLineItem().hasAmountText() || amountCents == null) {
            amountText = getBillLineItem().getAmountText();
        }
        else {
            amountText = TextUtils.formatPriceCents(amountCents, getCurrencySymbol());
        }

        mAmount.setText(amountText);
    }

    private void updateHelpText() {
        if (getBillLineItem().hasHelpText()) {
            mQuestionMarkImage.setVisibility(VISIBLE);
            getRootView().setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final BaseActivity baseActivity
                                    = BaseActivity.getInstance(getContext());
                            if (baseActivity == null) {
                                return;
                            }
                            final FragmentManager fm = baseActivity.getSupportFragmentManager();
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
                    }

            );

        }
        else {
            mQuestionMarkImage.setVisibility(GONE);
            getRootView().setOnClickListener(null);
        }

    }
}
