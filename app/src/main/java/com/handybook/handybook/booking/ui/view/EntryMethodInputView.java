package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.InputFormDefinition;
import com.handybook.handybook.booking.util.OptionListToAttributeArrayConverter;
import com.handybook.handybook.ui.widget.BasicInputTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * view for the EntryMethodsInfo model
 */
public final class EntryMethodInputView extends LinearLayout
{
    @Bind(R.id.booking_entry_info_options_layout)
    LinearLayout mEntryInfoOptionsLayout;
    @Bind(R.id.booking_entry_info_instructions)
    TextView mEntryInfoInstructions;
    @Bind(R.id.booking_entry_info_input_form_layout)
    LinearLayout mEntryInfoInputFormLayout;


    private Map<String, BasicInputTextView> mSelectedOptionInputFieldMachineNameToViewMap = new HashMap<>();
    private List<EntryMethodOption> mEntryMethodOptions;
    private BookingOptionsSelectView mOptionsView;
    private BookingOptionsView.OnUpdatedListener mOptionUpdatedListener = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            EntryMethodOption entryMethodOption = getSelectedEntryMethodOption();
            if (entryMethodOption == null)
            {
                Crashlytics.logException(new Exception(
                        "Selected entry method option is null on option updated"));
                return;
            }
            updateViewForSelectedEntryMethodOption(entryMethodOption);
        }

        @Override
        public void onShowChildren(
                final BookingOptionsView view,
                final String[] items
        )
        {
        }

        @Override
        public void onHideChildren(
                final BookingOptionsView view,
                final String[] items
        )
        {
        }
    };

    public EntryMethodInputView(final Context context)
    {
        super(context);
        init();
    }

    public EntryMethodInputView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EntryMethodInputView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_entry_info, this);
        ButterKnife.bind(this);
    }

    public void updateViewForModel(
            @NonNull EntryMethodsInfo entryMethodsInfo,
            @NonNull Context context
    )
    {
        //only need this to be global because of the options view updated listener
        mEntryMethodOptions = entryMethodsInfo.getEntryMethodOptions();

        mEntryInfoInstructions.setText(entryMethodsInfo.getInstructionText());
        /*
        build a BookingOption model from the recurrence options so we can use it
        to create an options view
         */
        final BookingOption option = getBookingOptionModel(mEntryMethodOptions);
        mOptionsView
                = new BookingOptionsSelectView(context, option, mOptionUpdatedListener);
        mOptionsView.hideTitle();

        //set default selected option
        selectDefaultOption(mOptionsView, entryMethodsInfo);

        mEntryInfoOptionsLayout.removeAllViews();
        mEntryInfoOptionsLayout.addView(mOptionsView, 0);
    }

    private void selectDefaultOption(
            @NonNull BookingOptionsSelectView optionsSelectView,
            @NonNull EntryMethodsInfo entryMethodsInfo
    )
    {
        String defaultOptionMachineName = entryMethodsInfo.getSelectedOptionMachineName();
        if (defaultOptionMachineName == null) { return; }
        List<EntryMethodOption> entryMethodOptions = entryMethodsInfo.getEntryMethodOptions();
        for (int i = 0; i < entryMethodOptions.size(); i++)
        {
            if (defaultOptionMachineName.equals(entryMethodOptions.get(i).getMachineName()))
            {
                optionsSelectView.setCurrentIndex(i);
                return;
            }
        }
    }

    /**
     * @return a map of input form machine names to their values
     */
    @Nullable
    public Map<String, String> getInputFormValues()
    {
        if (mSelectedOptionInputFieldMachineNameToViewMap == null) { return null; }
        Map<String, String> inputFormValues = new HashMap<>();
        for (String s : mSelectedOptionInputFieldMachineNameToViewMap.keySet())
        {
            BasicInputTextView inputTextView = mSelectedOptionInputFieldMachineNameToViewMap.get(s);
            if (inputTextView == null)
            {
                continue; //this should never happen
            }
            inputFormValues.put(s, inputTextView.getInput());
        }
        return inputFormValues;
    }

    @Nullable
    public EntryMethodOption getSelectedEntryMethodOption()
    {
        int selectedOptionIndex = mOptionsView.getCurrentIndex();
        if (selectedOptionIndex < 0) { return null; }
        return mEntryMethodOptions.get(selectedOptionIndex);
    }

    public boolean validateFields()
    {
        //options don't necessarily have input fields
        if (mSelectedOptionInputFieldMachineNameToViewMap == null) { return true; }
        boolean areAllFieldsValid = true;
        for (String key : mSelectedOptionInputFieldMachineNameToViewMap.keySet())
        {
            BasicInputTextView inputTextView = mSelectedOptionInputFieldMachineNameToViewMap.get(key);
            if (!inputTextView.validate())
            //this also updates the field UI depending on whether value is valid so can't just return
            {
                areAllFieldsValid = false;
            }
        }
        return areAllFieldsValid;
    }

    @NonNull
    private BookingOption getBookingOptionModel(
            @NonNull List<EntryMethodOption> entryMethodOptions
    )
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(OptionListToAttributeArrayConverter.getOptionsTitleTextArray(
                entryMethodOptions));
        option.setOptionsSubText(OptionListToAttributeArrayConverter.getOptionsSubTextArray(
                entryMethodOptions));
        option.setDefaultValue("-1");
        //^this makes it default to nothing.
        //for some reason the model expects string but parses this into an int

        //UI for recommended options
        //TODO hacky, refactor asap
        if (!entryMethodOptions.isEmpty())
        {
            EntryMethodOption lastOption = entryMethodOptions.get(entryMethodOptions.size() - 1);
            if (BookingInstruction.InstructionType.EntryMethod.AT_HOME.equals(lastOption.getMachineName()))
            {
                //set "Recommended" as the super text for the first option
                String[] superTextArray = new String[entryMethodOptions.size()];
                superTextArray[0] = getResources().getString(R.string.recommended);
                option.setOptionsSuperText(superTextArray);
            }
        }
        option.setLeftStripIndicatorVisible(getRecommendedArray(entryMethodOptions));
        return option;
    }

    private boolean[] getRecommendedArray(List<EntryMethodOption> entryMethodOptions)
    {
        boolean[] recommended = new boolean[entryMethodOptions.size()];
        for (int i = 0; i < recommended.length; i++)
        {
            recommended[i] = entryMethodOptions.get(i).isRecommended();
        }
        return recommended;
    }

    /**
     * TODO pre-generate the views updates the input form based on the selected entry method option
     *
     * @param entryMethodOption
     */
    private void updateViewForSelectedEntryMethodOption(@NonNull EntryMethodOption entryMethodOption)
    {
        mEntryInfoInputFormLayout.removeAllViews();
        mSelectedOptionInputFieldMachineNameToViewMap.clear();

        //update the input form
        InputFormDefinition inputFormDefinition = entryMethodOption.getInputFormDefinition();
        if (inputFormDefinition == null) { return; }

        List<InputFormDefinition.InputFormField> inputFormFields =
                inputFormDefinition.getFieldDefinitions();
        for (InputFormDefinition.InputFormField inputFormField : inputFormFields)
        {
            BasicInputTextView inputTextView =
                    (BasicInputTextView) LayoutInflater.from(getContext())
                                                       .inflate(
                                                               R.layout.element_text_input_view,
                                                               null
                                                       );
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.default_margin_quarter);

            if (inputFormField.isRequired())
            {
                //TODO don't like using this input view's method but using it for now
                inputTextView.setMinLength(1);
            }
            inputTextView.setHint(inputFormField.getHintText());
            inputTextView.setLines(inputFormField.getNumLines());
            inputTextView.setText(inputFormField.getValue());
            mEntryInfoInputFormLayout.addView(inputTextView, layoutParams);

            mSelectedOptionInputFieldMachineNameToViewMap.put(
                    inputFormField.getMachineName(),
                    inputTextView
            );
        }


    }
}
