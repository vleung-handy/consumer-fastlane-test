package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;


/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionPaymentView extends BookingDetailSectionView
{
    @Bind(R.id.pay_lines_section)
    public LinearLayout paymentLinesSection;

    @Bind(R.id.total_text)
    public TextView totalText;

    public BookingDetailSectionPaymentView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionPaymentView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionPaymentView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    //TODO: Clean this up
    public void updatePaymentDisplay(final Booking booking, final User user)
    {
        final String price = TextUtils.formatPrice(booking.getPrice(),
                user.getCurrencyChar(), null);
        totalText.setText(price);

        final ArrayList<Booking.LineItem> paymentInfo = booking.getPaymentInfo();

        //Sort the payment info
        if (paymentInfo != null && paymentInfo.size() > 0)
        {
            Collections.sort(paymentInfo, new Comparator<Booking.LineItem>()
            {
                @Override
                public int compare(final Booking.LineItem lhs, final Booking.LineItem rhs)
                {
                    return lhs.getOrder() - rhs.getOrder();
                }
            });
        }

        //Display the payment info
        if (paymentInfo != null && paymentInfo.size() > 0)
        {
            paymentLinesSection.setVisibility(View.VISIBLE);

            View paymentLineView;

            for (int i = 0; i < paymentInfo.size(); i++)
            {
                paymentLineView = inflate(R.layout.view_payment_line, paymentLinesSection);

                final TextView labelText = (TextView) paymentLineView.findViewById(R.id.label_text);
                final TextView amountText = (TextView) paymentLineView.findViewById(R.id.amount_text);
                final Booking.LineItem line = paymentInfo.get(i);

                labelText.setText(line.getLabel());
                amountText.setText(line.getAmount());

                //Adjust padding for the payment line view for any that are not the last
                if (i < paymentInfo.size() - 1)
                {
                    paymentLineView.setPadding(0, 0, 0, Utils.toDP(10, getContext()));
                }
            }
        }

        final String billedStatus = booking.getBilledStatus();
        if (billedStatus != null)
        {
            getEntryText().setText(billedStatus);
        }
        else
        {
            getEntryText().setVisibility(View.GONE);
        }
    }

}
