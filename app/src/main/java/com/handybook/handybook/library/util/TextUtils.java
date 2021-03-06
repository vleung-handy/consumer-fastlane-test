package com.handybook.handybook.library.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public final class TextUtils {
    //TODO: split this class out into more specific ones
    /**
     * making a formatter specifically with US locale because server price table values seem to
     * always be based on US locale
     * <p>
     * if we don't explicitly specify the US locale, then when device is of certain locales like
     * Italy, this would format 3.5 to "3,5" instead of "3.5", and server would expect "3.5"
     */
    private static final DecimalFormat AT_MOST_ONE_DECIMAL_PLACE_US_LOCALE_FORMAT =
            new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.US));


    public static final class Fonts {

        public static final String CIRCULAR_BOLD = "CircularStd-Bold.otf";
        public static final String CIRCULAR_BOOK = "CircularStd-Book.otf";
        public static final String CIRCULAR_MEDIUM = "CircularStd-Medium.otf";
    }

    /**
     * Return true if there is nothing meaningful in the string (null, empty string, string with
     * white spaces)
     */
    public static boolean isBlank(final String text) {
        return android.text.TextUtils.isEmpty(text) || text.trim().length() == 0;
    }

    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(final Context c, final String name) {
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                final Typeface t = Typeface.createFromAsset(
                        c.getAssets(),
                        String.format("fonts/%s", name)
                );
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }

    public static String formatToAtMostOneDecimalPlaceUSLocale(final double num) {
        return AT_MOST_ONE_DECIMAL_PLACE_US_LOCALE_FORMAT.format(num);
    }

    public static String formatPrice(
            final float price, final String currencyChar,
            final String currencySuffix
    ) {
        //so a price of 123.40 gets formatted as 123.4
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return (currencyChar != null ? currencyChar : "$")
               + decimalFormat.format(price)
               + (currencySuffix != null ? currencySuffix : "");
    }

    public static String formatPriceCents(
            final int priceCents,
            @NonNull final String currencySymbol
    ) {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setPositivePrefix(currencySymbol);
        decimalFormat.setNegativePrefix("-" + currencySymbol);
        return decimalFormat.format(priceCents / 100.0);
    }

    public static String formatPhone(String phone, final String prefix) {
        String shortFormat = "(%s) %s", longFormat = "(%s) %s-%s";
        if (prefix != null && prefix.equals("+44")) {
            shortFormat = "%s %s";
            longFormat = "%s %s %s";
        }

        phone = phone.replaceAll("\\D+", "");

        if (phone.length() < 4) {
            return phone;
        }
        else if (phone.length() >= 4 && phone.length() <= 6) {
            return String.format(shortFormat, phone.substring(0, 3), phone.substring(3));
        }
        else if (phone.length() >= 7 && phone.length() <= 10) {
            return String.format(
                    longFormat, phone.substring(0, 3), phone.substring(3, 6), phone.substring(6));
        }
        else {
            return phone;
        }
    }

    public static String formatAddress(
            final String address1, final String address2, final String city,
            final String state, final String zip
    ) {
        return address1 + (address2 != null && address2.length() > 0 ? ", "
                                                                       + address2 + "\n" : "\n") +
               city + ", "
               + state + " " + (zip != null ? zip.replace(" ", "\u00A0") : null);
    }

    public static String formatDate(final Date date, final String format) {
        if (date == null) {
            return null;
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        final DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setAmPmStrings(new String[]{"am", "pm"});
        dateFormat.setDateFormatSymbols(symbols);
        return dateFormat.format(date);
    }

    public static String formatDecimal(final float value, final String format) {
        final DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(value);
    }

    public static String toTitleCase(final String str) {
        if (str == null) {
            return null;
        }

        boolean space = true;

        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            }
            else if (Character.isWhitespace(c)) {
                space = true;
            }
            else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static CharSequence trim(final CharSequence s) {
        int start = 0;
        int end = s.length();

        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }

        return s.subSequence(start, end);
    }

    public static void stripUnderlines(final TextView textView) {
        final Spannable s = new SpannableString(textView.getText());
        final URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            final int start = s.getSpanStart(span);
            final int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private static final class URLSpanNoUnderline extends URLSpan {

        URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public final void updateDrawState(final TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    @NonNull
    public static Spanned fromHtml(@NonNull final String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            //noinspection deprecation
            return Html.fromHtml(text);
        }
    }


}
