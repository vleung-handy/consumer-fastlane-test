package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

public class FreezableInputTextView extends InputTextField {

    private int defaultInputType;

    public FreezableInputTextView(final Context context) {
        super(context);
        init();
    }

    public FreezableInputTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FreezableInputTextView(
            final Context context, final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        super.init();
        defaultInputType = this.getInputType();
    }

    public void setDisabled(final boolean disabled, final String hint) {
        if (disabled) {
            setHint(hint);
            setInputType(InputType.TYPE_NULL);
            setEnabled(false);
            clearFocus();
        }
        else {
            setHint(hint);
            setInputType(defaultInputType);
            setEnabled(true);
        }
    }

    protected boolean validate() {
        return true;
    }
}
