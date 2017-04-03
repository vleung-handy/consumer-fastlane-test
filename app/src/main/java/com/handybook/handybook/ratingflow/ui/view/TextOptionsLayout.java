package com.handybook.handybook.ratingflow.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.rating.GridDisplayItem;
import com.handybook.handybook.library.util.TextUtils;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;

public class TextOptionsLayout extends FlowLayout {

    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;
    @BindDimen(R.dimen.default_margin_quarter)
    int mDefaultMarginQuarter;
    @BindColor(R.color.handy_text_black)
    int mBlackCheckedColor;
    @BindColor(R.color.black_pressed)
    int mBlackUncheckedColor;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    public TextOptionsLayout(
            final Context context,
            @NonNull final List<GridDisplayItem> displayItems,
            @NonNull final CompoundButton.OnCheckedChangeListener onCheckedChangeListener
    ) {
        super(context);
        mOnCheckedChangeListener = onCheckedChangeListener;
        setGravity(Gravity.CENTER);
        init(displayItems);
    }

    private void init(final List<GridDisplayItem> displayItems) {
        ButterKnife.bind(this);
        for (final GridDisplayItem displayItem : displayItems) {
            addView(createTextCheckbox(displayItem));
        }
    }

    @NonNull
    private CheckBox createTextCheckbox(final GridDisplayItem item) {
        final CheckBox checkBox = new CheckBox(getContext());
        final LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(
                mDefaultMarginQuarter,
                mDefaultMarginQuarter,
                mDefaultMarginQuarter,
                mDefaultMarginQuarter
        );
        checkBox.setLayoutParams(layoutParams);
        checkBox.setChecked(item.isSelected());
        checkBox.setPadding(mDefaultPadding, mDefaultPadding, mDefaultPadding, mDefaultPadding);
        checkBox.setBackgroundResource(R.drawable.option_text_checkbox);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setButtonDrawable(R.color.transparent);
        checkBox.setTextColor(new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        }, new int[]{mBlackCheckedColor, mBlackUncheckedColor}));
        checkBox.setText(item.getReason().getValue());
        checkBox.setTypeface(TextUtils.get(getContext(), TextUtils.Fonts.CIRCULAR_BOOK));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                item.setSelected(isChecked);
                mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });
        return checkBox;
    }
}
