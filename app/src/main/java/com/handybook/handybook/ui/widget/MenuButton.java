package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.util.Utils;

public final class MenuButton extends ImageButton
{

    public MenuButton(final Context context, final View parent)
    {
        super(context);
        init(context, parent);
    }

    public MenuButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null);
    }

    public MenuButton(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, null);
    }

    void init(final Context context, final View parent)
    {

        //UPGRADE: this stuff should be in an xml not in code
        setImageResource(R.drawable.ic_menu);
        setBackgroundResource(0);
        setPadding(0, 0, 0, 0);
        setColor(ContextCompat.getColor(getContext(), R.color.black_pressed));

        RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        testLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        setLayoutParams(testLP);

        Utils.extendHitArea(this, parent, Utils.toDP(32, context));

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final MenuDrawerActivity activity = (MenuDrawerActivity) context;
                activity.toggleMenu();
            }
        });
    }


    public void setColor(final int id)
    {
        this.setColorFilter(id, PorterDuff.Mode.SRC_ATOP);
    }
}
