package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.InputFormDefinition;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.booking.util.OptionListToAttributeArrayConverter;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingConfirmationLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO TEMPORARILY SUPER HACKY, REFACTOR ASAP
 */
public final class BookingEntryInfoFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
    //TODO other fragments have these too. needs to be consolidated
    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";

    private List<EntryMethodOption> mEntryMethodOptions;

    private BookingOptionsSelectView mOptionsView;

    /**
     * map of input form machine name to input form fields
     *
     * TODO refactor this stuff
     */
    private Map<String, BasicInputTextView> mSelectedOptionInputFormFields = new HashMap<>();

    private boolean mIsNewUser;

    private Instructions mInstructions;
    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.fragment_booking_entry_info_bottom)
    LinearLayout mBottomView;
    @Bind(R.id.fragment_booking_entry_info_instructions_text)
    TextView mInstructionsText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private boolean mEntryMethodsInitialized = false;

    public static BookingEntryInfoFragment newInstance(
            final boolean isNewUser,
            final Instructions instructions,
            final EntryMethodsInfo entryMethodsInfo
    )
    {
        final BookingEntryInfoFragment fragment = new BookingEntryInfoFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_NEW_USER, isNewUser);
        args.putParcelable(EXTRA_INSTRUCTIONS, instructions);
        args.putSerializable(BookingFinalizeActivity.EXTRA_ENTRY_METHODS_INFO, entryMethodsInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIsNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mInstructions = getArguments().getParcelable(EXTRA_INSTRUCTIONS);

        bus.post(new LogEvent.AddLogEvent(new BookingConfirmationLog.BookingConfirmationShownLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAccessInformationShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_entry_info, container, false);

        ButterKnife.bind(this, view);

        //we don't allow the user to go back to the previous screen.
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.confirmation));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

        //TODO not being sent from server yet
//        EntryMethodsInfo entryMethodsInfo = (EntryMethodsInfo) getArguments()
//                .getSerializable(BookingFinalizeActivity.EXTRA_ENTRY_METHODS_INFO);
//        if(entryMethodsInfo != null)
//        {
//            initFromEntryMethodsInfo(entryMethodsInfo);
//            mNextButton.setOnClickListener(nextClicked);
//        }
//        else
//        {
//            Crashlytics.logException(new Exception("Entry methods info from bundle args is null"));
//            Toast.makeText(getContext(), R.string.default_error_string, Toast.LENGTH_SHORT).show();
//        }
        return view;
    }

    /**
     * updates the view and sets global vars based on the given QuoteConfig
     */
    private void initFromEntryMethodsInfo(@NonNull EntryMethodsInfo entryMethodsInfo)
    {
        //only need this to be global because of the options view updated listener
        mEntryMethodOptions = entryMethodsInfo.getEntryMethodOptions();

        mInstructionsText.setText(entryMethodsInfo.getInstructionText());
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

        mOptionsLayout.addView(mOptionsView, 0);
    }

    private void selectDefaultOption(
            @NonNull BookingOptionsSelectView optionsSelectView,
            @NonNull List<EntryMethodOption> entryMethodOptions)
    {
        for(int i = 0; i<entryMethodOptions.size(); i++)
        {
            if(entryMethodOptions.get(i).isDefault())
            {
                optionsSelectView.setCurrentIndex(i);
                return;
            }
        }
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        ((BaseActivity) getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        ((BaseActivity) getActivity()).setOnBackPressedListener(null);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    private boolean validateFields()
    {
        boolean areAllFieldsValid = true;
        for(String key : mSelectedOptionInputFormFields.keySet())
        {
            BasicInputTextView inputTextView = mSelectedOptionInputFormFields.get(key);
            if(!inputTextView.validate())
            //this also updates the field UI depending on whether value is valid so can't just return
            {
                areAllFieldsValid = false;
            }
        }
        return areAllFieldsValid;
    }

    @Override
    public final void onBack()
    {
        showBookings();
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAccessInformationDismissedLog()));
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (!validateFields() ||
                    bookingManager.getCurrentTransaction() == null)
                    /*
                    hot fix to prevent NPE caused by rapid multi-click
                    of the next button
                     */
            {
                return;
            }

            int selectedOptionIndex = mOptionsView.getCurrentIndex();
            EntryMethodOption entryMethodOption = mEntryMethodOptions.get(selectedOptionIndex);


            if(mSelectedOptionInputFormFields != null)
            {
                if(BookingInstruction.InstructionType.EntryMethod.LOCKBOX.equals(
                        entryMethodOption.getMachineName()))
                {
                    String lockboxAccessCode = mSelectedOptionInputFormFields.get(
                            InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_ACCESS_CODE).getInput();
                    String lockboxLocation = mSelectedOptionInputFormFields.get(
                            InputFormDefinition.InputFormField.SupportedMachineName.LOCKBOX_LOCATION).getInput();
                    bookingManager.getCurrentFinalizeBookingPayload().setEntryInfo(
                            entryMethodOption.getMachineName(),
                            lockboxAccessCode,
                            lockboxLocation //TODO not supporting structured data for now

                    );
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAccessInformationSubmittedLog(lockboxAccessCode+"\n" + lockboxLocation)));
                }
                else
                {
                    String entryDescription = mSelectedOptionInputFormFields.get(
                            InputFormDefinition.InputFormField.SupportedMachineName.DESCRIPTION).getInput();
                    //set info for non-lockbox options
                    bookingManager.getCurrentFinalizeBookingPayload().setEntryInfo(
                            entryMethodOption.getMachineName(),
                            entryDescription //TODO not supporting structured data for now

                    );
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAccessInformationSubmittedLog(entryDescription)));
                }

            }



            final Intent intent = new Intent(getActivity(), BookingFinalizeActivity.class);
            intent.putExtra(
                    BookingFinalizeActivity.EXTRA_PAGE,
                    BookingFinalizeActivity.PAGE_PREFERENCES
            );
            intent.putExtra(
                    BookingFinalizeActivity.EXTRA_NEW_USER,
                    mIsNewUser
            );
            intent.putExtra(BookingFinalizeActivity.EXTRA_INSTRUCTIONS, mInstructions);
            startActivity(intent);
        }
    };


    private void showBookings()
    {
        bookingManager.clearAll();

        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /**
     * TODO pre-generate the views
     * updates the input form based on the selected entry method option
     * @param entryMethodOption
     */
    private void updateViewForSelectedEntryMethodOption(@NonNull EntryMethodOption entryMethodOption)
    {
        mBottomView.removeAllViews();
        mSelectedOptionInputFormFields.clear();

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

            mSelectedOptionInputFormFields.put(inputFormField.getMachineName(), inputTextView);
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
            mNextButton.setOnClickListener(nextClicked);
            mEntryMethodsInitialized = true;
        }
    }
}
