package com.handybook.handybook.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

public class ThinIconButton extends LinearLayout //TODO: give better name
{
    public ThinIconButton(final Context context)
    {
        super(context);
        initView(context, null);
    }

    public ThinIconButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    public ThinIconButton(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public ThinIconButton(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    //TODO: clean up
    private void initView(final Context context, final AttributeSet attrs)
    {
        if (attrs == null) { return; }
        LayoutInflater.from(context).inflate(R.layout.element_thin_icon_button, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ThinIconButton,
                0, 0);
        try
        {
            Drawable iconDrawable = a.getDrawable(R.styleable.ThinIconButton_iconResourceId);
            if (iconDrawable != null)
            {
                ImageView iconImageView = (ImageView) findViewById(R.id
                        .thin_icon_button_left_icon_image);
                iconImageView.setImageDrawable(iconDrawable);
            }
            //TODO: how can i simply access the android:text attribute?
            String text = a.getString(R.styleable.ThinIconButton_text);
            if (text != null)
            {
                TextView textView = (TextView) findViewById(R.id.thin_icon_button_text);
                textView.setText(text);
            }
        }
        finally
        {
            a.recycle();
        }
    }

}
