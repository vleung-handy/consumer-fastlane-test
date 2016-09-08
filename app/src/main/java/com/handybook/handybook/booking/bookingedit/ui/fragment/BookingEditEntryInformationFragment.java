package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.InputFormDefinition;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.booking.util.OptionListToAttributeArrayConverter;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * TODO TEMPORARILY SUPER HACKY, REFACTOR ASAP
 */
public final class BookingEditEntryInformationFragment extends BookingFlowFragment
{
    private BookingUpdateEntryInformationTransaction entryInformationTransaction;

    private Booking booking;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    TextView headerText;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.fragment_booking_entry_info_bottom)
    LinearLayout mBottomView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private boolean mEntryMethodsInitialized = false;

    /**
     * map of input form machine name to input form fields
     *
     * TODO refactor this stuff
     */
    private Map<String, BasicInputTextView> mSelectedOptionMachineNameToInputFieldMap = new HashMap<>();


    public static BookingEditEntryInformationFragment newInstance(final Booking booking)
    {
        final BookingEditEntryInformationFragment fragment = new BookingEditEntryInformationFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        initTransaction();
    }

    private void initTransaction()
    {
        entryInformationTransaction = new BookingUpdateEntryInformationTransaction();
        entryInformationTransaction.setGetInId(booking.getEntryType());
        entryInformationTransaction.setGetInText(booking.getExtraEntryInfo());

        /*
        NOTE: there's currently no UI to ask whether it should be applied to all
        so defaulting this to true
         */
        entryInformationTransaction.setApplyToAllInSeries(true);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_entry_info, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.entry_info));

        initHeader();

        return view;
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        if (!validateFields())
        {
            return;
        }

        int selectedOptionIndex = mOptionsView.getCurrentIndex();
        if(selectedOptionIndex < 0) return;

        //create the object to send to the server based on the selected entry method + input fields
        EntryMethodOption selectedEntryMethodOption = mEntryMethodOptions.get(selectedOptionIndex);

        int getInId = EntryMethodOption.getEntryMethodGetInIdForMachineName(selectedEntryMethodOption.getMachineName());
        entryInformationTransaction.setGetInId(getInId);
        entryInformationTransaction.setLockboxAccessCode("");

        if(mSelectedOptionMachineNameToInputFieldMap != null)
        {
            if(BookingInstruction.InstructionType.EntryMethod.LOCKBOX.equals(
                    selectedEntryMethodOption.getMachineName()))
            {
                //TODO HACKY: getintext = lockbox location, lockbox_code = lockbox access code

                String lockboxAccessCode = mSelectedOptionMachineNameToInputFieldMap.get(
                        InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_ACCESS_CODE).getInput();
                String lockboxLocation = mSelectedOptionMachineNameToInputFieldMap.get(
                        InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_LOCATION).getInput();

                entryInformationTransaction.setGetInText(lockboxLocation);
                entryInformationTransaction.setLockboxAccessCode(lockboxAccessCode);
                //set lockbox info
            }
            else
            {
                String entryDescription = mSelectedOptionMachineNameToInputFieldMap.get(
                        InputFormDefinition.InputFormField.SupportedMachineName.ADDITIONAL_INSTRUCTIONS).getInput();
                entryInformationTransaction.setGetInText(entryDescription);
                //set info for non-lockbox options
            }
        }


        disableInputs();
        progressDialog.show();
        int bookingId = Integer.parseInt(booking.getId());
        bus.post(new BookingEditEvent.RequestUpdateBookingEntryInformation(bookingId, entryInformationTransaction));
    }

    private void initHeader()
    {
        //todo why is this needed?
        final String text = getString(R.string.pro_entry_information);
        final SpannableString spanText = new SpannableString(text);
        headerText.setText(spanText, TextView.BufferType.SPANNABLE);
    }


    private List<EntryMethodOption> mEntryMethodOptions;

    private BookingOptionsSelectView mOptionsView;

    /**
     * updates the view and sets global vars based on the given QuoteConfig
     */
    private void initFromEntryMethodsInfo(@NonNull EntryMethodsInfo entryMethodsInfo)
    {
        //only need this to be global because of the options view updated listener
        mEntryMethodOptions = entryMethodsInfo.getEntryMethodOptions();

        /*
        build a BookingOption model from the recurrence options so we can use it
        to create an options view
         */
        final BookingOption option = getBookingOptionModel(mEntryMethodOptions);
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        mOptionsView.hideTitle();

        //set default selected option
        selectDefaultOption(mOptionsView, mEntryMethodOptions);

        optionsLayout.addView(mOptionsView, 0);
    }

    private void selectDefaultOption(
            @NonNull BookingOptionsSelectView optionsSelectView,
            @NonNull List<EntryMethodOption> entryMethodOptions)
    {
        int bookingEntryMethodId = booking.getEntryType();
        String bookingEntryMethodMachineName =
                EntryMethodOption.getEntryMethodMachineNameForGetInId(bookingEntryMethodId);

        if(bookingEntryMethodMachineName == null) return;

        for(int i = 0; i<entryMethodOptions.size(); i++)
        {
            //select the entry method as in the booking
            if(bookingEntryMethodMachineName.equals(entryMethodOptions.get(i).getMachineName()))
            {
                optionsSelectView.setCurrentIndex(i);
                return;
            }
        }
    }

    private boolean validateFields()
    {
        if(mSelectedOptionMachineNameToInputFieldMap == null) return false;
        boolean areAllFieldsValid = true;
        for(String key : mSelectedOptionMachineNameToInputFieldMap.keySet())
        {
            BasicInputTextView inputTextView = mSelectedOptionMachineNameToInputFieldMap.get(key);
            if(!inputTextView.validate())
            //this also updates the field UI depending on whether value is valid so can't just return
            {
                areAllFieldsValid = false;
            }
        }
        return areAllFieldsValid;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @Subscribe
    public final void onReceiveUpdateBookingEntryInformationSuccess(BookingEditEvent.ReceiveUpdateBookingEntryInformationSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();
        showToast(R.string.updated_entry_information);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingEntryInformationError(BookingEditEvent.ReceiveUpdateBookingEntryInformationError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated;

    {
        optionUpdated = new BookingOptionsView.OnUpdatedListener()
        {
            @Override
            public void onUpdate(final BookingOptionsView view)
            {
                final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
                EntryMethodOption entryMethodOption = mEntryMethodOptions.get(index);
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
    }


    //TODO pre-generate the views
    /**
     * updates the input form based on the selected entry method option
     * @param entryMethodOption
     */
    private void updateViewForSelectedEntryMethodOption(@NonNull EntryMethodOption entryMethodOption)
    {
        mBottomView.removeAllViews();
        mSelectedOptionMachineNameToInputFieldMap.clear();

        //update the input form
        InputFormDefinition inputFormDefinition = entryMethodOption.getInputFormDefinition();
        if(inputFormDefinition == null) return;

        List<InputFormDefinition.InputFormField> inputFormFields =
                inputFormDefinition.getFieldDefinitions();
        for(InputFormDefinition.InputFormField inputFormField : inputFormFields)
        {
            BasicInputTextView inputTextView =
                    (BasicInputTextView) LayoutInflater.from(getContext()).inflate(R.layout.element_text_input_view, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.default_margin_quarter);

            if(inputFormField.isRequired())
            {
                //TODO don't like using this input view's method but using it for now
                inputTextView.setMinLength(1);
            }
            inputTextView.setHint(inputFormField.getHintText());
            inputTextView.setLines(inputFormField.getNumLines());
            mBottomView.addView(inputTextView, layoutParams);

            mSelectedOptionMachineNameToInputFieldMap.put(inputFormField.getMachineName(), inputTextView);
        }

        //populate with previously specified values

        int selectedOptionIndex = mOptionsView.getCurrentIndex();
        EntryMethodOption selectedEntryMethodOption = mEntryMethodOptions.get(selectedOptionIndex);

        int selectedGetInId = EntryMethodOption.getEntryMethodGetInIdForMachineName(
                selectedEntryMethodOption.getMachineName());

        //if selection is equal to what it was before
        if(selectedGetInId == booking.getEntryType())
        {
            if(BookingInstruction.InstructionType.EntryMethod.LOCKBOX.equals(
                    selectedEntryMethodOption.getMachineName()))
            {
                BasicInputTextView textView = mSelectedOptionMachineNameToInputFieldMap.get(InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_LOCATION);
                BasicInputTextView lockboxCodeTextView =
                        mSelectedOptionMachineNameToInputFieldMap.get(
                                InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_ACCESS_CODE);
                lockboxCodeTextView.setText(booking.getLockboxCode());
                textView.setText(booking.getExtraEntryInfo());

            }
            else
            {
                BasicInputTextView textView = mSelectedOptionMachineNameToInputFieldMap.get(InputFormDefinition.InputFormField.SupportedMachineName.ADDITIONAL_INSTRUCTIONS);
                textView.setText(booking.getExtraEntryInfo());

            }
        }
    }

    private boolean[] getRecommendedArray(List<EntryMethodOption> entryMethodOptions)
    {
        boolean[] recommended = new boolean[entryMethodOptions.size()];
        for(int i = 0; i<recommended.length; i++)
        {
            recommended[i] = entryMethodOptions.get(i).isRecommended();
        }
        return recommended;
    }


    @NonNull
    private BookingOption getBookingOptionModel(
            @NonNull List<EntryMethodOption> entryMethodOptions)
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(OptionListToAttributeArrayConverter.getOptionsTitleTextArray(entryMethodOptions));
        option.setOptionsSubText(OptionListToAttributeArrayConverter.getOptionsSubTextArray(entryMethodOptions));
        option.setDefaultValue("-1");
        //^this makes it default to nothing.
        //for some reason the model expects string but parses this into an int

        //UI for recommended options
        //TODO hacky, refactor asap
        if(!entryMethodOptions.isEmpty())
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

    @Override
    public void onResume()
    {
        super.onResume();
        if(!mEntryMethodsInitialized)
        {
            bus.post(new ConfigurationEvent.RequestConfiguration());
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(final ConfigurationEvent.ReceiveConfigurationSuccess event)
    {
        //todo if not initialized yet
        if(!mEntryMethodsInitialized)
        {
            EntryMethodsInfo entryMethodsInfo =
                    EntryMethodsInfo.getEntryMethodInfo_HACK(event.getConfiguration(), getContext());
            initFromEntryMethodsInfo(entryMethodsInfo);
            mEntryMethodsInitialized = true;
        }
    }
}
