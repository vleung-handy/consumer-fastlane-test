package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class HtmlTextView extends android.support.v7.widget.AppCompatTextView {

    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        //Strip underlines
        final Spannable spannableText = new SpannableString(Html.fromHtml(text.toString()));
        final URLSpan[] spans = spannableText.getSpans(0, spannableText.length(), URLSpan.class);
        for (URLSpan span : spans) {
            final int start = spannableText.getSpanStart(span);
            final int end = spannableText.getSpanEnd(span);
            spannableText.removeSpan(span);
            span = new UrlSpanNoUnderline(span.getURL());
            spannableText.setSpan(span, start, end, 0);
        }
        super.setText(spannableText, type);
    }

    private static final class UrlSpanNoUnderline extends URLSpan {

        UrlSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public final void updateDrawState(final TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

    }

}
