package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;

final class BookingOptionsSelectView extends BookingOptionsIndexView {
    protected String[] optionsSubtitles;
    protected String[] optionsRightText;
    private LinearLayout optionLayout;
    private RelativeLayout layout;
    private HashMap<Integer, CheckBox> checkMap;
    private int checkedIndex;
    private HashSet<Integer> checkedIndexes;
    private boolean isMulti;

    BookingOptionsSelectView(final Context context, final BookingOption option,
                             final OnUpdatedListener updateListener) {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        init(context);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context) {
        final String type = option.getType();
        if (!type.equals("option") && !type.equals("checklist")) return;
        if (type.equals("checklist")) isMulti = true;

        optionsSubtitles = option.getOptionsSubText();
        optionsRightText = option.getOptionsRightText();

        checkMap = new HashMap<>();
        layout = (RelativeLayout)this.findViewById(R.id.layout);
        optionLayout = (LinearLayout)this.findViewById(R.id.options_layout);
        checkedIndex = isMulti ? 0 : Integer.parseInt(option.getDefaultValue());
        checkedIndexes = new HashSet<>();

        for (int i = 0; i < optionsList.length; i++) {
            final String optionText = optionsList[i];
            final LinearLayout optionView = (LinearLayout)LayoutInflater.from(context)
                    .inflate(R.layout.view_booking_select_option, null);

            final TextView title = (TextView)optionView.findViewById(R.id.title_text);
            title.setText(optionText);

            String subTitleText;
            if (optionsSubtitles != null && optionsSubtitles.length >= i + 1
                    && (subTitleText = optionsSubtitles[i]) != null && subTitleText.length() > 0) {
                final TextView subTitle = (TextView)optionView.findViewById(R.id.sub_text);
                subTitle.setText(subTitleText);
                subTitle.setVisibility(VISIBLE);
            }

            String rightText;
            if (optionsRightText != null && optionsRightText.length >= i + 1
                    && (rightText = optionsRightText[i]) != null && rightText.length() > 0) {
                final TextView rightTextView = (TextView)optionView.findViewById(R.id.right_text);
                rightTextView.setText(rightText);
                rightTextView.setVisibility(VISIBLE);
            }

            final CheckBox checkBox = (CheckBox)optionView.findViewById(R.id.check_box);
            if (!isMulti && i == checkedIndex) checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(checkedChanged);
            checkMap.put(i, checkBox);

            if (i < optionsList.length - 1) {
                final LinearLayout.LayoutParams layoutParams
                        = (LinearLayout.LayoutParams)optionLayout.getLayoutParams();

                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                        layoutParams.rightMargin, Utils.toDP(16, context));

                optionView.setLayoutParams(layoutParams);
            }

            optionView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final CheckBox box =  ((CheckBox)optionView.findViewById(R.id.check_box));
                    if (isMulti) box.setChecked(!box.isChecked());
                    else box.setChecked(true);
                }
            });

            optionLayout.addView(optionView);
        }

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    final String getCurrentValue() {
        final CheckBox box = checkMap.get(checkedIndex);
        final RelativeLayout layout = (RelativeLayout)box.getParent();
        final TextView title = (TextView)layout.findViewById(R.id.title_text);
        return title.getText().toString();
    }

    final void setCurrentIndex(final int index) {
        if (index < 0) return;
        for (final CheckBox checkBox : checkMap.values()) checkBox.setChecked(false);
        checkMap.get(index).setChecked(true);
        checkedIndex = index;

        if (updateListener != null) updateListener
                .onUpdate(BookingOptionsSelectView.this);

        invalidate();
        requestLayout();
    }

    final void setCheckedIndexes(final Integer[] indexes) {
        for (final CheckBox checkBox : checkMap.values()) checkBox.setChecked(false);
        checkedIndexes.clear();

        for (final int i : indexes) {
            checkMap.get(i).setChecked(true);
            checkedIndexes.add(i);
        }

        if (updateListener != null) updateListener
                .onUpdate(BookingOptionsSelectView.this);

        invalidate();
        requestLayout();
    }

    final int getCurrentIndex() {
        return checkedIndex;
    }

    final Integer[] getCheckedIndexes() {
        return checkedIndexes.toArray(new Integer[checkedIndexes.size()]);
    }

    public final void hideSeparator() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource((R.drawable.booking_cell_last));
        invalidate();
        requestLayout();
    }

    @Override
    protected void hideTitle() {
        super.hideTitle();

        layout.setPadding(layout.getPaddingLeft(), 0, layout.getPaddingRight(),
                layout.getPaddingBottom());

        invalidate();
        requestLayout();
    }

    private final CompoundButton.OnCheckedChangeListener checkedChanged
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            for (final Integer index : checkMap.keySet()) {
                final CheckBox box = checkMap.get(index);

                box.setOnCheckedChangeListener(null);

                if (box == buttonView) {
                    checkedIndex = index;
                    if (!isMulti) box.setChecked(true);
                    else {
                        box.setChecked(isChecked);
                        if (isChecked) checkedIndexes.add(index);
                        else checkedIndexes.remove(index);
                    }
                }
                else if (!isMulti) box.setChecked(false);

                box.setOnCheckedChangeListener(this);
            }

            if (!warningsMap.isEmpty()) handleWarnings(getCurrentIndex());
            if (!childMap.isEmpty()) handleChildren(getCurrentIndex());
            if (updateListener != null) updateListener
                    .onUpdate(BookingOptionsSelectView.this);
        }
    };
}
