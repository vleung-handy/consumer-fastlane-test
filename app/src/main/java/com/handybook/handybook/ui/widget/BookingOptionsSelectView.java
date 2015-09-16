package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public final class BookingOptionsSelectView extends BookingOptionsIndexView
{
    protected String[] optionsSubtitles;
    protected String[] optionsRightText;
    private HashMap<Integer, CheckBox> checkMap;
    private int checkedIndex;
    private HashSet<Integer> checkedIndexes;
    private boolean isMulti;

    public BookingOptionsSelectView(final Context context, final BookingOption option,
                                    final OnUpdatedListener updateListener)
    {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        init(context);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    BookingOptionsSelectView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private void init(final Context context)
    {
        final String type = option.getType();

        if (!type.equals("option") && !type.equals("checklist"))
        {
            return;
        }

        if (type.equals("checklist"))
        {
            isMulti = true;
        }

        mainLayout = (RelativeLayout) this.findViewById(R.id.rel_layout);

        final LinearLayout optionLayout = (LinearLayout) this.findViewById(R.id.options_layout);

        optionsSubtitles = option.getOptionsSubText();
        optionsRightText = option.getOptionsRightText();

        checkMap = new HashMap<>();


        int optionDefaultValue = 0;

        if (option.getDefaultValue() != null)
        {
            try
            {
                optionDefaultValue = Integer.parseInt(option.getDefaultValue());
            }
            catch (NumberFormatException e)
            {
                optionDefaultValue = 0;
            }
        }

        checkedIndex = isMulti ? 0 : optionDefaultValue;
        checkedIndexes = new HashSet<>();

        for (int i = 0; i < optionsList.length; i++)
        {
            final String optionText = optionsList[i];
            final LinearLayout optionView = (LinearLayout) LayoutInflater.from(context)
                    .inflate(R.layout.view_booking_select_option, this, false);

            final TextView title = (TextView) optionView.findViewById(R.id.title_text);
            title.setText(optionText);

            String subTitleText;
            if (optionsSubtitles != null &&
                    optionsSubtitles.length >= i + 1 &&
                    (subTitleText = optionsSubtitles[i]) != null && subTitleText.length() > 0)
            {
                final TextView subTitle = (TextView) optionView.findViewById(R.id.sub_text);
                subTitle.setText(subTitleText);
                subTitle.setVisibility(VISIBLE);
            }

            String rightText;
            if (optionsRightText != null &&
                    optionsRightText.length >= i + 1 &&
                    (rightText = optionsRightText[i]) != null && rightText.length() > 0)
            {
                final TextView rightTextView = (TextView) optionView.findViewById(R.id.right_text);
                rightTextView.setText(rightText);
                rightTextView.setVisibility(VISIBLE);
            }

            final CheckBox checkBox = (CheckBox) optionView.findViewById(R.id.check_box);
            if (!isMulti && i == checkedIndex)
            {
                selectOption(checkBox, true);
            }
            checkBox.setOnCheckedChangeListener(checkedChanged);
            checkMap.put(i, checkBox);

            if (i < optionsList.length - 1)
            {
                final LinearLayout.LayoutParams layoutParams
                        = (LinearLayout.LayoutParams) optionLayout.getLayoutParams();

                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                        layoutParams.rightMargin, Utils.toDP(16, context));

                optionView.setLayoutParams(layoutParams);
            }

            optionView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    final CheckBox box = ((CheckBox) optionView.findViewById(R.id.check_box));
                    if (isMulti)
                    {
                        box.setChecked(!box.isChecked());
                    }
                    else
                    {
                        box.setChecked(true);
                    }
                }
            });

            optionLayout.addView(optionView);
        }

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    public final String getCurrentValue()
    {
        final CheckBox box = checkMap.get(checkedIndex);
        final ViewGroup layout = (ViewGroup) box.getParent();
        final TextView title = (TextView) layout.findViewById(R.id.title_text);
        return title.getText().toString();
    }

    public final void setCurrentIndex(final int index)
    {
        if (index < 0)
        {
            return;
        }

        for (final CheckBox checkBox : checkMap.values())
        {
            checkBox.setChecked(false);
        }

        checkMap.get(index).setChecked(true);
        checkedIndex = index;

        if (updateListener != null)
        {
            updateListener.onUpdate(BookingOptionsSelectView.this);
        }

        invalidate();
        requestLayout();
    }

    public final void setCheckedIndexes(final Integer[] indexes)
    {
        for (final CheckBox checkBox : checkMap.values())
        {
            checkBox.setChecked(false);
        }
        checkedIndexes.clear();

        for (final int i : indexes)
        {
            checkMap.get(i).setChecked(true);
            checkedIndexes.add(i);
        }

        if (updateListener != null)
        {
            updateListener.onUpdate(BookingOptionsSelectView.this);
        }

        invalidate();
        requestLayout();
    }

    public final int getCurrentIndex()
    {
        return checkedIndex;
    }

    public final Integer[] getCheckedIndexes()
    {
        final Integer[] indexes = checkedIndexes.toArray(new Integer[checkedIndexes.size()]);
        Arrays.sort(indexes);
        return indexes;
    }

    @Override
    public void hideTitle()
    {
        super.hideTitle();

        mainLayout.setPadding(mainLayout.getPaddingLeft(), 0, mainLayout.getPaddingRight(), mainLayout.getPaddingBottom());

        invalidate();
        requestLayout();
    }

    private void selectOption(final CheckBox box, final boolean isChecked)
    {
        final ViewGroup layout = (ViewGroup) box.getParent();
        final TextView title = (TextView) layout.findViewById(R.id.title_text);
        final TextView subTitle = (TextView) layout.findViewById(R.id.sub_text);
        final TextView rightText = (TextView) layout.findViewById(R.id.right_text);

        if (isChecked)
        {
            title.setTextColor(getResources().getColor(R.color.black));
            subTitle.setTextColor(getResources().getColor(R.color.handy_blue));
            rightText.setTextColor(getResources().getColor(R.color.black));
            box.setChecked(true);
        }
        else
        {
            title.setTextColor(getResources().getColor(R.color.black_pressed));
            subTitle.setTextColor(getResources().getColor(R.color.black_pressed));
            rightText.setTextColor(getResources().getColor(R.color.black_pressed));
            box.setChecked(false);
        }
    }

    private final CompoundButton.OnCheckedChangeListener checkedChanged
            = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
        {
            for (final Integer index : checkMap.keySet())
            {
                final CheckBox box = checkMap.get(index);

                box.setOnCheckedChangeListener(null);

                if (box == buttonView)
                {
                    checkedIndex = index;
                    if (!isMulti)
                    {
                        selectOption(box, true);
                    }
                    else
                    {
                        selectOption(box, isChecked);
                        if (isChecked)
                        {
                            checkedIndexes.add(index);
                        }
                        else
                        {
                            checkedIndexes.remove(index);
                        }
                    }
                }
                else if (!isMulti)
                {
                    selectOption(box, false);
                }

                box.setOnCheckedChangeListener(this);
            }

            if (!warningsMap.isEmpty())
            {
                handleWarnings(getCurrentIndex());
            }
            if (!childMap.isEmpty())
            {
                handleChildren(getCurrentIndex());
            }
            if (updateListener != null)
            {
                updateListener
                        .onUpdate(BookingOptionsSelectView.this);
            }
        }
    };
}
