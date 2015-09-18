package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.text.util.Linkify;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentProfessionalInformation extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId()
    {
        return R.string.professional;
    }

    @Override
    protected int getEntryActionTextResourceId()
    {
        return R.string.request_pro;
    }

    @Override
    protected boolean hasEnabledAction()
    {
        return false;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        final Booking.Provider pro = booking.getProvider();

        if (pro.getStatus() == Booking.Provider.PROVIDER_STATUS_ASSIGNED)
        {
            view.entryText.setText(pro.getFirstName() + " "
                    + pro.getLastName()
                    + (pro.getPhone() != null ? "\n"
                    + TextUtils.formatPhone(pro.getPhone(), user.getPhonePrefix()) : ""));

            //TODO WHAT IS THIS LINKIFY THING DOING, IS THIS REPLACED BY OUR ACTION TEXT LINKS?
            Linkify.addLinks(view.entryText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(view.entryText);


            //deprecating the linkify and replacing them with explicit contact buttons


        }
        else
        {
            //if no pro has been assigned indicate
            view.entryText.setText(R.string.pro_assignment_pending);
        }





    }


    @Override
    protected void onActionClick()
    {
        //If no pro assigned can request a pro

        //If pro has been assigned can use special call and text buttons to contact the pro but can longer request a pro



    }


}
