package com.handybook.handybook.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.view.InjectedLinearLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * a navigation bar with a back button
 *
 * temporary until we use Toolbar
 */
public class BackButtonNavBar extends InjectedLinearLayout
{
    @Bind(R.id.back_button)
    ImageButton mBackButton;

    //putting this into a widget because we will soon use these everywhere
    public BackButtonNavBar(final Context context)
    {
        super(context);
        initView(context, null);
    }

    public BackButtonNavBar(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    public BackButtonNavBar(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setBackButtonVisible(final boolean visible)
    {
        mBackButton.setVisibility(visible ? VISIBLE : GONE);
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClick()
    {
        if(getContext() instanceof Activity)
        {
            ((Activity) getContext()).onBackPressed();
        }
    }

    private void initView(final Context context, final AttributeSet attrs)
    {
        if (attrs == null) { return; }
        LayoutInflater.from(context).inflate(R.layout.element_navbar_back_button, this);

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
