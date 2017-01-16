package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.handybook.handybook.library.ui.view.FramedIconDrawable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

//TODO this desperately needs to be refactored
public final class BookingOptionsSelectView extends BookingOptionsIndexView
{
    protected String[] optionsSubtitles;
    protected String[] optionsSuperText;//todo rename
    protected String[] optionsRightSubText;
    protected String[] optionsRightTitleText;
    protected boolean[] optionsLeftStripIndicatorVisible;
    protected boolean[] optionsHidden;
    protected int[] optionImagesResourceIds;
    private HashMap<Integer, CheckBox> checkMap;
    private View optionViews[]; //need a reference to the option views so we can easily modify them later
    private int checkedIndex;
    private HashSet<Integer> checkedIndexes;
    private boolean isMulti;

    private int mOptionEntryLayoutResourceId = R.layout.view_booking_select_option;

    public BookingOptionsSelectView(
            final Context context,
            int optionEntryLayoutResourceId,
            final BookingOption option,
            final OnUpdatedListener updateListener
    )
    {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        mOptionEntryLayoutResourceId = optionEntryLayoutResourceId;
        init(context);
    }

    public BookingOptionsSelectView(
            final Context context, final BookingOption option,
            final OnUpdatedListener updateListener
    )
    {
        super(context, R.layout.view_booking_options_select, option, updateListener);
        init(context);
    }

    public BookingOptionsSelectView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingOptionsSelectView(final Context context, final AttributeSet attrs, final int defStyle)
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
    private boolean shouldDisplayOptionString(String[] optionsStrings, int optionIndex)
    {
        return optionsStrings != null &&
                optionIndex < optionsStrings.length &&
                !TextUtils.isEmpty(optionsStrings[optionIndex]);
    }

    private boolean isBooleanOptionValid(boolean[] options, int optionIndex)
    {
        return options != null && optionIndex < options.length;
    }

    private boolean shouldShowLeftStripIndicator(boolean[] shouldShowLeftStripIndicator, int optionIndex)
    {
        return isBooleanOptionValid(shouldShowLeftStripIndicator, optionIndex)
                && shouldShowLeftStripIndicator[optionIndex];
    }

    private boolean shouldHideOption(boolean[] optionsHidden, int optionIndex)
    {
        return isBooleanOptionValid(optionsHidden, optionIndex)
                && optionsHidden[optionIndex];
    }

    private boolean shouldDisableOption(boolean[] optionsDisabled, int optionIndex)
    {
        return isBooleanOptionValid(optionsDisabled, optionIndex)
                && optionsDisabled[optionIndex];
    }

    private void showTextView(TextView textView, String textString)
    {
        textView.setText(textString);
        textView.setVisibility(VISIBLE);
    }

    @SuppressWarnings("deprecation")
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

        optionsSuperText = option.getOptionsSuperText();
        optionsSubtitles = option.getOptionsSubText();
        optionsRightSubText = option.getOptionsRightSubText();
        optionsRightTitleText = option.getOptionsRightTitleText();
        optionImagesResourceIds = option.getImageResourceIds();
        optionsHidden = option.getOptionsHidden();
        optionsLeftStripIndicatorVisible = option.getLeftStripIndicatorVisible();
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

        optionViews = new View[optionsList.length];
        for (int i = 0; i < optionsList.length; i++)
        {
            final String optionText = optionsList[i];
            final ViewGroup optionView = (ViewGroup) LayoutInflater.from(context)
                    .inflate(mOptionEntryLayoutResourceId, this, false);

            final TextView title = (TextView) optionView.findViewById(R.id.title_text);
            title.setText(optionText);

            if (shouldDisplayOptionString(optionsSubtitles, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.sub_text), optionsSubtitles[i]);
            }

            if (shouldDisplayOptionString(optionsRightSubText, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.right_subtitle_text), optionsRightSubText[i]);
            }

            if (shouldDisplayOptionString(optionsRightTitleText, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.right_title_text), optionsRightTitleText[i]);
            }

            if (shouldDisplayOptionString(optionsSuperText, i))
            {
                showTextView((TextView) optionView.findViewById(R.id.booking_select_option_super_text), optionsSuperText[i]);
            }

            if (shouldShowLeftStripIndicator(optionsLeftStripIndicatorVisible, i))
            {
                optionView.findViewById(R.id.booking_select_option_left_strip_indicator).setVisibility(VISIBLE);
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
                    //If the box is disabled, then do nothing
                    if(!box.isEnabled())
                        return;

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

            if (shouldHideOption(optionsHidden, i))
            {
                optionView.setVisibility(GONE);
            }

            optionViews[i] = optionView;
            optionLayout.addView(optionView);
        }

        handleWarnings(getCurrentIndex());
        handleChildren(getCurrentIndex());
    }

    public final void showAllOptions()
    {
        if (optionViews == null)
        {
            //this shouldn't happen
            Crashlytics.logException(new Exception("Option view is null"));
            return;
        }
        for (int i = 0; i < optionViews.length; i++)
        {
            if (optionViews[i] == null)
            {
                //this shouldn't happen
                Crashlytics.logException(new Exception("Option view is null at index " + i));
            }
            else
            {
                optionViews[i].setVisibility(VISIBLE);
            }
        }
    }

    public final String getCurrentValue()
    {
        final CheckBox box = checkMap.get(checkedIndex);
        if(box == null)
            return null;

        final ViewGroup layout = (ViewGroup) box.getParent();
        final TextView title = (TextView) layout.findViewById(R.id.title_text);
        return title.getText().toString();
    }

    public final void setIsOptionEnabled(boolean isEnabled, int position)
    {
        CheckBox checkBox = checkMap.get(position);
        checkBox.setEnabled(isEnabled);

        //Mark as unchecked if disabled
        if(!isEnabled)
        {
            if(checkBox.isChecked())
            {
                checkedIndex = -1;
                checkBox.setOnCheckedChangeListener(null);
            }

            selectOption(checkBox, false);
        }
    }

    public final void updateRightOptionsTitleText(String text, int position)
    {
        showTextView((TextView) optionViews[position].findViewById(R.id.right_title_text), text);
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
        if (box.getBackground() instanceof FramedIconDrawable)
        {
            if (isChecked)
            {
                ((FramedIconDrawable) box.getBackground()).setColor(
                        ContextCompat.getColor(getContext(),
                                R.color.handy_blue));
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
