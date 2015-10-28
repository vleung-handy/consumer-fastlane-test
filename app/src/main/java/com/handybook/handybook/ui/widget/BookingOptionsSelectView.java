package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public final class BookingOptionsSelectView extends BookingOptionsIndexView
{
    protected String[] optionsSubtitles;
    protected String[] optionsRightSubText;
    protected String[] optionsRightTitleText;
    protected int[] optionImagesResourceIds;
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

    private boolean isArrayIndexValid(int[] array, int index)
    {
        return array != null &&
                index < array.length;
    }

    //TODO: is it possible to make this use isArrayIndexValid
    private boolean shouldDisplayOptionStringFromArray(String[] array, int index)
    {
        return array != null &&
                index < array.length &&
                array[index] != null && !array[index].isEmpty();
    }

    private void showTextView(TextView textView, String textString)
    {
        textView.setText(textString);
        textView.setVisibility(VISIBLE);
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
        optionsRightSubText = option.getOptionsRightSubText();
        optionsRightTitleText = option.getOptionsRightTitleText();
        optionImagesResourceIds = option.getImageResourceIds();
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

            if (shouldDisplayOptionStringFromArray(optionsSubtitles, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.sub_text), optionsSubtitles[i]);
            }

            if (shouldDisplayOptionStringFromArray(optionsRightSubText, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.right_subtitle_text), optionsRightSubText[i]);
            }

            if (shouldDisplayOptionStringFromArray(optionsRightTitleText, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.right_title_text), optionsRightTitleText[i]);
            }

            final CheckBox checkBox = (CheckBox) optionView.findViewById(R.id.check_box);
            if (isArrayIndexValid(optionImagesResourceIds, i) && optionImagesResourceIds[i] != 0)
            {
                //TODO: if this might be used elsewhere we should put into a function
                Drawable drawables[] = new Drawable[]
                        {
                                ContextCompat.getDrawable(context, R.drawable.option_circle_frame).mutate(),
                                ContextCompat.getDrawable(context, optionImagesResourceIds[i]).mutate()
                        };
                //have to make drawables mutable so that it won't share color filters with any other drawables

                LayerDrawable layerDrawable = new LayerDrawable(drawables);
                int inset = (int) getResources().getDimension(R.dimen.framed_icon_inset);
                layerDrawable.setLayerInset(1, inset, inset, inset, inset);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    checkBox.setBackground(layerDrawable);
                }
                else
                {
                    checkBox.setBackgroundDrawable(layerDrawable);
                }
            }
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
        if (index < 0 || !checkMap.containsKey(index))
        {
            Crashlytics.log("BookingOptionsSelectView::setCurrentIndex invalid index : " + index);
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

        for (final Integer i : indexes)
        {
            if (i == null)
            {
                continue;
            }
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
        final TextView rightSubtitleText = (TextView) layout.findViewById(R.id.right_subtitle_text);
        final TextView rightTitleText = (TextView) layout.findViewById(R.id.right_title_text);

        if (isChecked)
        {
            title.setTextColor(getResources().getColor(R.color.handy_text_black));
            subTitle.setTextColor(getResources().getColor(R.color.handy_blue));
            rightSubtitleText.setTextColor(getResources().getColor(R.color.handy_text_black));
            rightTitleText.setTextColor(getResources().getColor(R.color.handy_text_black));
            box.getBackground().setColorFilter(getResources().getColor(R.color.handy_blue), PorterDuff.Mode.SRC_IN);
            box.setChecked(true);
        }
        else
        {
            title.setTextColor(getResources().getColor(R.color.black_pressed));
            subTitle.setTextColor(getResources().getColor(R.color.black_pressed));
            rightSubtitleText.setTextColor(getResources().getColor(R.color.black_pressed));
            rightTitleText.setTextColor(getResources().getColor(R.color.black_pressed));
            box.getBackground().clearColorFilter();
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
