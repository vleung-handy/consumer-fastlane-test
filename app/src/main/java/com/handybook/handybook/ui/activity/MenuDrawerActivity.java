package com.handybook.handybook.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.simplealertdialog.SimpleAlertDialog;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public abstract class MenuDrawerActivity extends BaseActivity  implements SimpleAlertDialog.OnClickListener {
    private static final String STATE_MENU_DRAWER = "MENU_DRAWER";
    protected static final String EXTRA_SHOW_NAV_FOR_TRANSITION = "EXTRA_SHOW_NAV_FOR_TRANSITION";

    private MenuDrawer menuDrawer;
    private boolean showNavForTransition;
    protected boolean disableDrawer;
    private OnDrawerStateChangeListener onDrawerStateChangeListener;

    protected abstract Fragment createFragment();
    protected abstract String getNavItemTitle();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

        menuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(final View view, final int i, final int i2, final int i3) {
                return disableDrawer;
            }
        });

        menuDrawer.setOnDrawerStateChangeListener(
            new MenuDrawer.OnDrawerStateChangeListener() {
                @Override
                public void onDrawerStateChange(final int oldState, final int newState) {
                    if (newState == MenuDrawer.STATE_OPENING
                            || newState == MenuDrawer.STATE_CLOSING
                            || newState == MenuDrawer.STATE_DRAGGING) {

                        InputMethodManager imm = (InputMethodManager)MenuDrawerActivity.this
                                .getSystemService(INPUT_METHOD_SERVICE);

                        final View focus = MenuDrawerActivity.this.getCurrentFocus();
                        if (focus != null) {
                            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                        }
                    }

                    if (onDrawerStateChangeListener != null)
                        onDrawerStateChangeListener.onDrawerStateChange(menuDrawer, oldState, newState);
                }

                @Override
                public void onDrawerSlide(float openRatio, int offsetPixels) {}
        });
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

    public final MenuDrawer getMenuDrawer() {
        return menuDrawer;
    }

    public final void navigateToActivity(final Class<? extends Activity> clazz) {
        final Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MenuDrawerActivity.EXTRA_SHOW_NAV_FOR_TRANSITION, true);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        this.finish();
    }
    
    public final void setDrawerDisabled(final boolean disableDrawer) {
        this.disableDrawer = disableDrawer;
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

    public void setOnDrawerStateChangedListener(final OnDrawerStateChangeListener onDrawerStateChangedListener) {
        this.onDrawerStateChangeListener = onDrawerStateChangedListener;
    }

    public interface OnDrawerStateChangeListener {
        void onDrawerStateChange(final MenuDrawer menuDrawer, final int oldState, final int newState);
    }
}
