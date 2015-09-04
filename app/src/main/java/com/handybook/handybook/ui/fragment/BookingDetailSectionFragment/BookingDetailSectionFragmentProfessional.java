package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.text.util.Linkify;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

public class BookingDetailSectionFragmentProfessional extends BookingDetailSectionFragment
{
//    public static BookingDetailSectionFragmentProfessional newInstance(final Booking booking)
//    {
//        final BookingDetailSectionFragmentProfessional fragment = new BookingDetailSectionFragmentProfessional();
//        final Bundle args = new Bundle();
//        args.putParcelable(BundleKeys.BOOKING, booking);
//        fragment.setArguments(args);
//        return fragment;
//    }




    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        System.out.println("This update display WAS overridden, it is of fragment professional type");
        //BookingDetailProfessionalSectionView professionalSectionView = (BookingDetailProfessionalSectionView) bookingDetailSectionView;
        //professionalSectionView.updateDisplay(booking, user);

        //bookingDetailSectionView.updateDisplay(booking, user);

        final Booking.Provider pro = booking.getProvider();
        if (pro.getStatus() == Booking.Provider.PROVIDER_STATUS_ASSIGNED)
        {

            bookingDetailSectionView.entryTitle.setText(R.string.professional);

            bookingDetailSectionView.entryText.setText(pro.getFirstName() + " "
                    + pro.getLastName()
                    + (pro.getPhone() != null ? "\n"
                    + TextUtils.formatPhone(pro.getPhone(), user.getPhonePrefix()) : ""));

            //TODO WHAT IS THIS LINKIFY THING DOING, IS THIS REPLACED BY OUR ACTION TEXT LINKS?
            Linkify.addLinks(bookingDetailSectionView.entryText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(bookingDetailSectionView.entryText);

            bookingDetailSectionView.entryActionText.setText(R.string.contact);

        }
        else
        {
            bookingDetailSectionView.setVisibility(View.GONE);
        }

    }


    @Override
    protected void testDoThing()
    {
        System.out.println("Professional fragment on click should not exist");
    }


}
