package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.RecurrenceOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.booking.util.OptionListToAttributeArrayConverter;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingRecurrenceFragment extends BookingFlowFragment {

    private BookingTransaction bookingTransaction;
    private List<RecurrenceOption> mRecurrenceOptions;
    private BookingOptionsSelectView mOptionsView;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.fragment_booking_recurrence_show_more_options_button)
    View mShowMoreOptionsButton;
    @Bind(R.id.fragment_booking_recurrence_disclaimer_text)
    TextView mDisclaimerText;

    public static BookingRecurrenceFragment newInstance(@Nullable final Bundle extras) {
        BookingRecurrenceFragment fragment = new BookingRecurrenceFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_recurrence,
                                               container,
                                               false
                                       );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.how_often));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        /*
        not adding null check because i want the app to crash if booking manager
        does not have a quote or quote config or recurrence options,
        because the user will not be able to proceed anyway
         */
        BookingQuote.QuoteConfig quoteConfig = bookingManager.getCurrentQuote().getQuoteConfig();
        initFromQuoteConfig(quoteConfig);

        nextButton.setOnClickListener(nextClicked);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(40);
        return view;
    }

    /**
     * updates the view and sets global vars based on the given QuoteConfig
     *
     * @param quoteConfig
     */
    private void initFromQuoteConfig(BookingQuote.QuoteConfig quoteConfig) {
        //only need this to be global because of the options view updated listener
        mRecurrenceOptions = quoteConfig.getRecurrenceOptions();

        /*
        build a BookingOption model from the recurrence options so we can use it
        to create an options view
         */
        final BookingOption option = getBookingOptionModel(mRecurrenceOptions);
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        mOptionsView.hideTitle();

        //set default selected option
        selectDefaultOption(mOptionsView, mRecurrenceOptions);

        //set disclaimer text
        mDisclaimerText.setText(quoteConfig.getDisclaimerText());

        //update the show more options button hidden state
        mShowMoreOptionsButton.setVisibility(
                hasHiddenOption(mRecurrenceOptions) ? View.VISIBLE : View.GONE);

        optionsLayout.addView(mOptionsView, 0);
    }

    /**
     * select the default recurrence option,
     * based on the "default" attribute of the
     * items in the recurrence options list
     *
     * @param optionsSelectView
     * @param recurrenceOptions
     */
    private void selectDefaultOption(
            @NonNull BookingOptionsSelectView optionsSelectView,
            @NonNull List<RecurrenceOption> recurrenceOptions
    ) {
        for (int i = 0; i < recurrenceOptions.size(); i++) {
            if (recurrenceOptions.get(i).isDefault()) {
                optionsSelectView.setCurrentIndex(i);
                return;
            }
        }
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            continueBookingFlow();
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            RecurrenceOption recurrenceOption = mRecurrenceOptions.get(index);
            bookingTransaction.setRecurringFrequency(
                    recurrenceOption.getFrequencyValue());

        }

        @Override
        public void onShowChildren(
                final BookingOptionsView view,
                final String[] items
        ) {
        }

        @Override
        public void onHideChildren(
                final BookingOptionsView view,
                final String[] items
        ) {
        }
    };

    /**
     * this button should only show if there are hidden recurrence options
     * <p/>
     * clicking it will show all the options and hide this button
     */
    @OnClick(R.id.fragment_booking_recurrence_show_more_options_button)
    public void onShowMoreOptionsButtonClicked() {
        mOptionsView.showAllOptions();
        mShowMoreOptionsButton.setVisibility(View.GONE);
    }

    /**
     * determines whether any of the options should be hidden
     * currently used to determine whether we should display the "show more options" button
     * @param recurrenceOptions
     * @return
     */
    private boolean hasHiddenOption(
            @NonNull List<RecurrenceOption> recurrenceOptions
    ) {
        for (RecurrenceOption recurrenceOption : recurrenceOptions) {
            if (recurrenceOption.isHidden()) {
                return true;
            }
        }
        return false;
    }

    /*
    unfortunately we have to build arrays from recurrence options,
    each of just a particular options attribute,
    because of the way the BookingOption model is structured
     */

    @NonNull
    private String[] getSavingsInfoArray(
            @NonNull List<RecurrenceOption> recurrenceOptions
    ) {
        final String[] info = new String[recurrenceOptions.size()];
        for (int i = 0; i < info.length; i++) {
            info[i] = recurrenceOptions.get(i).getPriceInfoText();
        }
        return info;
    }

    @NonNull
    private boolean[] getOptionsHiddenArray(
            @NonNull List<RecurrenceOption> recurrenceOptions
    ) {
        boolean[] optionsHidden = new boolean[recurrenceOptions.size()];
        for (int i = 0; i < optionsHidden.length; i++) {
            optionsHidden[i] = recurrenceOptions.get(i).isHidden();
        }
        return optionsHidden;
    }

    @NonNull
    private BookingOption getBookingOptionModel(
            @NonNull List<RecurrenceOption> recurrenceOptions
    ) {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(OptionListToAttributeArrayConverter.getOptionsTitleTextArray(
                recurrenceOptions));
        option.setOptionsSubText(OptionListToAttributeArrayConverter.getOptionsSubTextArray(
                recurrenceOptions));
        option.setOptionsRightSubText(getSavingsInfoArray(recurrenceOptions));
        option.setOptionsHidden(getOptionsHiddenArray(recurrenceOptions));
        return option;
    }
}
