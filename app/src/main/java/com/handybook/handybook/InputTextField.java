package com.handybook.handybook;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

abstract class InputTextField extends EditText {
    private final ColorStateList defaultHintColor = this.getHintTextColors();
    private final ColorStateList defaultTextColor = this.getTextColors();
    private boolean isHighlighted;

    public InputTextField(final Context context) {
        super(context);
        init(context);
    }

    public InputTextField(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputTextField (final Context context, final AttributeSet attrs, final  int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setFreezesText(true);
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (start != 0 || before != 0) unHighlight();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    final void highlight() {
        isHighlighted = true;
        this.setHintTextColor(getResources().getColor(R.color.error_red));
        this.setTextColor(getResources().getColor(R.color.error_red));
    }

    final void unHighlight() {
        isHighlighted = false;
        this.setHintTextColor(defaultHintColor);
        this.setTextColor(defaultTextColor);
    }

    final boolean isHighlighted() {
        return isHighlighted;
    }

    abstract boolean validate();
}
