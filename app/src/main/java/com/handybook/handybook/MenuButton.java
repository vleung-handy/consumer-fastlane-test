package com.handybook.handybook;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public final class MenuButton extends ImageButton {

    public MenuButton(final Context context) {
        super(context);
        init(context);
    }

    public MenuButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MenuButton(final Context context, final AttributeSet attrs,
                      final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    void init(final Context context) {
        this.setImageResource(R.drawable.ic_menu);
        this.setBackgroundResource(0);
        this.setPadding(0,0,0,0);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final MenuDrawerActivity activity = (MenuDrawerActivity)context;
                activity.getMenuDrawer().toggleMenu();
            }
        });
    }

    void setColor(final int id) {
        this.setColorFilter(id, PorterDuff.Mode.SRC_ATOP);
    }
}
