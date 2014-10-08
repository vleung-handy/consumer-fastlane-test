package com.handybook.handybook;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

abstract class MenuDrawerActivity extends Activity {
    private static final String STATE_MENU_DRAWER = "MENU_DRAWER";
    protected static final String EXTRA_SHOW_NAV_FOR_TRANSITION = "EXTRA_SHOW_NAV_FOR_TRANSITION";

    private MenuDrawer menuDrawer;
    private boolean showNavForTransition;

    protected abstract Fragment createFragment();
    protected abstract String getNavItemTitle();

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT,
                MenuDrawer.MENU_DRAG_WINDOW);
        menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        menuDrawer.setDropShadowSize(1);
        menuDrawer.setContentView(R.layout.activity_menu_drawer);
        menuDrawer.setMenuView(R.layout.activity_menu_nav);

        final FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        NavigationFragment navFragment
                = (NavigationFragment)fm.findFragmentById(R.id.nav_fragment_container);

        if (navFragment == null) {
            navFragment = NavigationFragment.newInstance(getNavItemTitle());
            fm.beginTransaction().add(R.id.nav_fragment_container, navFragment).commit();
        }

        if (savedInstanceState == null && (showNavForTransition
                = getIntent().getBooleanExtra(EXTRA_SHOW_NAV_FOR_TRANSITION, false))) {
            menuDrawer.openMenu(false);
        }
    }

    @Override
    protected final void onStart() {
        super.onStart();
        if (showNavForTransition) {
            menuDrawer.closeMenu();
            showNavForTransition = false;
        }
    }

    @Override
    protected final void onRestoreInstanceState(final Bundle inState) {
        super.onRestoreInstanceState(inState);
        menuDrawer.restoreState(inState.getParcelable(STATE_MENU_DRAWER));
    }

    @Override
    protected final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MENU_DRAWER, menuDrawer.saveState());
    }

    @Override
    public final void onBackPressed() {
        final int drawerState = menuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            menuDrawer.closeMenu();
            return;
        }
        super.onBackPressed();
    }

    public MenuDrawer getMenuDrawer() {
        return menuDrawer;
    }
}
