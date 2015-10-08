package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;

public final class BookingOptionsTextView extends BookingOptionsView
{
    private EditText editText;

    public BookingOptionsTextView(final Context context, final BookingOption option,
                                  final OnUpdatedListener updateListener)
    {
        super(context, R.layout.view_booking_options_text, option, updateListener);
        init();
    }

    BookingOptionsTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    BookingOptionsTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private void init()
    {
        if (!option.getType().equals("text"))
        {
            return;
        }

        mainLayout = (RelativeLayout) this.findViewById(R.id.rel_layout);

        editText = (EditText) this.findViewById(R.id.edit_text);
        editText.setGravity(Gravity.TOP);
        editText.setHint(option.getDefaultValue());
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                          final int after)
            {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                                      final int count)
            {

            }

            @Override
            public void afterTextChanged(final Editable s)
            {
                if (updateListener != null)
                {
                    updateListener.onUpdate(BookingOptionsTextView.this);
                }
            }
        });
    }

    public final String getCurrentValue()
    {
        return editText.getText().toString().trim();
    }

    public final void setValue(final String value)
    {
        editText.setText(value);
    }

    public final void enableSingleMode()
    {
        mainLayout.setBackgroundResource(0);
        mainLayout.setPadding(mainLayout.getPaddingLeft(), 0, mainLayout.getPaddingRight(), 0);
        invalidate();
        requestLayout();
    }
}
