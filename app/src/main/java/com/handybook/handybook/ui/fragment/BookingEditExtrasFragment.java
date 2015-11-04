package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingEditExtrasTransaction;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.EditExtrasInfo;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.handybook.handybook.ui.widget.LabelValueView;
import com.handybook.handybook.util.MathUtils;
import com.handybook.handybook.util.TextUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private Booking mBooking;
    private EditExtrasInfo mEditExtrasInfo;
    private BookingOptionsSelectView mOptionsView;
    private BookingEditExtrasTransaction mBookingEditExtrasTransaction;

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
        initTransaction();
    }

    private void initTransaction()
    {
        mBookingEditExtrasTransaction = new BookingEditExtrasTransaction();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HandyEvent.RequestGetServiceExtrasOptions(Integer.parseInt(mBooking.getId())));
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

        for (int i = 0; i < mEditExtrasInfo.getOptionsDisplayNames().length; i++)
        {
            //unfortunately, the booking object returned from the server
            //only contains display names (and no machine/key names) of the extras!
            boolean optionWasInOriginalBooking = bookingExtras.contains(mEditExtrasInfo.getOptionsDisplayNames()[i]);
            if (selectedOptionIndexes.contains(i)) //if this index is of an option that user selected
            {
                if (!optionWasInOriginalBooking) //if was not in original booking
                {
                    addedExtras.add(mEditExtrasInfo.getOptionsMachineNames()[i]);
                }
                //otherwise it was already in original booking. do nothing
            }
            else
            {
                if (optionWasInOriginalBooking) //if index was not selected and it was in original booking
                {
                    removedExtras.add(mEditExtrasInfo.getOptionsMachineNames()[i]);
                }
                //otherwise it was not selected and wasn't in original booking. do nothing
            }
        }
        mBookingEditExtrasTransaction.setAddedExtras(addedExtras.toArray(new String[]{}));
        mBookingEditExtrasTransaction.setRemovedExtras(removedExtras.toArray(new String[]{}));
        showUiBlockers();
        bus.post(new HandyEvent.RequestEditServiceExtrasOptions(Integer.parseInt(mBooking.getId()), mBookingEditExtrasTransaction));
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
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_CHECKLIST);
        bookingOption.setOptions(mEditExtrasInfo.getOptionsDisplayNames());
        bookingOption.setOptionsSubText(mEditExtrasInfo.getOptionsSubText());
        bookingOption.setImageResourceIds(mEditExtrasInfo.getOptionImagesResourceIdArray());
        EditExtrasInfo.OptionPrice[] optionPrices = mEditExtrasInfo.getOptionPrices();
        String[] optionsRightStrings = new String[optionPrices.length];
        for (int i = 0; i < optionPrices.length; i++)
        {
            optionsRightStrings[i] = optionPrices[i].getFormattedPrice();
        }

        bookingOption.setOptionsRightTitleText(optionsRightStrings);

        mOptionsView = new BookingOptionsSelectView(getActivity(),
                bookingOption, optionUpdated);

        mOptionsView.hideTitle();

        //highlight the current selection
        ArrayList<Booking.ExtraInfo> extrasInfo = mBooking.getExtrasInfo();

        //the only way to know what extras a user has selected is by an array of extras display names in the booking object
        if (extrasInfo.size() > 0)
        {
            Map<String, Integer> extraDisplayNameToOptionIndexMap = mEditExtrasInfo.getExtraDisplayNameToOptionIndexMap();
            Integer checkedIndexes[] = new Integer[extrasInfo.size()];
            for (int i = 0; i < checkedIndexes.length; i++)
            {
                checkedIndexes[i] = extraDisplayNameToOptionIndexMap.get(extrasInfo.get(i).getLabel());
            }

            mOptionsView.setCheckedIndexes(checkedIndexes);
        }

        mOptionsLayout.removeAllViews();
        mOptionsLayout.addView(mOptionsView, 0);
    }

    //TODO: move somewhere else?
    private LabelValueView createPaymentDetailLabelValueView(String label, String value)
    {
        LabelValueView labelValueView = (LabelValueView) getActivity().getLayoutInflater().inflate(R.layout.element_label_value_view, null);
        labelValueView.setLabelAndValueText(label, value);
        return labelValueView;
    }

    //TODO: move somewhere else
    private String getFormattedHoursForPriceTable(float hours)
    {
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc

        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatNumberToAtMostOneDecimalPlace(hours);
    }

    /*
    updates the fields that are dependent on the options selected
     */
    private void updateBookingSummaryViewForOptionsSelected() //TODO: clean this up
    {
        mBookingExtrasPriceTableLayout.removeAllViews();

        float extrasHours = 0;
        for (Integer i : mOptionsView.getCheckedIndexes()) //build the extras details section
        {
            addExtrasDetailsRow(mEditExtrasInfo.getOptionsDisplayNames()[i],
                    mEditExtrasInfo.getHourInfo()[i],
                    mEditExtrasInfo.getOptionPrices()[i].getFormattedPrice());

            extrasHours += mEditExtrasInfo.getHourInfo()[i];
        }

        float bookingBaseHours = mEditExtrasInfo.getBaseHours();
        float totalHours = bookingBaseHours + extrasHours;

        //build the resulting booking detail section
        mBookingDurationText.setText(getResources().getString(R.string.booking_edit_num_hours_display, totalHours));
        mBilledOnText.setText(getResources().getString(R.string.billed_on_date, mEditExtrasInfo.getPaidStatus().getFutureBillDateFormatted()));

        String totalHoursFormatted = getFormattedHoursForPriceTable(totalHours);
        Map<String, EditExtrasInfo.PriceInfo> priceTable = mEditExtrasInfo.getPriceTable();
        mTotalDueText.setText(priceTable.containsKey(
                totalHoursFormatted) ?
                priceTable.get(totalHoursFormatted).getTotalDueFormatted() :
                getResources().getString(R.string.no_data_indicator));
    }

    private void addExtrasDetailsRow(String displayName, float hours, String formattedPrice)
    {
        String rowLabel = getResources().getString(R.string.booking_edit_extras_booking_extras_entry_label, displayName, hours);
        String priceLabel = getResources().getString(R.string.booking_edit_positive_price, formattedPrice);
        LabelValueView extrasDetailRow = createPaymentDetailLabelValueView(rowLabel, priceLabel);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.default_margin));
        extrasDetailRow.setLayoutParams(layoutParams);
        mBookingExtrasPriceTableLayout.addView(extrasDetailRow, layoutParams);
    }

    private void updateBookingSummaryText()
    {
        float bookingBaseHours = mEditExtrasInfo.getBaseHours(); //it is weird for api to return this
        String originalBookingBaseHours = getFormattedHoursForPriceTable(bookingBaseHours);
        String originalBookingBasePrice = mEditExtrasInfo.getPriceTable().get(originalBookingBaseHours).getTotalDueFormatted();
        mBookingTableRow.setLabelAndValueText(getResources().getString(R.string.booking_edit_base_time_label, bookingBaseHours), originalBookingBasePrice);
    }

    @Override
    protected void removeUiBlockers()
    {
        super.removeUiBlockers();
        mContentContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public final void onReceiveServicesExtrasOptionsSuccess(HandyEvent.ReceiveGetServiceExtrasOptionsSuccess event)
    {
        mEditExtrasInfo = event.editExtrasInfo;

        createOptionsView();
        updateBookingSummaryText();
        updateBookingSummaryViewForOptionsSelected();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveServicesExtrasOptionsError(HandyEvent.ReceiveGetServiceExtrasOptionsError event)
    {
        onReceiveErrorEvent(event);
    }

    @Subscribe
    public final void onReceiveUpdateServiceExtrasSuccess(HandyEvent.ReceiveEditServiceExtrasOptionsSuccess event)
    {
        removeUiBlockers();
        showToast(getString(R.string.edit_extras_update_success_msg));

        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateServiceExtrasError(HandyEvent.ReceiveEditServiceExtrasOptionsError event)
    {
        onReceiveErrorEvent(event);
    }
}
