package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDetailFragment extends BookingFlowFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    private Booking booking;
    private boolean updatedBooking;

    @InjectView(R.id.service_text) TextView serviceText;
    @InjectView(R.id.job_text) TextView jobText;
    @InjectView(R.id.address_text) TextView addrText;
    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.duration_text) TextView durationText;
    @InjectView(R.id.price_text) TextView priceText;
    @InjectView(R.id.pro_text) TextView proText;
    @InjectView(R.id.pro_layout) View proView;
    @InjectView(R.id.reschedule_button) Button rescheduleButton;


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

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_UPDATED_BOOKING)) {
                setUpdatedBookingResult();
            }
        }
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

        addrText.setText(TextUtils.formatAddress(address.getAddress1(), address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));

        dateText.setText(TextUtils.formatDate(booking.getStartDate(), "MMM d',' h:mm aaa"));

        durationText.setText(TextUtils.formatDecimal(booking.getHours(), "#.#") + " "
                + getString(R.string.hours).toLowerCase());

        final User user = userManager.getCurrentUser();
        priceText.setText(TextUtils.formatPrice(booking.getPrice(),
                user.getCurrencyChar(), null));

        final Booking.Provider pro = booking.getProvider();
        if (pro.getStatus() == 3) {
            proText.setText(pro.getFirstName() + " " + pro.getLastName() +
                    (pro.getPhone() != null ? "\n" + TextUtils.formatPhone(pro.getPhone(),
                            user.getPhonePrefix()) : ""));

            Linkify.addLinks(proText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(proText);
        }
        else proView.setVisibility(View.GONE);

        rescheduleButton.setOnClickListener(rescheduleClicked);

        return view;
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        rescheduleButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        rescheduleButton.setClickable(true);
    }

    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
                                       final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == BookingRescheduleActivity.RESULT_NEW_DATE) {
            booking.setStartDate(new Date(data
                    .getLongExtra(BookingRescheduleActivity.EXTRA_NEW_DATE, 0)));
            dateText.setText(TextUtils.formatDate(booking.getStartDate(), "MMM d',' h:mm aaa"));
            setUpdatedBookingResult();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, updatedBooking);
    }

    private View.OnClickListener rescheduleClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableInputs();
            progressDialog.show();

            dataManager.getPreRescheduleInfo(booking.getId(), new DataManager.Callback<String>() {
                @Override
                public void onSuccess(final String notice) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();

                    final Intent intent = new Intent(getActivity(), BookingRescheduleActivity.class);
                    intent.putExtra(BookingRescheduleActivity.EXTRA_BOOKING, booking);
                    intent.putExtra(BookingRescheduleActivity.EXTRA_NOTICE, notice);
                    startActivityForResult(intent, BookingRescheduleActivity.RESULT_NEW_DATE);
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
            });
        }
    };

    private final void setUpdatedBookingResult() {
        updatedBooking = true;

        final Intent intent = new Intent();
        intent.putExtra(BookingDetailActivity.EXTRA_UPDATED_BOOKING, booking);
        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_UPDATED, intent);
    }
}
