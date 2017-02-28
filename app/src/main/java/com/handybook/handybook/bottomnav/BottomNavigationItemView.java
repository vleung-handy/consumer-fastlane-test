package com.handybook.handybook.bottomnav;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * this is mostly copied from android.support.design.internal.BottomNavigationItemView
 */
public class BottomNavigationItemView extends FrameLayout implements MenuView.ItemView {

    public static final int INVALID_ITEM_POSITION = -1;

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private ImageView mIcon;
    private final TextView mLabel;
    //This is a custom handy indicator used for when there is chat messages
    private final View mIndicator;
    private int mItemPosition = INVALID_ITEM_POSITION;

    private MenuItemImpl mItemData;

    private ColorStateList mIconTint;

    public BottomNavigationItemView(@NonNull Context context) {
        this(context, null);
    }

    public BottomNavigationItemView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context)
                      .inflate(
                              com.handybook.handybook.R.layout.layout_bottom_navigation_item,
                              this,
                              true
                      );
        setBackgroundResource(com.handybook.handybook.R.drawable.background_bottom_nav_item);
        mIcon = (ImageView) findViewById(R.id.icon);
        mLabel = (TextView) findViewById(com.handybook.handybook.R.id.bottom_nav_item_label);
        mIndicator = findViewById(com.handybook.handybook.R.id.indicator);
    }

    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        mItemData = itemData;
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setIcon(itemData.getIcon());
        setTitle(itemData.getTitle());
        setId(itemData.getItemId());
    }

    public void setItemPosition(int position) {
        mItemPosition = position;
    }

    public int getItemPosition() {
        return mItemPosition;
    }

    @Override
    public MenuItemImpl getItemData() {
        return mItemData;
    }

    @Override
    public void setTitle(CharSequence title) {
        mLabel.setText(title);
    }

    @Override
    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    @Override
    public void setChecked(boolean checked) {
        mItemData.setChecked(checked);
        ViewCompat.setPivotX(mLabel, mLabel.getWidth() / 2);
        ViewCompat.setPivotY(mLabel, mLabel.getBaseline());
        LayoutParams iconParams = (LayoutParams) mIcon.getLayoutParams();
        iconParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        mIcon.setLayoutParams(iconParams);

        refreshDrawableState();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mLabel.setEnabled(enabled);
        mIcon.setEnabled(enabled);
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mItemData != null && mItemData.isCheckable() && mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    @Override
    public void setIcon(Drawable icon) {
        if (icon != null) {
            Drawable.ConstantState state = icon.getConstantState();
            icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
            DrawableCompat.setTintList(icon, mIconTint);
        }
        mIcon.setImageDrawable(icon);
    }

    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override
    public boolean showsIcon() {
        return true;
    }

    public void setIconTintList(ColorStateList tint) {
        mIconTint = tint;
        if (mItemData != null) {
            // Update the icon so that the tint takes effect
            setIcon(mItemData.getIcon());
        }
    }

    public void setTextColor(ColorStateList color) {
        mLabel.setTextColor(color);
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                                      ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }

    public void showIndicator(boolean showIndicator) {
        mIndicator.setVisibility(showIndicator ? View.VISIBLE : View.GONE);
    }
}
