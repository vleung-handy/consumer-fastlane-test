package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.os.Build;
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
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.ui.widget.FramedIconDrawable;

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

    private int mOptionEntryLayoutResourceId = R.layout.view_booking_select_option;

    public BookingOptionsSelectView(final Context context, int optionEntryLayoutResourceId, final
                                    BookingOption option,
                                    final OnUpdatedListener updateListener)
    {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        mOptionEntryLayoutResourceId = optionEntryLayoutResourceId;
        init(context);
    }

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

    //TODO: put these in a util
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
                    .inflate(mOptionEntryLayoutResourceId, this, false);

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
                int inset = (int) context.getResources().getDimension(R.dimen.framed_icon_inset);
                FramedIconDrawable framedIconDrawable = new FramedIconDrawable(
                        getContext(),
                        R.drawable.option_circle_frame,
                        optionImagesResourceIds[i],
                        inset
                );

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    checkBox.setBackground(framedIconDrawable);
                }
                else
                {
                    checkBox.setBackgroundDrawable(framedIconDrawable);
                }
            }
            if (!isMulti && i == checkedIndex)
            {
                selectOption(checkBox, true);
            }
            checkBox.setOnCheckedChangeListener(checkedChanged);
            checkMap.put(i, checkBox);

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

        if (mainLayout != null)
        {
            mainLayout.setPadding(mainLayout.getPaddingLeft(), 0, mainLayout.getPaddingRight(), mainLayout.getPaddingBottom());
        }

        invalidate();
        requestLayout();
    }

    private void updateCheckbox(final CheckBox box, final boolean isChecked)
    {
        //don't want filter applied to drawables that aren't mutable
        //because the filter will be retained throughout the app
        //TODO: need a better to determine and ensure checkbox drawable is mutable so we can apply filters to it
        //TODO: should we always use Drawable.mutate() on the checkbox drawables?
        if(box.getBackground() instanceof FramedIconDrawable)
        {
            if(isChecked)
            {
                ((FramedIconDrawable) box.getBackground()).setColor(getResources().getColor(R.color.handy_blue));
            }
            else
            {
                ((FramedIconDrawable) box.getBackground()).clearColor();
            }
        }
        box.setChecked(isChecked);
    }

    private void selectOption(final CheckBox box, final boolean isChecked)
    {
        final ViewGroup layout = (ViewGroup) box.getParent();
        layout.dispatchSetSelected(isChecked);

        //update the check box (uses custom drawable)
        updateCheckbox(box, isChecked);
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