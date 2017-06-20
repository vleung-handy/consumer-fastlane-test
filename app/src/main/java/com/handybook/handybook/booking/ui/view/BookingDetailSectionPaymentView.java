package com.handybook.handybook.booking.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.library.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailSectionPaymentView extends BookingDetailSectionView {

    @BindView(R.id.pay_lines_section)
    public LinearLayout paymentLinesSection;
    @BindView(R.id.total_text)
    public TextView totalText;
    @BindView(R.id.total_price_sub_text)
    TextView mTotalPriceSubText;

    public BookingDetailSectionPaymentView(final Context context) {
        super(context);
    }

    public BookingDetailSectionPaymentView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingDetailSectionPaymentView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_booking_detail_section_payment, this);
        ButterKnife.bind(this);
    }

    //TODO: Clean this up
    public void updatePaymentDisplay(
            final Booking booking,
            final User user,
            @Nullable final String totalPriceSubText
    ) {
        totalText.setText(booking.formatPrice(user.getCurrencyChar()));
        mTotalPriceSubText.setVisibility(TextUtils.isEmpty(totalPriceSubText)
                                              ? GONE
                                              : VISIBLE);
        mTotalPriceSubText.setText(totalPriceSubText);

        final ArrayList<Booking.LineItem> paymentInfo = booking.getPaymentInfo();

        //Sort the payment info
        if (paymentInfo != null && paymentInfo.size() > 0) {
            Collections.sort(paymentInfo, new Comparator<Booking.LineItem>() {
                @Override
                public int compare(final Booking.LineItem lhs, final Booking.LineItem rhs) {
                    return lhs.getOrder() - rhs.getOrder();
                }
            });
        }

        //Display the payment info
        if (paymentInfo != null && paymentInfo.size() > 0) {
            paymentLinesSection.setVisibility(View.VISIBLE);

            View paymentLineView;

            for (int i = 0; i < paymentInfo.size(); i++) {
                paymentLineView = inflate(getContext(), R.layout.view_payment_line, null);
                paymentLinesSection.addView(paymentLineView);

                final TextView labelText = (TextView) paymentLineView.findViewById(
                        R.id.view_payment_line_label_text
                );
                final TextView amountText = (TextView) paymentLineView.findViewById(
                        R.id.view_payment_line_amount_text
                );
                final ImageView questionMark = (ImageView) paymentLineView.findViewById(
                        R.id.view_payment_line_question_mark
                );
                final Booking.LineItem line = paymentInfo.get(i);

                labelText.setText(line.getLabel());
                amountText.setText(line.getAmount());
                if (line.hasHelpText()) {
                    questionMark.setVisibility(VISIBLE);
                    paymentLineView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final FragmentManager fm = ((AppCompatActivity) getContext())
                                    .getSupportFragmentManager();
                            if (fm.findFragmentByTag(PriceLineHelpTextDialog.TAG) == null) {
                                PriceLineHelpTextDialog
                                        .newInstance(line.getHelpText())
                                        .show(fm, PriceLineHelpTextDialog.TAG);
                            }
                        }
                    });

                }
                else {
                    questionMark.setVisibility(GONE);
                }

                //Adjust padding for the payment line view for any that are not the last
                if (i < paymentInfo.size() - 1) {
                    paymentLineView.setPadding(0, 0, 0, Utils.toDP(10, getContext()));
                }
            }
        }

        final String billedStatus = booking.getBilledStatus();
        if (billedStatus != null) {
            getEntryText().setText(billedStatus);
        }
        else {
            getEntryText().setVisibility(View.GONE);
        }
    }

    public static class PriceLineHelpTextDialog extends DialogFragment {

        public static final String KEY_MESSAGE = "PLHTD:Message";
        public static final String TAG = "PriceLineHelpTextDialog";

        public static PriceLineHelpTextDialog newInstance(final String text) {
            PriceLineHelpTextDialog dialogFragment = new PriceLineHelpTextDialog();
            Bundle args = new Bundle();
            args.putString(KEY_MESSAGE, text);
            dialogFragment.setArguments(args);
            return dialogFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String message = "";
            if (getArguments() == null) {
                dismiss();
                throw new RuntimeException("Use newInstance() to initialize this dialog!");
            }
            else {
                message = getArguments().getString(KEY_MESSAGE);
            }
            builder.setMessage(message)
                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
            return builder.create();
        }
    }
}
