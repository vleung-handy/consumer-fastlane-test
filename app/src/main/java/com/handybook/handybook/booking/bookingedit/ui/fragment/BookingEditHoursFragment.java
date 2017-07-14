package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.LabelValueView;
import com.handybook.handybook.library.util.UiUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditHoursFragment extends ProgressSpinnerFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.booking_edit_hours_base_time_row)
    LabelValueView mBaseTimeDetailsView;
    @BindView(R.id.booking_edit_hours_added_time_row)
    LabelValueView mAddedTimeDetailsView;
    @BindView(R.id.booking_edit_hours_extras_time_row)
    LabelValueView mExtrasTimeDetailsView;
    @BindView(R.id.booking_edit_hours_total_due_text)
    TextView mTotalDueText;
    @BindView(R.id.booking_edit_hours_booking_duration_text)
    TextView mBookingDurationText;
    @BindView(R.id.booking_edit_hours_options_view_container)
    LinearLayout mOptionsViewContainer; //TODO: can we use a stub, or replaceView instead?
    @BindView(R.id.booking_edit_hours_billed_on_text)
    TextView mBilledOnText;
    @BindView(R.id.booking_edit_hours_container)
    ScrollView mContainer;
    @BindView(R.id.booking_edit_hours_apply_to_recurring_option_placeholder)
    ViewStub mApplyToRecurringOptionPlaceholder;
    @BindView(R.id.next_button)
    Button mSaveButton;

    private Booking mBooking;

    private BookingEditHoursViewModel mBookingEditHoursViewModel;
    private BookingOptionsSelectView mApplyToRecurringBookingsSelectView;
    private BookingOptionsSpinnerView mOptionsView;

    @NonNull
    public static BookingEditHoursFragment newInstance(Booking booking) {
        final BookingEditHoursFragment fragment = new BookingEditHoursFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        showUiBlockers();
        bus.post(new BookingEditEvent.RequestEditHoursInfoViewModel(Integer.parseInt(mBooking.getId())));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_edit_hours, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.booking_edit_hours_title));

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBooking.isRecurring()) {
            //show the "apply to recurring bookings" option
            BookingOption bookingOption
                    = new BookingOption(); //TODO: is there a standalone checkbox widget?
            bookingOption.setType(BookingOption.TYPE_CHECKLIST);
            bookingOption.setOptions(new String[]{getResources().getString(R.string.booking_edit_should_apply_to_subsequent_bookings_option_label)});

            mApplyToRecurringBookingsSelectView = new BookingOptionsSelectView(
                    getActivity(),
                    bookingOption,
                    null
            );
            mApplyToRecurringBookingsSelectView.setCurrentIndex(0);
            mApplyToRecurringBookingsSelectView.hideTitle();
            UiUtils.replaceView(
                    mApplyToRecurringOptionPlaceholder,
                    mApplyToRecurringBookingsSelectView
            );
        }
    }

    @OnClick(R.id.next_button)
    public void onSaveButtonPressed() {
        showUiBlockers();
        double selectedHours = Double.parseDouble(mOptionsView.getCurrentValue());
        BookingEditHoursRequest bookingEditHoursRequest = new BookingEditHoursRequest();
        bookingEditHoursRequest.setNewBaseHrs(selectedHours);
        bookingEditHoursRequest.setApplyToRecurring(
                mApplyToRecurringBookingsSelectView != null
                && mApplyToRecurringBookingsSelectView.getCheckedIndexes().length > 0);
        bus.post(new BookingEditEvent.RequestEditHours(
                Integer.parseInt(mBooking.getId()), bookingEditHoursRequest));
    }

    private void initializeUiForEditHoursInfo() {
        inflateOptionsView();

        //these are not editable from this screen
        String baseHoursFormatted = mBookingEditHoursViewModel.getBaseHoursFormatted();
        String basePriceFormatted = mBookingEditHoursViewModel.getBasePriceFormatted();
        mBaseTimeDetailsView.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_base_hours_formatted,
                        baseHoursFormatted
                ), basePriceFormatted);
        mExtrasTimeDetailsView.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_extra_hours_formatted,
                        mBookingEditHoursViewModel.getExtrasHoursFormatted()
                ),
                getResources().getString(
                        R.string.booking_edit_positive_price_formatted,
                        mBookingEditHoursViewModel.getExtrasPriceFormatted()
                )
        );

    }

    /**
     * Updates the price details view (which includes "Base time", "Added time", "Total Due", etc) based on the option that the user has selected
     */
    private void updateUiForOptionSelected() {
        float selectedHours = Float.parseFloat(mOptionsView.getCurrentValue());

        String addedHoursFormatted
                = mBookingEditHoursViewModel.getAddedHoursFormatted(selectedHours);
        String addedPriceFormatted = mBookingEditHoursViewModel.getAddedHoursPriceFormatted(
                selectedHours);
        String totalHoursFormatted
                = mBookingEditHoursViewModel.getTotalHoursFormatted(selectedHours);

        mAddedTimeDetailsView.setLabelAndValueText(
                getResources().getString(
                        R.string.booking_edit_added_hours_formatted,
                        addedHoursFormatted
                ),
                mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours) ?
                addedPriceFormatted : getResources().getString(
                        R.string.booking_edit_positive_price_formatted,
                        addedPriceFormatted
                )
        );

        mBookingDurationText.setText(
                getResources().getString(
                        R.string.booking_edit_num_hours_formatted,
                        totalHoursFormatted
                ));
        mBilledOnText.setText(getResources().getString(
                R.string.billed_on_date_formatted,
                mBookingEditHoursViewModel.getFutureBillDateFormatted()
        ));
        mTotalDueText.setText(mBookingEditHoursViewModel.getTotalDuePriceFormatted(selectedHours));

        TextView warningText = ((TextView) mOptionsView.findViewById(R.id.warning_text));
        //options view does not have a method to set this text. why?

        //this is the same logic that the web is using to show the edit hours warning message
        if (mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours)) {
            warningText.setVisibility(View.VISIBLE);
            warningText.setText(R.string.booking_edit_hours_options_warning);
        }
        else {
            warningText.setVisibility(View.GONE);
        }

    }

    /**
     * initializes the option selector view based on the edit hours view model
     */
    private void inflateOptionsView() {
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);

        String[] optionHourStrings = mBookingEditHoursViewModel.getSelectableHoursArray();
        bookingOption.setOptions(optionHourStrings);

        //by default, the selected option will be the # of base hours in the booking
        double baseHours = mBookingEditHoursViewModel.getBaseHours();
        //find out which index in the option hour array should be selected
        int selectedIndex = 0;
        for (int i = 0; i < optionHourStrings.length; i++) {
            if (baseHours == Double.parseDouble(optionHourStrings[i])) {
                selectedIndex = i;
                break;
            }
        }
        bookingOption.setDefaultValue(Integer.toString(selectedIndex));
        //for some reason this function only accepts a string, but then the view converts it to an index?

        mOptionsView = new BookingOptionsSpinnerView(
                getActivity(), bookingOption,
                new BookingOptionsView.OnUpdatedListener() {
                    @Override
                    public void onUpdate(final BookingOptionsView view) {
                        updateUiForOptionSelected();
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
                }
        );

        ((TextView) mOptionsView.findViewById(R.id.title_text)).setText(
                R.string.booking_edit_hours_options_title);
        //seems like options view does not have a setter for this text!

        mOptionsViewContainer.removeAllViews();
        mOptionsViewContainer.addView(mOptionsView);
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
        mContainer.setVisibility(View.VISIBLE);
        setSaveButtonEnabled(true);
    }

    @Subscribe
    public final void onReceiveEditHoursInfoSuccess(BookingEditEvent.ReceiveEditHoursInfoViewModelSuccess event) {
        mBookingEditHoursViewModel = event.editHoursInfoViewModel;
        initializeUiForEditHoursInfo();
        updateUiForOptionSelected();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveEditHoursInfoError(BookingEditEvent.ReceiveEditHoursInfoViewModelError event) {
        dataManagerErrorHandler.handleError(getActivity(), event.error);
        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid
    }

    @Subscribe
    public final void onReceiveEditHoursSuccess(BookingEditEvent.ReceiveEditHoursSuccess event) {
        showToast(getString(R.string.booking_edit_hours_update_success));

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveEditHoursError(BookingEditEvent.ReceiveEditHoursError event) {
        dataManagerErrorHandler.handleError(getActivity(), event.error);
        removeUiBlockers(); //allow user to try again
    }

}
