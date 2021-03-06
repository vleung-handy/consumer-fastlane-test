package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.handybook.handybook.R;

public abstract class InputTextField extends EditText {

    private final ColorStateList defaultHintColor = this.getHintTextColors();
    private final ColorStateList defaultTextColor = this.getTextColors();
    private boolean isHighlighted;

    public InputTextField(final Context context) {
        super(context);
        init();
    }

    public InputTextField(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputTextField(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    final CharSequence charSequence, final int start,
                    final int count, final int after
            ) {

            }

            @Override
            public void onTextChanged(
                    final CharSequence charSequence, final int start,
                    final int before, final int count
            ) {
                if (start != 0 || before != 0) { unHighlight(); }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });
    }

    public final void highlight() {
        isHighlighted = true;
        this.setHintTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
    }

    public final void unHighlight() {
        isHighlighted = false;
        this.setHintTextColor(defaultHintColor);
        this.setTextColor(defaultTextColor);
    }

    public final boolean isHighlighted() {
        return isHighlighted;
    }

    protected abstract boolean validate();

    public String getString() {
        return this.getText().toString();
    }
}
