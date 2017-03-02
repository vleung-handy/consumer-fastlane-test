package com.handybook.handybook.bottomnav;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * this is copied from android.support.design.internal.BottomNavigationMenu
 */
public final class BottomNavigationMenu extends MenuBuilder {

    public static final int MAX_ITEM_COUNT = 5;

    public BottomNavigationMenu(Context context) {
        super(context);
    }

    @Override
    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        throw new UnsupportedOperationException("BottomNavigationView does not support submenus");
    }

    @Override
    protected MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
        if (size() + 1 > MAX_ITEM_COUNT) {
            throw new IllegalArgumentException(
                    "Maximum number of items supported by BottomNavigationView is " + MAX_ITEM_COUNT
                    + ". Limit can be checked with BottomNavigationView#getMaxItemCount()");
        }
        return super.addInternal(group, id, categoryOrder, title);
    }
}
