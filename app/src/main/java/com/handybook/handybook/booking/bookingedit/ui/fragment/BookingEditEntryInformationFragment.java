package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.booking.bookingedit.model.BookingEditEntryInformationTransaction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.EntryMethodInputView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditEntryInformationFragment extends BookingFlowFragment
{
    private Booking booking;
    @Bind(R.id.next_button)
    Button nextButton;

    @Bind(R.id.entry_method_input_view)
    EntryMethodInputView mEntryMethodInputView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    BookingEditManager mBookingEditManager;

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
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_edit_entry_info,
                                               container,
                                               false
                                       );
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.entry_info));

        return view;
    }

    private void requestAvailableEntryMethodsInfo()
    {
        showUiBlockers();
        mBookingEditManager.getEntryMethodsInfo(
                booking.getId(),
                new DataManager.Callback<EntryMethodsInfo>()
                {

                    @Override
                    public void onSuccess(final EntryMethodsInfo response)
                    {
                        removeUiBlockers();
                        mEntryMethodInputView.updateViewForModel(response, getContext());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        removeUiBlockers();
                        showToast(R.string.default_error_string);
                    }
                }
        );
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        requestAvailableEntryMethodsInfo();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        if (!mEntryMethodInputView.validateFields())
        {
            return;
        }

        //create the object to send to the server based on the selected entry method + input fields
        EntryMethodOption selectedEntryMethodOption = mEntryMethodInputView.getSelectedEntryMethodOption();

        if (selectedEntryMethodOption == null) { return; }
        List<BookingInstruction> bookingInstructions = getBookingInstructionsListFromInput(
                selectedEntryMethodOption.getMachineName(),
                mEntryMethodInputView.getInputFormValues()
        );
        BookingEditEntryInformationTransaction editEntryInformationTransaction =
                new BookingEditEntryInformationTransaction(bookingInstructions, true);

        requestUpdateEntryMethodsInfo(booking.getId(), editEntryInformationTransaction);
    }

    private void requestUpdateEntryMethodsInfo(
            String bookingId,
            BookingEditEntryInformationTransaction editEntryInformationTransaction
    )
    {
        showUiBlockers();
        mBookingEditManager.updateEntryMethodsInfo(
                bookingId,
                editEntryInformationTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        removeUiBlockers();
                        showToast(R.string.updated_entry_information);
                        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
                        getActivity().finish();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {

                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    /**
     * converts the input form values map and selected entry method type to a booking instructions
     * list
     *
     * @param selectedEntryMethodMachineName
     * @param inputFormValues
     * @return
     */
    private List<BookingInstruction> getBookingInstructionsListFromInput(
            @BookingInstruction.EntryMethodType String selectedEntryMethodMachineName,
            Map<String, String> inputFormValues
    )
    {
        BookingInstruction entryInfoTypeInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.ENTRY_METHOD,
                null,
                selectedEntryMethodMachineName,
                null,
                null
        );
        List<BookingInstruction> bookingInstructionList = new ArrayList<>();
        bookingInstructionList.add(entryInfoTypeInstruction);

        if (inputFormValues != null)
        {
            for (String inputFormFieldMachineName : inputFormValues.keySet())
            {
                BookingInstruction entryInfoInstruction = new BookingInstruction(
                        null,
                        inputFormFieldMachineName,
                        null,
                        null,
                        inputFormValues.get(inputFormFieldMachineName),
                        null
                );
                bookingInstructionList.add(entryInfoInstruction);
            }
        }

        return bookingInstructionList;
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
}
