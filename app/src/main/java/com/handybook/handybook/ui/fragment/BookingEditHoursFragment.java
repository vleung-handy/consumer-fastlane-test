package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.request.BookingEditHoursRequest;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsSpinnerView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.handybook.handybook.ui.widget.LabelValueView;
import com.handybook.handybook.util.UiUtils;
import com.handybook.handybook.viewmodel.BookingEditHoursViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditHoursFragment extends BookingFlowFragment
{
    @Bind(R.id.booking_edit_hours_base_time_row)
    LabelValueView mBaseTimeDetailsView;
    @Bind(R.id.booking_edit_hours_added_time_row)
    LabelValueView mAddedTimeDetailsView;
    @Bind(R.id.booking_edit_hours_extras_time_row)
    LabelValueView mExtrasTimeDetailsView;
    @Bind(R.id.booking_edit_hours_total_due_text)
    TextView mTotalDueText;
    @Bind(R.id.booking_edit_hours_booking_duration_text)
    TextView mBookingDurationText;
    @Bind(R.id.booking_edit_hours_options_view_container)
    LinearLayout mOptionsViewContainer; //TODO: can we use a stub, or replaceView instead?
    @Bind(R.id.booking_edit_hours_billed_on_text)
    TextView mBilledOnText;
    @Bind(R.id.booking_edit_hours_container)
    ScrollView mContainer;
    @Bind(R.id.booking_edit_hours_apply_to_recurring_option_placeholder)
    ViewStub mApplyToRecurringOptionPlaceholder;
    @Bind(R.id.next_button)
    Button mSaveButton;

    private Booking mBooking;

    private BookingEditHoursViewModel mBookingEditHoursViewModel;
    private BookingOptionsSelectView mApplyToRecurringBookingsSelectView;
    private BookingOptionsSpinnerView mOptionsView;

    public static BookingEditHoursFragment newInstance(Booking booking)
    {
        final BookingEditHoursFragment fragment = new BookingEditHoursFragment();
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
        bus.post(new HandyEvent.RequestEditHoursInfoViewModel(Integer.parseInt(mBooking.getId())));
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_hours, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (mBooking.isRecurring())
        {
            //show the "apply to recurring bookings" option
            BookingOption bookingOption = new BookingOption(); //TODO: is there a standalone checkbox widget?
            bookingOption.setType(BookingOption.TYPE_CHECKLIST);
            bookingOption.setOptions(new String[]{getResources().getString(R.string.booking_edit_should_apply_to_subsequent_bookings_option_label)});

            mApplyToRecurringBookingsSelectView = new BookingOptionsSelectView(getActivity(), bookingOption,
                    null);
            mApplyToRecurringBookingsSelectView.setCurrentIndex(0);
            mApplyToRecurringBookingsSelectView.hideTitle();
            UiUtils.replaceView(mApplyToRecurringOptionPlaceholder, mApplyToRecurringBookingsSelectView);
        }
    }

    @OnClick(R.id.next_button)
    public void onSaveButtonPressed()
    {
        showUiBlockers();
        float selectedHours = Float.parseFloat(mOptionsView.getCurrentValue());
        BookingEditHoursRequest bookingEditHoursRequest = new BookingEditHoursRequest();
        bookingEditHoursRequest.setNewBaseHrs(selectedHours);
        bookingEditHoursRequest.setApplyToRecurring(
                mApplyToRecurringBookingsSelectView != null
                        && mApplyToRecurringBookingsSelectView.getCheckedIndexes().length > 0);
        bus.post(new HandyEvent.RequestEditHours(
                Integer.parseInt(mBooking.getId()), bookingEditHoursRequest));
    }

    private void initializeUiForEditHoursInfo()
    {
        inflateOptionsView();

        //these are not editable from this screen
        String baseHoursFormatted = mBookingEditHoursViewModel.getBaseHoursFormatted();
        String basePriceFormatted = mBookingEditHoursViewModel.getBasePriceFormatted();
        mBaseTimeDetailsView.setLabelAndValueText(
                getResources().getString(R.string.booking_edit_base_hours_formatted, baseHoursFormatted), basePriceFormatted);
        mExtrasTimeDetailsView.setLabelAndValueText(
                getResources().getString(R.string.booking_edit_extra_hours_formatted, mBookingEditHoursViewModel.getExtrasHoursFormatted()),
                getResources().getString(R.string.booking_edit_positive_price_formatted, mBookingEditHoursViewModel.getExtrasPriceFormatted()));

    }

    /**
     * Updates the price details view (which includes "Base time", "Added time", "Total Due", etc) based on the option that the user has selected
     */
    private void updateUiForOptionSelected()
    {
        float selectedHours = Float.parseFloat(mOptionsView.getCurrentValue());

        String addedHoursFormatted = mBookingEditHoursViewModel.getAddedHoursFormatted(selectedHours);
        String addedPriceFormatted = mBookingEditHoursViewModel.getAddedHoursPriceFormatted(selectedHours);
        String totalHoursFormatted = mBookingEditHoursViewModel.getTotalHoursFormatted(selectedHours);

        mAddedTimeDetailsView.setLabelAndValueText(
                getResources().getString(R.string.booking_edit_added_hours_formatted, addedHoursFormatted),
                mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours) ?
                        addedPriceFormatted : getResources().getString(R.string.booking_edit_positive_price_formatted,
                        addedPriceFormatted));

        mBookingDurationText.setText(
                getResources().getString(R.string.booking_edit_num_hours_formatted, totalHoursFormatted));
        mBilledOnText.setText(getResources().getString(R.string.billed_on_date_formatted,
                mBookingEditHoursViewModel.getFutureBillDateFormatted()));
        mTotalDueText.setText(mBookingEditHoursViewModel.getTotalDuePriceFormatted(selectedHours));

        TextView warningText = ((TextView) mOptionsView.findViewById(R.id.warning_text));
        //options view does not have a method to set this text. why?

        //this is the same logic that the web is using to show the edit hours warning message
        if (mBookingEditHoursViewModel.isSelectedHoursLessThanBaseHours(selectedHours))
        {
            warningText.setVisibility(View.VISIBLE);
            warningText.setText(R.string.booking_edit_hours_options_warning);
        }
        else
        {
            warningText.setVisibility(View.GONE);
        }

    }

    /**
     * initializes the option selector view based on the edit hours view model
     */
    private void inflateOptionsView()
    {
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);

        String[] optionHourStrings = mBookingEditHoursViewModel.getSelectableHoursArray();
        bookingOption.setOptions(optionHourStrings);

        //by default, the selected option will be the # of base hours in the booking
        float baseHours = mBookingEditHoursViewModel.getBaseHours();
        //find out which index in the option hour array should be selected
        int selectedIndex = 0;
        for (int i = 0; i < optionHourStrings.length; i++)
        {
            if (baseHours == Float.parseFloat(optionHourStrings[i]))
            {
                selectedIndex = i;
                break;
            }
        }
        bookingOption.setDefaultValue(Integer.toString(selectedIndex));
        //for some reason this function only accepts a string, but then the view converts it to an index?

        mOptionsView = new BookingOptionsSpinnerView(getActivity(), bookingOption,
                new BookingOptionsView.OnUpdatedListener()
                {
                    @Override
                    public void onUpdate(final BookingOptionsView view)
                    {
                        updateUiForOptionSelected();
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
                });

        ((TextView) mOptionsView.findViewById(R.id.title_text)).setText(
                R.string.booking_edit_hours_options_title);
        //seems like options view does not have a setter for this text!

        mOptionsViewContainer.removeAllViews();
        mOptionsViewContainer.addView(mOptionsView);
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
        mContainer.setVisibility(View.VISIBLE);
        setSaveButtonEnabled(true);
    }

    @Subscribe
    public final void onReceiveEditHoursInfoSuccess(HandyEvent.ReceiveEditHoursInfoViewModelSuccess event)
    {
        mBookingEditHoursViewModel = event.editHoursInfoViewModel;
        initializeUiForEditHoursInfo();
        updateUiForOptionSelected();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveEditHoursInfoError(HandyEvent.ReceiveEditHoursInfoViewModelError event)
    {
        onReceiveErrorEvent(event);
        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid
    }

    @Subscribe
    public final void onReceiveEditHoursSuccess(HandyEvent.ReceiveEditHoursSuccess event)
    {
        showToast(getString(R.string.booking_edit_hours_update_success));

        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, new Intent());
        getActivity().finish();
        //TODO: booking details screen not calling onActivityResult() and updating the booking
    }

    @Subscribe
    public final void onReceiveEditHoursError(HandyEvent.ReceiveEditHoursError event)
    {
        onReceiveErrorEvent(event);
        removeUiBlockers(); //allow user to try again
    }

}
