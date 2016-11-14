package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.handybook.handybook.R;

/**
 * A button that is checkable, using a custom image to represent checked/unchecked states
 */
public class CheckableImageButton extends ImageButton
        implements View.OnClickListener, Checkable
{
    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;
    private OnClickListener mListener;
    private boolean mChecked;

    public CheckableImageButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CheckableImageButton(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    )
    {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init()
    {
        setAdjustViewBounds(true);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_radio_on_blue);
        mUncheckedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_radio_off_gray);
        this.setOnClickListener(this);

        setChecked(false);
        updateState();
    }

    @Override
    public void onClick(final View view)
    {
        toggle();

        if (mListener != null)
        {
            mListener.onClick(this);
        }
    }

    @Override
    public void setChecked(final boolean checked)
    {
        mChecked = checked;
        updateState();
    }

    @Override
    public boolean isChecked()
    {
        return mChecked;
    }

    @Override
    public void toggle()
    {
        setChecked(!isChecked());
    }

    public void setListener(final OnClickListener listener)
    {
        mListener = listener;
    }

    /**
     * Must call this to refresh the layout
     */
    public void updateState()
    {
        setImageDrawable(isChecked() ? mCheckedDrawable : mUncheckedDrawable);
    }
}
