package com.handybook.handybook.library.util;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.CreditCard;

public final class CreditCardUtils {

    private static final String VISA_PREFIX = "4";
    private static final String MASTERCARD_PREFIX = "5";
    private static final String DISCOVER_PREFIX = "6";
    private static final String AMEX_PREFIX_1 = "34";
    private static final String AMEX_PREFIX_2 = "37";

    @NonNull
    public static String formatCreditCardNumberAfterEndingDigitAdded(@NonNull String number) {
        if (number.length() < 5) {
            return number;
        }
        if (number.startsWith(AMEX_PREFIX_1) || number.startsWith(AMEX_PREFIX_2)) {
            if (number.length() == 5) {
                return String.format("%s %s", number.substring(0, 4), number.substring(4));
            }
            else if (number.length() == 12) {
                return String.format("%s %s", number.substring(0, 11), number.substring(11));
            }
            return number;
        }
        else {
            // All other credit cards
            if (number.length() == 5) {
                return String.format("%s %s", number.substring(0, 4), number.substring(4));
            }
            else if (number.length() == 10) {
                return String.format("%s %s", number.substring(0, 9), number.substring(9));
            }
            else if (number.length() == 15) {
                return String.format("%s %s", number.substring(0, 14), number.substring(14));
            }
            return number;
        }
    }

    @NonNull
    public static String formatCreditCardNumber(@NonNull final String number) {
        if (number.length() < 1) {
            return number;
        }

        final String raw = number.replaceAll("\\D+", "");

        if (number.length() < 5) {
            return raw;
        }
        else if (number.startsWith(AMEX_PREFIX_1) || number.startsWith(AMEX_PREFIX_2)) {
            if (raw.length() >= 5 && raw.length() <= 10) {
                return String.format(
                        "%s %s",
                        raw.substring(0, 4),
                        raw.substring(4)
                );
            }

            if (raw.length() >= 11) {
                return String.format(
                        "%s %s %s",
                        raw.substring(0, 4),
                        raw.substring(4, 10),
                        raw.substring(10)
                );
            }
        }
        else {
            // Non AMEX credit card types
            if (raw.length() >= 5 && raw.length() <= 8) {
                return String.format(
                        "%s %s",
                        raw.substring(0, 4),
                        raw.substring(4)
                );
            }

            if (raw.length() >= 9 && raw.length() <= 12) {
                return String.format(
                        "%s %s %s",
                        raw.substring(0, 4),
                        raw.substring(4, 8),
                        raw.substring(8)
                );
            }

            if (raw.length() >= 13) {
                return String.format(
                        "%s %s %s %s",
                        raw.substring(0, 4),
                        raw.substring(4, 8),
                        raw.substring(8, 12),
                        raw.substring(12)
                );
            }
        }
        return raw;
    }

    @NonNull
    public static String formatCreditCardExpDate(@NonNull final String number) {
        if (number.length() < 1) {
            return number;
        }

        final String raw = number.replaceAll("\\D+", "");

        if (raw.length() >= 3) {
            return String.format("%s/%s", raw.substring(0, 2), raw.substring(2));
        }

        return raw;
    }

    @NonNull
    public static CreditCard.Type getCreditCardType(@NonNull final String number) {
        if (number.startsWith(AMEX_PREFIX_1) || number.startsWith(AMEX_PREFIX_2)) {
            return CreditCard.Type.AMEX;
        }
        else if (number.startsWith(VISA_PREFIX)) {
            return CreditCard.Type.VISA;
        }
        else if (number.startsWith(MASTERCARD_PREFIX)) {
            return CreditCard.Type.MASTERCARD;
        }
        else if (number.startsWith(DISCOVER_PREFIX)) {
            return CreditCard.Type.DISCOVER;
        }
        else {
            return CreditCard.Type.OTHER;
        }
    }
}
