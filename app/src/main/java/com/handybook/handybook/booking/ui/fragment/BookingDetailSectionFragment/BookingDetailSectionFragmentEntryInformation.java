package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.text.Html;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.EntryMethodOption;
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
        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            String extraEntryInfo = booking.getExtraEntryInfo(); //TODO doubling as lockbox location for hack
            int entryMethodTypeId = booking.getEntryType();
            String entryMethodMachineName =
                    EntryMethodOption.getEntryMethodMachineNameForGetInId(entryMethodTypeId);

            if(BookingInstruction.InstructionType.EntryMethod.LOCKBOX.equals(entryMethodMachineName))
            {
                //TODO hack, refactor later
                String htmlText = "<b>" + entryInfo + "</b><br>Location: " + extraEntryInfo + "<br>Access code: " + booking.getLockboxCode();
                getSectionView().getEntryText().setText(Html.fromHtml(htmlText));
            }
            else
            {
                //not lockbox
                String entryInfoFormatted = entryInfo + " " + (extraEntryInfo != null ? extraEntryInfo : "");
                getSectionView().getEntryText().setText(entryInfoFormatted);
            }
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
