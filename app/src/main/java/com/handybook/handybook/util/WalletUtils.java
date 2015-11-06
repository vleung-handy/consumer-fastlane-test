package com.handybook.handybook.util;

import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.WalletConstants;
import com.google.common.collect.Lists;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.stripe.Stripe;

import java.util.List;

public class WalletUtils
{
    public static final int REQUEST_CODE_LOAD_MASKED_WALLET = 1001;
    public static final int REQUEST_CODE_LOAD_FULL_WALLET = 1002;
    private static final String CURRENCY_CODE_USD = "USD";
    private static final String HANDY_MERCHANT_NAME = "Handy Technologies Inc.";

    public static int getEnvironment()
    {
        if (BuildConfig.FLAVOR.equalsIgnoreCase(BaseApplication.FLAVOR_PROD))
        {
            return WalletConstants.ENVIRONMENT_PRODUCTION;
        }
        else
        {
            return WalletConstants.ENVIRONMENT_TEST;
        }
    }

    public static MaskedWalletRequest createMaskedWalletRequest(BookingQuote quote, BookingTransaction transaction)
    {
        String itemPrice = getItemPrice(quote, transaction);

        List<LineItem> lineItems = Lists.newArrayList(LineItem.newBuilder()
                .setCurrencyCode(CURRENCY_CODE_USD)
                .setQuantity("1")
                .setUnitPrice(itemPrice)
                .setTotalPrice(itemPrice)
                .build());

        return MaskedWalletRequest.newBuilder()
                .setMerchantName(HANDY_MERCHANT_NAME)
                .setPhoneNumberRequired(false)
                .setShippingAddressRequired(false)
                .setCurrencyCode(CURRENCY_CODE_USD)
                .setEstimatedTotalPrice(itemPrice)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(CURRENCY_CODE_USD)
                        .setTotalPrice(itemPrice)
                        .setLineItems(lineItems)
                        .build())
                .setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                        .addParameter("gateway", "stripe")
                        .addParameter("stripe:publishableKey", quote.getStripeKey())
                        .addParameter("stripe:version", Stripe.VERSION)
                        .build())
                .build();
    }

    public static FullWalletRequest createFullWalletRequest(final BookingQuote quote, final BookingTransaction transaction, MaskedWallet maskedWallet)
    {
        String itemPrice = getItemPrice(quote, transaction);

        return FullWalletRequest.newBuilder()
                .setGoogleTransactionId(maskedWallet.getGoogleTransactionId())
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(CURRENCY_CODE_USD)
                        .setTotalPrice(itemPrice)
                        .build())
                .build();
    }

    private static String getItemPrice(final BookingQuote quote, final BookingTransaction transaction)
    {
        final float hours = transaction.getHours() + transaction.getExtraHours();
        final float[] pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
        return Float.toString(pricing[0] == pricing[1] ? pricing[0] : pricing[1]);
    }
}
