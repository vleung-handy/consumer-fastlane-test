package com.handybook.handybook.util;

import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.common.collect.Lists;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.stripe.Stripe;

import java.util.List;

public class PaymentUtils
{
    public static MaskedWalletRequest createMaskedWalletRequest(BookingQuote quote, BookingTransaction transaction)
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

        return MaskedWalletRequest.newBuilder()
                .setMerchantName("Handy Technologies Inc.")
                .setPhoneNumberRequired(false)
                .setShippingAddressRequired(false)
                .setCurrencyCode("USD")
                .setEstimatedTotalPrice(itemPrice)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode("USD")
                        .setTotalPrice(itemPrice)
                        .setLineItems(lineItems)
                        .build())
                .setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                        .addParameter("gateway", "stripe")
                        .addParameter("stripe:publishableKey", "pk_qPX5iTm3zI9AebN3rxOtFUe1Z4l92") // should be quote.getStripeKey()
                        .addParameter("stripe:version", Stripe.VERSION)
                        .build())
                .build();
    }

    public static FullWalletRequest createFullWalletRequest(BookingQuote quote, BookingTransaction transaction, MaskedWallet maskedWallet)
    {
        final float hours = transaction.getHours() + transaction.getExtraHours();
        final float[] pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
        String itemPrice = Float.toString(pricing[0] == pricing[1] ? pricing[0] : pricing[1]);

        return FullWalletRequest.newBuilder()
                .setGoogleTransactionId(maskedWallet.getGoogleTransactionId())
                .setCart(Cart.newBuilder()
                        .setCurrencyCode("USD")
                        .setTotalPrice(itemPrice)
                        .build())
                .build();
    }
}
