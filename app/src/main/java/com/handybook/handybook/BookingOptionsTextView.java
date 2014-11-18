package com.handybook.handybook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

final class BookingOptionsTextView extends BookingOptionsView {
    private EditText editText;

    BookingOptionsTextView(final Context context, final BookingOption option,
                           final OnUpdatedListener updateListener) {
        super(context, R.layout.view_booking_options_text, option, updateListener);
        init();
    }

    BookingOptionsTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        if (!option.getType().equals("text")) return;

        editText = (EditText)this.findViewById(R.id.edit_text);
        editText.setHint(option.getDefaultValue());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                          final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                                      final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (updateListener != null) updateListener
                        .onUpdate(BookingOptionsTextView.this);
            }
        });
    }

    final String getCurrentValue() {
        return editText.getText().toString().trim();
    }

    final void setValue(final String value) {
        editText.setText(value);
    }

    public final void hideSeparator() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource((R.drawable.booking_cell_last));
        invalidate();
        requestLayout();
    }

    public final void enableSingleMode() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource(0);
        view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight(), 0);
        invalidate();
        requestLayout();
    }
}
