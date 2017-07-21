package com.handybook.handybook.core.ui.widget;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class HandyTextWatcher implements TextWatcher {

    private boolean mDeletingFromEnd;
    private boolean mAddingLastChar;

    @Override
    public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
        mDeletingFromEnd = (start == sequence.length() - 1) && after < count;
        mAddingLastChar = (start == sequence.length()) && after - 1 == count;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(final Editable s) {}

    protected boolean isDeletingFromEnd() {
        return mDeletingFromEnd;
    }

    protected boolean isAddingLastChar() {
        return mAddingLastChar;
    }

    protected void changeText(@NonNull EditText editText, @NonNull CharSequence text) {
        editText.removeTextChangedListener(this);
        editText.setText(text);
        editText.setSelection(text.length());
        editText.addTextChangedListener(this);
    }
}
