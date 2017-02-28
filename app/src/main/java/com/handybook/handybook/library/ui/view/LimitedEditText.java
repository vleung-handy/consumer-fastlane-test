package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

public class LimitedEditText extends EditText {

    private int maxLines = 1;
    private int maxCharacters = 140;
    private Context context;

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(final int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    @Override
    public int getMaxLines() {
        return maxLines;
    }

    @Override
    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines;
    }

    public LimitedEditText(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public LimitedEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LimitedEditText(final Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final TextWatcher watcher = new TextWatcher() {
            private String text;
            private int beforeCursorPosition = 0;

            @Override
            public void onTextChanged(
                    final CharSequence s, final int start, final int before,
                    final int count
            ) {}

            @Override
            public void beforeTextChanged(
                    final CharSequence s, final int start, final int count,
                    final int after
            ) {
                text = s.toString();
                beforeCursorPosition = start;
            }

            @Override
            public void afterTextChanged(final Editable s) {
                removeTextChangedListener(this);

                if (LimitedEditText.this.getLineCount() > maxLines) {
                    LimitedEditText.this.setText(text);
                    LimitedEditText.this.setSelection(beforeCursorPosition);
                }

                if (s.toString().length() > maxCharacters) {
                    LimitedEditText.this.setText(text);
                    LimitedEditText.this.setSelection(beforeCursorPosition);
                    Toast.makeText(context, "text too long", Toast.LENGTH_SHORT)
                         .show();
                }

                addTextChangedListener(this);
            }
        };

        this.addTextChangedListener(watcher);
    }

}
