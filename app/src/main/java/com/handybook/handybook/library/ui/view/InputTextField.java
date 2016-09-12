package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.handybook.handybook.R;

public abstract class InputTextField extends AutoCompleteTextView
{
    private final ColorStateList defaultHintColor = this.getHintTextColors();
    private final ColorStateList defaultTextColor = this.getTextColors();
    private boolean isHighlighted;

    public InputTextField(final Context context)
    {
        super(context);
        init();
    }

    public InputTextField(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public InputTextField(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    protected void init()
    {
        this.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(
                    final CharSequence charSequence, final int start,
                    final int count, final int after
            )
            {

            }

            @Override
            public void onTextChanged(
                    final CharSequence charSequence, final int start,
                    final int before, final int count
            )
            {
                if (start != 0 || before != 0) { unHighlight(); }
            }

            @Override
            public void afterTextChanged(final Editable editable)
            {
            }
        });
    }

    public final void highlight()
    {
        isHighlighted = true;
        this.setHintTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.error_red));
    }

    public final void unHighlight()
    {
        isHighlighted = false;
        this.setHintTextColor(defaultHintColor);
        this.setTextColor(defaultTextColor);
    }

    public final boolean isHighlighted()
    {
        return isHighlighted;
    }

    protected abstract boolean validate();

    public String getString()
    {
        return this.getText().toString();
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.state = isHighlighted ? 1 : 0;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        isHighlighted = ss.state == 1;

        if (isHighlighted)
        {
            highlight();
        }
        else
        {
            unHighlight();
        }
    }

    /**
     * Example of saving state is grabbed from here.
     * http://trickyandroid.com/saving-android-view-state-correctly/
     */
    static class SavedState extends BaseSavedState
    {
        int state;

        SavedState(Parcelable superState)
        {
            super(superState);
        }

        private SavedState(Parcel in)
        {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>()
        {
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }
}
