package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.booking.bookingedit.model.BookingEditEntryInformationRequest;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.EntryMethodsInfoView;
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
    EntryMethodsInfoView mEntryMethodsInfoView;

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

    /**
     * requests for the available entry methods info for the booking associated with the given id
     * <p/>
     * updates the view on callback success
     */
    private void requestAvailableEntryMethodsInfo(String bookingId)
    {
        showUiBlockers();
        mBookingEditManager.getEntryMethodsInfo(
                bookingId,
                new DataManager.Callback<EntryMethodsInfo>()
                {

                    @Override
                    public void onSuccess(final EntryMethodsInfo response)
                    {
                        removeUiBlockers();
                        mEntryMethodsInfoView.updateViewForModel(response, getContext());
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
        requestAvailableEntryMethodsInfo(booking.getId());
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        if (!mEntryMethodsInfoView.validateFields())
        {
            return;
        }

        //create the object to send to the server based on the selected entry method + input fields
        EntryMethodOption selectedEntryMethodOption = mEntryMethodsInfoView.getSelectedEntryMethodOption();

        if (selectedEntryMethodOption == null) { return; }

        //create the request object
        List<BookingInstruction> bookingInstructions = getBookingInstructionsListFromInput(
                selectedEntryMethodOption.getMachineName(),
                mEntryMethodsInfoView.getSelectedEntryMethodInputFormValues()
        );
        BookingEditEntryInformationRequest editEntryInformationTransaction =
                new BookingEditEntryInformationRequest(bookingInstructions, true);

        requestUpdateEntryMethodsInfo(booking.getId(), editEntryInformationTransaction);
    }

    /**
     * request to update the entry methods info of the booking associated with the given id
     *
     * @param bookingId
     * @param editEntryInformationRequest
     */
    private void requestUpdateEntryMethodsInfo(
            String bookingId,
            BookingEditEntryInformationRequest editEntryInformationRequest
    )
    {
        showUiBlockers();
        mBookingEditManager.updateEntryMethodsInfo(
                bookingId,
                editEntryInformationRequest,
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
     * creates a booking instructions list from the given
     * selected entry method input form values
     * and selected entry method machine name
     *
     * @param selectedEntryMethodMachineName
     * @param selectedEntryMethodInputFormValues
     * @return
     */
    private List<BookingInstruction> getBookingInstructionsListFromInput(
            @BookingInstruction.EntryMethodType String selectedEntryMethodMachineName,
            @NonNull Map<String, String> selectedEntryMethodInputFormValues
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

        //create booking instructions from the selected entry method input form values
        for (String inputFormFieldMachineName : selectedEntryMethodInputFormValues.keySet())
        {
            BookingInstruction entryInfoInstruction = new BookingInstruction(
                    null,
                    inputFormFieldMachineName,
                    null,
                    null,
                    selectedEntryMethodInputFormValues.get(inputFormFieldMachineName),
                    null
            );
            bookingInstructionList.add(entryInfoInstruction);
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
