package com.handybook.handybook.util;

import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.common.collect.Lists;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;

import java.util.List;

public class PaymentUtils
{
    public static MaskedWalletRequest createMaskedWalletRequest(BookingQuote quote, BookingTransaction transaction,
                                                                PaymentMethodTokenizationParameters parameters)
    {
        final float hours = transaction.getHours() + transaction.getExtraHours();
        final float[] pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
        String itemPrice = Float.toString(pricing[0] == pricing[1] ? pricing[0] : pricing[1]);

        List<LineItem> lineItems = Lists.newArrayList(LineItem.newBuilder()
                .setCurrencyCode("USD")
                .setQuantity("1")
                .setUnitPrice(itemPrice)
                .setTotalPrice(itemPrice)
                .build());

        MaskedWalletRequest.Builder builder = MaskedWalletRequest.newBuilder()
                .setMerchantName("Handy Technologies Inc.")
                .setPhoneNumberRequired(false)
                .setShippingAddressRequired(false)
                .setCurrencyCode("USD")
                .setEstimatedTotalPrice(itemPrice)
                        // Create a Cart with the current line items. Provide all the information
                        // available up to this point with estimates for shipping and tax included.
                .setCart(Cart.newBuilder()
                        .setCurrencyCode("USD")
                        .setTotalPrice(itemPrice)
                        .setLineItems(lineItems)
                        .build());

        if (parameters != null)
        {
            builder.setPaymentMethodTokenizationParameters(parameters);
        }

        return builder.build();
    }
}
