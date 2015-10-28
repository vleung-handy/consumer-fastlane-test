package com.handybook.handybook.ui.fragment;

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
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingUpdateExtrasTransaction;
import com.handybook.handybook.core.EditExtrasInfo;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.handybook.handybook.util.TextUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditExtrasFragment extends BookingFlowFragment
{
    @Inject
    SecurePreferences mSecurePrefs;

    @Bind(R.id.booking_edit_extras_content_container)
    ScrollView mContentContainer;
    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.nav_text)
    TextView mNavText;
    @Bind(R.id.booking_edit_extras_booking_label_row)
    LinearLayout mBookingTableRow;
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
    private BookingUpdateExtrasTransaction mBookingUpdateExtrasTransaction;

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
        mBookingUpdateExtrasTransaction = new BookingUpdateExtrasTransaction();
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

        mNavText.setText(R.string.edit_extras_title);
        mNextButton.setText(R.string.update);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                onSaveButtonPressed();
            }
        });
        return view;
    }

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
        mBookingUpdateExtrasTransaction.setAddedExtras(addedExtras.toArray(new String[]{}));
        mBookingUpdateExtrasTransaction.setRemovedExtras(removedExtras.toArray(new String[]{}));
        showUiBlockers();
        bus.post(new HandyEvent.RequestEditServiceExtrasOptions(Integer.parseInt(mBooking.getId()), mBookingUpdateExtrasTransaction));
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

    private int[] getOptionImagesResourceIdArray(EditExtrasInfo editExtrasInfo)
    {
        int[] resourceIds = new int[editExtrasInfo.getOptionsDisplayNames().length];
        for (int i = 0; i < resourceIds.length; i++)
        {
            resourceIds[i] = Booking.getImageResourceIdForMachineName(editExtrasInfo.getOptionsMachineNames()[i]);
        }
        return resourceIds;
    }

    //TODO: clean up
    private void createOptionsView()
    {
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_CHECKLIST);
        bookingOption.setOptions(mEditExtrasInfo.getOptionsDisplayNames());
        bookingOption.setOptionsSubText(mEditExtrasInfo.getOptionsSubText());
        bookingOption.setImageResourceIds(getOptionImagesResourceIdArray(mEditExtrasInfo));
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

        //this logic was duplicated from BookingExtrasFragment
        final String selected = mSecurePrefs.getString("STATE_BOOKING_CLEANING_EXTRAS_SEL");
        if (selected != null)
        {
            final String[] indexes = selected.split(",");
            final ArrayList<Integer> checked = new ArrayList<>();

            for (int i = 0; i < indexes.length; i++)
            {
                try
                {
                    checked.add(Integer.parseInt(indexes[i]));
                } catch (final NumberFormatException e)
                {
                }
            }

            mOptionsView.setCheckedIndexes(checked.toArray(new Integer[checked.size()]));
        }

        //highlight the current selection
        ArrayList<Booking.ExtraInfo> extrasInfo = mBooking.getExtrasInfo();

        //the only way to know what extras a user has selected is by an array of extras display names in the booking object
        if (extrasInfo.size() > 0)
        {
            Map<String, Integer> extraDisplayNameToOptionIndexMap = getExtraDisplayNameToOptionIndexMap(mEditExtrasInfo);
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

    //NOTE: the only way to know what extras a user has selected is by an array of extras display names in the booking object
    //so we must map those display names to associated index in the options
    private Map<String, Integer> getExtraDisplayNameToOptionIndexMap(EditExtrasInfo editExtrasInfo)
    {
        Map<String, Integer> extraDisplayNameToOptionIndexMap = new HashMap<>();
        for (int i = 0; i < editExtrasInfo.getOptionsDisplayNames().length; i++)
        {
            extraDisplayNameToOptionIndexMap.put(editExtrasInfo.getOptionsDisplayNames()[i], i);
        }
        return extraDisplayNameToOptionIndexMap;
    }

    //TODO: move somewhere else?
    private LinearLayout getPaymentDetailTableRow(String label, String value)
    {
        LinearLayout bookingRow = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.element_booking_extras_price_row, null);
        return updatePaymentDetailTableRow(bookingRow, label, value);
    }

    private LinearLayout updatePaymentDetailTableRow(LinearLayout bookingRow, String label, String value)
    {
        ((TextView) bookingRow.findViewById(R.id.price_table_row_label_text)).setText(label);
        ((TextView) bookingRow.findViewById(R.id.price_table_row_price_text)).setText(value);
        return bookingRow;
    }

    private String getFormattedHoursForPriceTable(float hours)
    {
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc
        return TextUtils.formatNumberToAtMostOneDecimalPoint(hours);
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
        mBookingDurationText.setText(getResources().getString(R.string.edit_extras_booking_total_hours, totalHours));
        mBilledOnText.setText(getResources().getString(R.string.edit_extras_billed_on_date, mEditExtrasInfo.getPaidStatus().getFutureBillDateFormatted()));

        String totalHoursFormatted = getFormattedHoursForPriceTable(totalHours);
        Map<String, EditExtrasInfo.PriceInfo> priceTable = mEditExtrasInfo.getPriceTable();
        mTotalDueText.setText(priceTable.containsKey(totalHoursFormatted) ? priceTable.get(totalHoursFormatted).getTotalDueFormatted() : getResources().getString(R.string.no_data_indicator));
    }

    private void addExtrasDetailsRow(String displayName, float hours, String formattedPrice)
    {
        String rowLabel = getResources().getString(R.string.edit_extras_booking_extras_entry_label, displayName, hours);
        String priceLabel = getResources().getString(R.string.edit_extras_booking_extras_price, formattedPrice);
        LinearLayout extrasDetailRow = getPaymentDetailTableRow(rowLabel, priceLabel);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.default_margin));
        extrasDetailRow.setLayoutParams(layoutParams);
        mBookingExtrasPriceTableLayout.addView(extrasDetailRow, layoutParams);
    }

    private void updateBookingSummaryText()
    {
        float bookingBaseHours = mEditExtrasInfo.getBaseHours(); //it is weird for api to return this
        String originalBookingBaseHours = getFormattedHoursForPriceTable(bookingBaseHours);
        String originalBookingBasePrice = mEditExtrasInfo.getPriceTable().get(originalBookingBaseHours).getTotalDueFormatted();
        updatePaymentDetailTableRow(mBookingTableRow, getResources().getString(R.string.edit_extras_booking_base_hours, bookingBaseHours), originalBookingBasePrice);
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
