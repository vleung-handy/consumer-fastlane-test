package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ImageToggleButton extends LinearLayout implements View.OnClickListener, Checkable
{

    @Bind(R.id.image)
    ImageView mImage;

    @Bind(R.id.text)
    TextView mText;

    private Drawable mCheckedDrawable;
    private Drawable mUncheckedDrawable;

    private Drawable mCheckedBgDrawable;
    private Drawable mUncheckedBgDrawable;

    private Drawable mRippleDrawable;

    private String mCheckedText;
    private String mUncheckedText;

    private boolean mChecked;

    private OnClickListener mListener;
    private String mTag = "";

    public ImageToggleButton(Context context)
    {
        super(context);
        init();
    }

    public ImageToggleButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageToggleButton);

        mCheckedDrawable = a.getDrawable(R.styleable.ImageToggleButton_checked_icon);
        mUncheckedDrawable = a.getDrawable(R.styleable.ImageToggleButton_unchecked_icon);

        mCheckedText = a.getString(R.styleable.ImageToggleButton_checked_text);
        mUncheckedText = a.getString(R.styleable.ImageToggleButton_unchecked_text);

        mCheckedBgDrawable = a.getDrawable(R.styleable.ImageToggleButton_checked_bg);
        mUncheckedBgDrawable = a.getDrawable(R.styleable.ImageToggleButton_unchecked_bg);

        mRippleDrawable = a.getDrawable(R.styleable.ImageToggleButton_ripple_drawable);

        a.recycle();

        init();
    }

    void init()
    {
        inflate(getContext(), R.layout.image_toggle_button, this);
        ButterKnife.bind(this);

        setClipChildren(false);
        setClipToPadding(false);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        this.setOnClickListener(this);

        setChecked(false);
        updateState();

        if (mRippleDrawable != null)
        {
            setRipples();
        }
    }

    public void setListener(final OnClickListener listener)
    {
        mListener = listener;
    }

    public void setCheckedDrawable(final Drawable checkedDrawable)
    {
        mCheckedDrawable = checkedDrawable;
    }

    public void setUncheckedDrawable(final Drawable uncheckedDrawable)
    {
        mUncheckedDrawable = uncheckedDrawable;
    }

    public void setCheckedText(final String checkedText)
    {
        mCheckedText = checkedText;
    }

    public void setUncheckedText(final String uncheckedText)
    {
        mUncheckedText = uncheckedText;
    }

    private void setRipples()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            setClickable(true);
            mText.setClickable(false);
            mImage.setClickable(false);
            setForeground(mRippleDrawable);
        }
    }

    @Override
    public void onClick(View view)
    {
        toggle();
        updateState();

        if (mListener != null)
        {
            mListener.onClick(this);
        }
    }

    @Override
    @NonNull
    public String getTag()
    {
        return mTag;
    }

    public void setTag(@NonNull final String tag)
    {
        mTag = tag;
    }

    /**
     * Must call this to refresh the layout
     */
    @SuppressWarnings("deprecation")
    public void updateState()
    {
        if (isChecked())
        {
            mImage.setImageDrawable(mCheckedDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                setBackground(mCheckedBgDrawable);
            }
            else
            {
                setBackgroundDrawable(mCheckedBgDrawable);
            }
            mText.setText(mCheckedText);
        }
        else
        {
            mImage.setImageDrawable(mUncheckedDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                setBackground(mUncheckedBgDrawable);
            }
            else
            {
                setBackgroundDrawable(mUncheckedBgDrawable);
            }
            mText.setText(mUncheckedText);
        }
    }

    @Override
    public void setChecked(boolean b)
    {
        mChecked = b;
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
}
