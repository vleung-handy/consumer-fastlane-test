package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.constant.BookingRecurrence;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.view.EntryMethodsInfoView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingConfirmationLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEntryInfoFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
    //TODO other fragments have these too. needs to be consolidated
    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";

    private boolean mIsNewUser;

    private Instructions mInstructions;
    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.next_button)
    Button mNextButton;

    @Bind(R.id.entry_method_input_view)
    EntryMethodsInfoView mEntryMethodsInfoView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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
                                       .inflate(
                                               R.layout.fragment_booking_entry_info,
                                               container,
                                               false
                                       );
        ButterKnife.bind(this, view);

        //we don't allow the user to go back to the previous screen.
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.confirmation));
        if (!mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }

        EntryMethodsInfo entryMethodsInfo = (EntryMethodsInfo) getArguments()
                .getSerializable(BookingFinalizeActivity.EXTRA_ENTRY_METHODS_INFO);
        if (entryMethodsInfo != null)
        {
            mEntryMethodsInfoView.updateViewForModel(entryMethodsInfo, getContext());
            onEntryMethodsViewUpdated(entryMethodsInfo);
            mNextButton.setOnClickListener(nextClicked);
        }
        else
        {
            //something has gone horribly wrong
            Crashlytics.logException(new Exception("Entry methods info from bundle args is null"));
            Toast.makeText(getContext(), R.string.default_error_string, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    /**
     * called when the entry methods view is updated with new data
     * <p>
     * currently just logs
     *
     * @param entryMethodsInfo
     */
    private void onEntryMethodsViewUpdated(@NonNull EntryMethodsInfo entryMethodsInfo)
    {
        //log entry method recommendation shown
        BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        if (bookingTransaction == null || entryMethodsInfo.getEntryMethodOptions() == null)
        {
            return;
        }
        for (EntryMethodOption entryMethodOption : entryMethodsInfo.getEntryMethodOptions())
        {
            /*
            in logging terms, whether an entry method is "recommended"
            is whether the entry method option subtitle is present
            ex. "Chosen by 13 of your neighbors"
             */
            if (!TextUtils.isEmpty(entryMethodOption.getSubtitleText()))
            {
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.EntryMethodLog.
                        RecommendationShown(
                        String.valueOf(bookingTransaction.getBookingId()),
                        bookingTransaction.getRecurringFrequency() != BookingRecurrence.ONE_TIME,
                        entryMethodOption.getMachineName()
                )));
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
            if (!mEntryMethodsInfoView.validateFields() ||
                    bookingManager.getCurrentTransaction() == null)
                    /*
                    hot fix to prevent NPE caused by rapid multi-click
                    of the next button
                     */
            {
                return;
            }

            Map<String, String> selectedEntryMethodInputFormValues = mEntryMethodsInfoView.getSelectedEntryMethodInputFormValues();
            EntryMethodOption selectedEntryMethodOption = mEntryMethodsInfoView.getSelectedEntryMethodOption();
            if (selectedEntryMethodOption != null)
            {
                bookingManager.getCurrentFinalizeBookingPayload().setEntryInfo(
                        selectedEntryMethodOption.getMachineName(),
                        selectedEntryMethodInputFormValues

                );

                //business expects the "submitted" event to be logged when the user clicks next
                BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
                if (bookingTransaction != null)
                {
                    bus.post(new LogEvent.AddLogEvent(
                            new BookingFunnelLog.EntryMethodLog.InfoSubmitted(
                                    String.valueOf(bookingTransaction.getBookingId()),
                                    bookingTransaction.getRecurringFrequency() != BookingRecurrence.ONE_TIME,
                                    selectedEntryMethodOption.getMachineName()
                            )));
                }


                bus.post(new LogEvent.AddLogEvent(
                        new BookingFunnelLog.BookingAccessInformationSubmittedLog(
                                selectedEntryMethodOption.getMachineName())));
            }

            //else - no option selected. allow user to proceed anyway (same as iOS behavior)

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
}
