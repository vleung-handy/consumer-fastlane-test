package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.InputFormDefinition;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    public static final String TAG= "BookingDetailSectionFragmentEntryInformation";

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.entry_info;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return true;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final EntryMethodOption entryMethodOption = booking.getEntryMethodOption();
        if (entryMethodOption != null)
        {
            String titleText = entryMethodOption.getTitleText();
            String bodyText = "";
            boolean shouldDisplayAnyAdditionalInstructions = false;
            if (entryMethodOption.getInputFormDefinition() != null
                    && entryMethodOption.getInputFormDefinition().getFieldDefinitions() != null)
            {
                for (InputFormDefinition.InputFormField inputFormField : entryMethodOption.getInputFormDefinition()
                                                                                          .getFieldDefinitions())
                {
                    if (!TextUtils.isEmpty(inputFormField.getValue()))
                    {
                        bodyText = bodyText + "<br>" + inputFormField.getTitle() + ": " + inputFormField
                                .getValue();
                        shouldDisplayAnyAdditionalInstructions = true;
                    }
                }
            }
            if (shouldDisplayAnyAdditionalInstructions)
            {
                titleText = "<b>" + titleText + "</b>";
            }
            getSectionView().getEntryText().setText(Html.fromHtml(titleText + bodyText));
        }
        else
        {
            getSectionView().getEntryText().setText(R.string.no_information);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditEntryInformationActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
