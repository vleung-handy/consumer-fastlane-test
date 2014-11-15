package com.handybook.handybook;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDetailFragment extends BookingFlowFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking booking;

    @InjectView(R.id.service_text) TextView serviceText;
    @InjectView(R.id.job_text) TextView jobText;
    @InjectView(R.id.address_text) TextView addrText;
    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.duration_text) TextView durationText;
    @InjectView(R.id.price_text) TextView priceText;
    @InjectView(R.id.pro_text) TextView proText;
    @InjectView(R.id.pro_layout) View proView;

    static BookingDetailFragment newInstance(final Booking booking) {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(EXTRA_BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_detail,container, false);

        ButterKnife.inject(this, view);

        serviceText.setText(booking.getService());
        jobText.setText(booking.getId());

        final Booking.Address address = booking.getAddress();
        final String address2 = address.getAddress2();

        addrText.setText(address.getAddress1() + (address2 != null && address2.length() > 0 ? ", "
                + address2 + "\n" : "\n") + address.getCity() + ", "
                + address.getState() + " " + address.getZip());

        dateText.setText(TextUtils.formatDate(booking.getStartDate(), "MMM d',' h:mm aaa"));

        durationText.setText(TextUtils.formatDecimal(booking.getHours(), "#.#") + " "
                + getString(R.string.hours).toLowerCase());

        final User user = userManager.getCurrentUser();
        priceText.setText(TextUtils.formatPrice(booking.getPrice(),
                user.getCurrencyChar(), user.getCurrencySuffix()));

        final Booking.Provider pro = booking.getProvider();
        if (pro.getStatus() == 3) {
            proText.setText(pro.getFirstName() + " " + pro.getLastName() +
                    (pro.getPhone() != null ? "\n" + TextUtils.formatPhone(pro.getPhone(),
                            user.getPhonePrefix()) : ""));

            Linkify.addLinks(proText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(proText);
        }
        else proView.setVisibility(View.GONE);

        return view;
    }
}
