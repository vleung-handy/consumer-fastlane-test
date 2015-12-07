package com.handybook.handybook.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

/**
 * a navigation bar with an x button
 */
public class NavbarXButton extends LinearLayout
{
    //putting this into a widget because we will soon use these everywhere
    public NavbarXButton(final Context context)
    {
        super(context);
        initView(context, null);
    }

    public NavbarXButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    public NavbarXButton(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public NavbarXButton(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(final Context context, final AttributeSet attrs)
    {
        if (attrs == null) { return; }
        LayoutInflater.from(context).inflate(R.layout.element_navbar_x_button, this);

        TypedArray androidAttributes = context.obtainStyledAttributes(attrs, new
                int[]{android.R.attr.text});
        try
        {
            String s = androidAttributes.getString(0);
            if (s != null)
            {
                TextView textView = (TextView) findViewById(R.id.nav_text);
                textView.setText(s);
            }
        }
        finally
        {
            androidAttributes.recycle();
        }
    }
}
