package com.handybook.handybook;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

import java.text.DecimalFormat;

public final class TextUtils {

    static String formatPrice(final float price, final String currencyChar,
                              final String currencySuffix) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return (currencyChar != null ? currencyChar : "")
                + decimalFormat.format(price)
                + (currencySuffix != null ? currencySuffix : "");
    }

    static String formatPhone(String phone, final String prefix) {
        String shortFormat = "(%s) %s", longFormat = "(%s) %s-%s";
        if (prefix != null && prefix.equals("+44")) {
            shortFormat = "%s %s";
            longFormat = "%s %s %s";
        }

        phone = phone.replaceAll("\\D+", "");

        if (phone.length() < 4) return phone;

        else if (phone.length() >= 4 && phone.length() <= 6)
            return String.format(shortFormat, phone.substring(0, 3), phone.substring(3));

        else if (phone.length() >= 7 && phone.length() <= 10)
            return String.format(longFormat, phone.substring(0, 3), phone.substring(3, 6),
                    phone.substring(6));

        else return phone;
    }

    static void stripUnderlines(final TextView textView) {
        final Spannable s = new SpannableString(textView.getText());
        final URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span: spans) {
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
}
