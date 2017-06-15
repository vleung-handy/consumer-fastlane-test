package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.LabelValueView;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditExtrasFragment extends ProgressSpinnerFragment {

    //TODO: use ViewModel
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.booking_edit_extras_content_container)
    ScrollView mContentContainer;
    @BindView(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @BindView(R.id.booking_edit_extras_booking_label_row)
    LabelValueView mBookingTableRow;
    @BindView(R.id.booking_edit_extras_booking_duration_text)
    TextView mBookingDurationText;
    @BindView(R.id.booking_edit_extras_total_due_text)
    TextView mTotalDueText;
    @BindView(R.id.booking_edit_extras_billed_on_text)
    TextView mBilledOnText;
    @BindView(R.id.booking_extras_price_table)
    LinearLayout mBookingExtrasPriceTableLayout;
    @BindView(R.id.next_button)
    Button mSaveButton;

    private Booking mBooking;

    private BookingEditExtrasViewModel mBookingEditExtrasViewModel;
    private BookingOptionsSelectView mOptionsView;

    public static BookingEditExtrasFragment newInstance(@NonNull Booking booking) {
        final BookingEditExtrasFragment fragment = new BookingEditExtrasFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //booking should never be null here
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        if (mBooking != null) {
            Crashlytics.log("Showing edit extras for booking with id " + mBooking.getId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressSpinner(true);
        bus.post(new BookingEditEvent.RequestEditBookingExtrasViewModel(
                Integer.parseInt(mBooking.getId())));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_edit_extras, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.booking_edit_extras_title));

        return view;
    }

    @OnClick(R.id.next_button)
    public void onSaveButtonPressed() {
        //api expects 2 string arrays of service machine names
        //one of added extras and one of removed extras, relative to the original booking
        List<String> addedExtras = new LinkedList<>();
        List<String> removedExtras = new LinkedList<>();

        //build a set of options that user has picked
        //so that we can check in constant time if a particular option was picked
        Set<Integer> selectedOptionIndexes = new HashSet<>();
        for (int i = 0; i < mOptionsView.getCheckedIndexes().length; i++) {
            selectedOptionIndexes.add(mOptionsView.getCheckedIndexes()[i]);
        }

        //build a set of extras that the original booking contained
        //so that we can check in constant time if a particular extra was already in the original booking
        Set<String> bookingExtras = new HashSet<>();
        for (int i = 0; i < mBooking.getExtrasInfo().size(); i++) {
            bookingExtras.add(mBooking.getExtrasInfo().get(i).getLabel());
        }

        for (int i = 0; i < mBookingEditExtrasViewModel.getNumberOfOptions(); i++) {
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
            else {
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
        showProgressSpinner(true);
        bus.post(new BookingEditEvent.RequestEditBookingExtras(
                Integer.parseInt(mBooking.getId()), bookingEditExtrasRequest));
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            updateBookingSummaryViewForOptionsSelected();
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

    //TODO: clean up
    private void createOptionsView() {
        mOptionsView = new BookingOptionsSelectView(getActivity(),
                                                    mBookingEditExtrasViewModel.getBookingOption(),
                                                    optionUpdated
        );

        mOptionsView.hideTitle();

        //highlight the current selection
        mOptionsView.setCheckedIndexes(mBookingEditExtrasViewModel.getCheckedIndexesForBooking(
                mBooking));

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
            addExtrasDetailsRow(
                    mBookingEditExtrasViewModel.getOptionDisplayName(i),
                    mBookingEditExtrasViewModel.getHourInfo(i),
                    mBookingEditExtrasViewModel.getFormattedOptionPrice(i)
            );
        }

        float totalHours = mBookingEditExtrasViewModel.getTotalHoursForCheckedIndexes(
                mOptionsView.getCheckedIndexes());

        //build the resulting booking detail section
        mBookingDurationText.setText(getResources().getString(
                R.string.booking_edit_num_hours_formatted, Float.toString(totalHours)));
        mBilledOnText.setText(getResources().getString(
                R.string.billed_on_date_formatted,
                mBookingEditExtrasViewModel
                        .getFutureBillDateFormatted()
        ));

        mTotalDueText.setText(mBookingEditExtrasViewModel.getTotalDueText(
                mOptionsView.getCheckedIndexes(),
                this.getActivity()
        ));
    }

    private void addExtrasDetailsRow(String displayName, float hours, String formattedPrice) {
        String rowLabel = getResources().getString(
                R.string.booking_edit_extras_booking_extras_entry_formatted,
                displayName,
                hours
        );
        String priceLabel = getResources().getString(
                R.string.booking_edit_positive_price_formatted,
                formattedPrice
        );
        LabelValueView extrasDetailRow = LabelValueView.newInstance(
                getActivity(),
                rowLabel,
                priceLabel
        );
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.default_margin));
        extrasDetailRow.setLayoutParams(layoutParams);
        mBookingExtrasPriceTableLayout.addView(extrasDetailRow, layoutParams);
    }

    private void updateBookingSummaryText() {
        float bookingBaseHours = mBookingEditExtrasViewModel.getBookingBaseHours();
        String originalBookingBasePrice = mBookingEditExtrasViewModel
                .getOriginalBookingBasePriceFormatted(getContext());
        mBookingTableRow.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_base_hours_formatted,
                        Float.toString(bookingBaseHours)
                ),
                originalBookingBasePrice
        );
    }

    private void setSaveButtonEnabled(boolean enabled) {
        mSaveButton.setEnabled(enabled);
    }

    @Override
    protected void showUiBlockers() {
        super.showProgressSpinner(true);
        setSaveButtonEnabled(false);
    }

    @Override
    protected void removeUiBlockers() {
        super.hideProgressSpinner();
        mContentContainer.setVisibility(View.VISIBLE);
        setSaveButtonEnabled(true);
    }

    @Subscribe
    public final void onReceiveEditExtrasViewModelSuccess(
            BookingEditEvent.ReceiveEditBookingExtrasViewModelSuccess event
    ) {
        mBookingEditExtrasViewModel = event.mBookingEditExtrasViewModel;

        createOptionsView();
        updateBookingSummaryText();
        updateBookingSummaryViewForOptionsSelected();
        hideProgressSpinner();
    }

    @Subscribe
    public final void onReceiveEditExtrasViewModelError(
            BookingEditEvent.ReceiveEditBookingExtrasViewModelError event
    ) {
        dataManagerErrorHandler.handleError(getActivity(), event.error);
        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid
    }

    @Subscribe
    public final void onReceiveEditBookingExtrasSuccess(BookingEditEvent.ReceiveEditExtrasSuccess event) {
        showToast(getString(R.string.booking_edit_extras_update_success));

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditBookingExtrasError(BookingEditEvent.ReceiveEditExtrasError event) {
        dataManagerErrorHandler.handleError(getActivity(), event.error);
        hideProgressSpinner(); //allow user to try again
    }
}
