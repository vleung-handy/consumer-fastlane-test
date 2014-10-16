package com.handybook.handybook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.simplealertdialog.SimpleAlertDialog;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

abstract class MenuDrawerActivity extends BaseActivity  implements SimpleAlertDialog.OnClickListener {
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

        final FragmentManager fm = getSupportFragmentManager();
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

    final MenuDrawer getMenuDrawer() {
        return menuDrawer;
    }

    final void navigateToActivity(final Class<? extends Activity> clazz) {
        final Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MenuDrawerActivity.EXTRA_SHOW_NAV_FOR_TRANSITION, true);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        this.finish();
    }

    @Override
    public final void onDialogPositiveButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {
        final FragmentManager fm = getSupportFragmentManager();
        final NavigationFragment navFragment
                = (NavigationFragment)fm.findFragmentById(R.id.nav_fragment_container);
        if (requestCode == 1) navFragment.onDialogPositiveButtonClicked(dialog, requestCode, view);
    }

    @Override
    public final void onDialogNegativeButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {
        final FragmentManager fm = getSupportFragmentManager();
        final NavigationFragment navFragment
                = (NavigationFragment)fm.findFragmentById(R.id.nav_fragment_container);
        if (requestCode == 1) navFragment.onDialogNegativeButtonClicked(dialog, requestCode, view);
    }
}
