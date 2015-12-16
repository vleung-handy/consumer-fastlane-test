package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.booking.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.ui.widget.LabelValueView;
import com.handybook.handybook.booking.viewmodel.BookingEditExtrasViewModel;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditExtrasFragment extends BookingFlowFragment
{
    //TODO: use ViewModel
    @Bind(R.id.booking_edit_extras_content_container)
    ScrollView mContentContainer;
    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.booking_edit_extras_booking_label_row)
    LabelValueView mBookingTableRow;
    @Bind(R.id.booking_edit_extras_booking_duration_text)
    TextView mBookingDurationText;
    @Bind(R.id.booking_edit_extras_total_due_text)
    TextView mTotalDueText;
    @Bind(R.id.booking_edit_extras_billed_on_text)
    TextView mBilledOnText;
    @Bind(R.id.booking_extras_price_table)
    LinearLayout mBookingExtrasPriceTableLayout;
    @Bind(R.id.next_button)
    Button mSaveButton;

    private Booking mBooking;

    private BookingEditExtrasViewModel mBookingEditExtrasViewModel;
    private BookingOptionsSelectView mOptionsView;

    public static BookingEditExtrasFragment newInstance(Booking booking)
    {
        final BookingEditExtrasFragment fragment = new BookingEditExtrasFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mixpanel.trackEventAppTrackExtras();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HandyEvent.RequestEditBookingExtrasViewModel(
                Integer.parseInt(mBooking.getId())));
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_extras, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.next_button)
    public void onSaveButtonPressed()
    {
        //api expects 2 string arrays of service machine names
        //one of added extras and one of removed extras, relative to the original booking
        List<String> addedExtras = new LinkedList<>();
        List<String> removedExtras = new LinkedList<>();

        //build a set of options that user has picked
        //so that we can check in constant time if a particular option was picked
        Set<Integer> selectedOptionIndexes = new HashSet<>();
        for (int i = 0; i < mOptionsView.getCheckedIndexes().length; i++)
        {
            selectedOptionIndexes.add(mOptionsView.getCheckedIndexes()[i]);
        }

        //build a set of extras that the original booking contained
        //so that we can check in constant time if a particular extra was already in the original booking
        Set<String> bookingExtras = new HashSet<>();
        for (int i = 0; i < mBooking.getExtrasInfo().size(); i++)
        {
            bookingExtras.add(mBooking.getExtrasInfo().get(i).getLabel());
        }

        for (int i = 0; i < mBookingEditExtrasViewModel.getNumberOfOptions(); i++)
        {
            //unfortunately, the booking object returned from the server
            //only contains display names (and no machine/key names) of the extras!
            String optionDisplayName = mBookingEditExtrasViewModel.getOptionDisplayName(i);
            boolean optionWasInOriginalBooking = bookingExtras.contains(optionDisplayName);
            if (selectedOptionIndexes.contains(i)) //if this index is of an option that user selected
            {
                if (!optionWasInOriginalBooking) //if was not in original booking
                {
                    addedExtras.add(mBookingEditExtrasViewModel.getOptionMachineName(i));
                }
                //otherwise it was already in original booking. do nothing
            }
            else
            {
                if (optionWasInOriginalBooking) //if index was not selected and it was in original booking
                {
                    removedExtras.add(mBookingEditExtrasViewModel.getOptionMachineName(i));
                }
                //otherwise it was not selected and wasn't in original booking. do nothing
            }
        }

        BookingEditExtrasRequest bookingEditExtrasRequest = new BookingEditExtrasRequest();
        bookingEditExtrasRequest.setAddedExtras(addedExtras.toArray(new String[]{}));
        bookingEditExtrasRequest.setRemovedExtras(removedExtras.toArray(new String[]{}));
        showUiBlockers();
        bus.post(new HandyEvent.RequestEditBookingExtras(
                Integer.parseInt(mBooking.getId()), bookingEditExtrasRequest));
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            updateBookingSummaryViewForOptionsSelected();
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }
    };

    //TODO: clean up
    private void createOptionsView()
    {
        mOptionsView = new BookingOptionsSelectView(getActivity(),
                mBookingEditExtrasViewModel.getBookingOption(), optionUpdated);

        mOptionsView.hideTitle();

        //highlight the current selection
        mOptionsView.setCheckedIndexes(mBookingEditExtrasViewModel.getCheckedIndexesForBooking(mBooking));

        mOptionsLayout.removeAllViews();
        mOptionsLayout.addView(mOptionsView, 0);
    }

    /*
    updates the fields that are dependent on the options selected
     */
    private void updateBookingSummaryViewForOptionsSelected() //TODO: clean this up
    {
        mBookingExtrasPriceTableLayout.removeAllViews();

        for (Integer i : mOptionsView.getCheckedIndexes()) //build the extras details section
        {
            addExtrasDetailsRow(mBookingEditExtrasViewModel.getOptionDisplayName(i),
                    mBookingEditExtrasViewModel.getHourInfo(i),
                    mBookingEditExtrasViewModel.getFormattedOptionPrice(i)
            );
        }

        float totalHours = mBookingEditExtrasViewModel.getTotalHoursForCheckedIndexes(
                mOptionsView.getCheckedIndexes());

        //build the resulting booking detail section
        mBookingDurationText.setText(getResources().getString(
                R.string.booking_edit_num_hours_formatted, totalHours));
        mBilledOnText.setText(getResources().getString(
                R.string.billed_on_date_formatted,
                mBookingEditExtrasViewModel
                .getFutureBillDateFormatted()));

        mTotalDueText.setText(mBookingEditExtrasViewModel.getTotalDueText(
                mOptionsView.getCheckedIndexes(),
                this.getActivity()));
    }

    private void addExtrasDetailsRow(String displayName, float hours, String formattedPrice)
    {
        String rowLabel = getResources().getString(R.string.booking_edit_extras_booking_extras_entry_formatted, displayName, hours);
        String priceLabel = getResources().getString(R.string.booking_edit_positive_price_formatted, formattedPrice);
        LabelValueView extrasDetailRow = LabelValueView.newInstance(getActivity(), rowLabel, priceLabel);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.default_margin));
        extrasDetailRow.setLayoutParams(layoutParams);
        mBookingExtrasPriceTableLayout.addView(extrasDetailRow, layoutParams);
    }

    private void updateBookingSummaryText()
    {
        float bookingBaseHours = mBookingEditExtrasViewModel.getBookingBaseHours();
        String originalBookingBasePrice = mBookingEditExtrasViewModel
                .getOriginalBookingBasePriceFormatted();
        mBookingTableRow.setLabelAndValueText(
                getResources().getString(R.string.booking_edit_base_hours_formatted, bookingBaseHours),
                originalBookingBasePrice);
    }

    private void setSaveButtonEnabled(boolean enabled)
    {
        mSaveButton.setEnabled(enabled);
    }

    @Override
    protected void showUiBlockers()
    {
        super.showUiBlockers();
        setSaveButtonEnabled(false);
    }

    @Override
    protected void removeUiBlockers()
    {
        super.removeUiBlockers();
        mContentContainer.setVisibility(View.VISIBLE);
        setSaveButtonEnabled(true);
    }

    @Subscribe
    public final void onReceiveEditExtrasViewModelSuccess(
            HandyEvent.ReceiveEditBookingExtrasViewModelSuccess event)
    {
        mBookingEditExtrasViewModel = event.mBookingEditExtrasViewModel;

        createOptionsView();
        updateBookingSummaryText();
        updateBookingSummaryViewForOptionsSelected();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveEditExtrasViewModelError(
            HandyEvent.ReceiveEditBookingExtrasViewModelError event)
    {
        onReceiveErrorEvent(event);
        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid
    }

    @Subscribe
    public final void onReceiveEditBookingExtrasSuccess(HandyEvent.ReceiveEditExtrasSuccess event)
    {
        showToast(getString(R.string.booking_edit_extras_update_success));

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditBookingExtrasError(HandyEvent.ReceiveEditExtrasError event)
    {
        onReceiveErrorEvent(event);
        removeUiBlockers(); //allow user to try again
    }
}
