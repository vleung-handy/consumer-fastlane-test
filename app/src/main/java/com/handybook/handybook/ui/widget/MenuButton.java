package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.util.Utils;

public final class MenuButton extends ImageButton {

    public MenuButton(final Context context, final View parent) {
        super(context);
        init(context, parent);
    }

    public MenuButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public MenuButton(final Context context, final AttributeSet attrs,
                      final int defStyle) {
        super(context, attrs, defStyle);
        init(context, null);
    }

    void init(final Context context, final View parent) {

        //UPGRADE: this stuff should be in an xml not in code
        this.setImageResource(R.drawable.ic_menu);
        this.setBackgroundResource(0);
        this.setPadding(0, 0, 0, 0);
        this.setColor(getResources().getColor(R.color.black_pressed));

        RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        testLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.setLayoutParams(testLP);

        Utils.extendHitArea(this, parent, Utils.toDP(32, context));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final MenuDrawerActivity activity = (MenuDrawerActivity)context;
                activity.getMenuDrawer().toggleMenu();
            }
        });
    }




    public void setColor(final int id) {
        this.setColorFilter(id, PorterDuff.Mode.SRC_ATOP);
    }
}
