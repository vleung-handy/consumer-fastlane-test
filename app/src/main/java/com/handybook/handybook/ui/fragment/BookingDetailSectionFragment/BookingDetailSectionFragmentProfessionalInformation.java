package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.text.util.Linkify;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentProfessionalInformation extends BookingDetailSectionFragment
{
    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        final Booking.Provider pro = booking.getProvider();

        if (pro.getStatus() == Booking.Provider.PROVIDER_STATUS_ASSIGNED)
        {
            view.entryTitle.setText(R.string.professional);

            view.entryText.setText(pro.getFirstName() + " "
                    + pro.getLastName()
                    + (pro.getPhone() != null ? "\n"
                    + TextUtils.formatPhone(pro.getPhone(), user.getPhonePrefix()) : ""));

            //TODO WHAT IS THIS LINKIFY THING DOING, IS THIS REPLACED BY OUR ACTION TEXT LINKS?
            Linkify.addLinks(view.entryText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(view.entryText);

            view.entryActionText.setText(R.string.contact);

        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActionClick()
    {
        System.out.println("Professional fragment on click should not exist");
    }


}
