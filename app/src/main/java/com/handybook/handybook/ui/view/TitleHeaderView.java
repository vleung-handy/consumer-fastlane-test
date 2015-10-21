package com.handybook.handybook.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;

public class TitleHeaderView extends InjectedLinearLayout
{
    @Bind(R.id.title_text)
    TextView titleText;
    public TitleHeaderView(Context context)
    {
        super(context);
    }

    public TitleHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TitleHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setTitleString(String titleString)
    {
        titleText.setText(titleString);
    }
}
