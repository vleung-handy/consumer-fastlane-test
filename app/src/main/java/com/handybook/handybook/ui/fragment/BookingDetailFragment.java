package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDetailFragment extends BookingFlowFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    private Booking booking;
    private boolean updatedBooking;

    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.time_text) TextView timeText;
    @InjectView(R.id.freq_text) TextView freqText;
    @InjectView(R.id.freq_layout) View freqLayout;
    @InjectView(R.id.pro_section) View proSection;
    @InjectView(R.id.pro_text) TextView proText;
    @InjectView(R.id.laundry_section) View laundrySection;
    @InjectView(R.id.entry_section) View entrySection;
    @InjectView(R.id.entry_text) TextView entryText;
    @InjectView(R.id.pro_note_section) View proNoteSection;
    @InjectView(R.id.pro_note_text) TextView proNoteText;
    @InjectView(R.id.extras_section) View extrasSection;
    @InjectView(R.id.extras_text) TextView extrasText;
    @InjectView(R.id.addr_text) TextView addrText;
    @InjectView(R.id.total_text) TextView totalText;
    @InjectView(R.id.pay_lines_section) LinearLayout paymentLinesSection;
    @InjectView(R.id.billed_text) TextView billedText;
    @InjectView(R.id.options_layout) View optionsLayout;
    @InjectView(R.id.reschedule_button) Button rescheduleButton;
    @InjectView(R.id.cancel_button) Button cancelButton;
    @InjectView(R.id.booking_text) TextView bookingText;
    @InjectView(R.id.nav_text) TextView navText;

    public static BookingDetailFragment newInstance(final Booking booking) {
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
                .inflate(R.layout.fragment_booking_detail, container, false);

        ButterKnife.inject(this, view);

        navText.setText(booking.getService());
        bookingText.setText("Booking #" + booking.getId());

        updateDateTimeInfoText(booking.getStartDate());

        final String recurringInfo = booking.getRecurringInfo();
        if (recurringInfo == null) freqLayout.setVisibility(View.GONE);
        else freqText.setText(booking.getRecurringInfo());

        final User user = userManager.getCurrentUser();
        final Booking.Provider pro = booking.getProvider();
        if (pro.getStatus() == 3) {
            proText.setText(pro.getFirstName() + " " + pro.getLastName() +
                    (pro.getPhone() != null ? "\n" + TextUtils.formatPhone(pro.getPhone(),
                            user.getPhonePrefix()) : ""));

            Linkify.addLinks(proText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(proText);
        }
        else proSection.setVisibility(View.GONE);

        if (booking.getLaundryStatus() == null
                || booking.getLaundryStatus() == Booking.LaundryStatus.SKIPPED) {
            laundrySection.setVisibility(View.GONE);
        }

        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null) {
            entryText.setText(entryInfo + " "
                    + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
        }
        else entrySection.setVisibility(View.GONE);

        final String proNote = booking.getProNote();
        if (proNote != null) proNoteText.setText(proNote);
        else proNoteSection.setVisibility(View.GONE);

        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        if (extras != null && extras.size() > 0) {
            String extraInfo = "";

            for (int i = 0; i < extras.size(); i++) {
                final Booking.ExtraInfo info = extras.get(i);
                extraInfo += info.getLabel();

                if (i < extras.size() - 1) extraInfo += ", ";
            }

            extrasText.setText(extraInfo);
        }
        else extrasSection.setVisibility(View.GONE);

        final Booking.Address address = booking.getAddress();
        addrText.setText(TextUtils.formatAddress(address.getAddress1(), address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));

        final String price = TextUtils.formatPrice(booking.getPrice(),
                user.getCurrencyChar(), null);
        totalText.setText(price);

        final ArrayList<Booking.LineItem> paymentInfo = booking.getPaymentInfo();
        Collections.sort(paymentInfo, new Comparator<Booking.LineItem>() {
            @Override
            public int compare(final Booking.LineItem lhs, final Booking.LineItem rhs) {
                return lhs.getOrder() - rhs.getOrder();
            }
        });

        if (paymentInfo != null && paymentInfo.size() > 0) {
            paymentLinesSection.setVisibility(View.VISIBLE);

            View lineView;

            for (int i = 0; i < paymentInfo.size(); i++) {
                lineView = getActivity().getLayoutInflater()
                        .inflate(R.layout.view_payment_line, container, false);

                final TextView labelText = (TextView)lineView.findViewById(R.id.label_text);
                final TextView amountText = (TextView)lineView.findViewById(R.id.amount_text);
                final Booking.LineItem line = paymentInfo.get(i);

                labelText.setText(line.getLabel());
                amountText.setText(line.getAmount());

                if (i < paymentInfo.size() - 1) lineView.setPadding(0, 0, 0, Utils.toDP(10, getActivity()));

                paymentLinesSection.addView(lineView);
            }
        }

        final String billedStatus = booking.getBilledStatus();
        if (billedStatus != null)  billedText.setText(billedStatus);
        else billedText.setVisibility(View.GONE);

        if (booking.isPast()) optionsLayout.setVisibility(View.GONE);
        else {
            rescheduleButton.setOnClickListener(rescheduleClicked);
            cancelButton.setOnClickListener(cancelClicked);
        }

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

        if (resultCode == BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE) {
            updateDateTimeInfoText(new Date(data
                    .getLongExtra(BookingDateActivity.EXTRA_RESCHEDULE_NEW_DATE, 0)));
            setUpdatedBookingResult();
        }

        else if (resultCode == BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED) {
            setCanceledBookingResult();
            getActivity().finish();
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

                    final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                    intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_BOOKING, booking);
                    intent.putExtra(BookingDateActivity.EXTRA_RESCHEDULE_NOTICE, notice);
                    startActivityForResult(intent, BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE);
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

    private View.OnClickListener cancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableInputs();
            progressDialog.show();

            dataManager.getPreCancelationInfo(booking.getId(),
                    new DataManager.Callback<Pair<String, List<String>>>() {
                        @Override
                        public void onSuccess(final Pair<String, List<String>> result) {
                            if (!allowCallbacks) return;
                            enableInputs();
                            progressDialog.dismiss();

                            final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);

                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_OPTIONS,
                                    new ArrayList<>(result.second));

                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_NOTICE, result.first);
                            intent.putExtra(BookingCancelOptionsActivity.EXTRA_BOOKING, booking);

                            startActivityForResult(intent, BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED);
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

    private final void setCanceledBookingResult() {
        final Intent intent = new Intent();
        intent.putExtra(BookingDetailActivity.EXTRA_CANCELED_BOOKING, booking);
        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_CANCELED, intent);
    }

    private void updateDateTimeInfoText(final Date date) {
        final float hours = booking.getHours();
        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(date);
        endDate.add(Calendar.MINUTE, (int)(60 * hours));

        timeText.setText(TextUtils.formatDate(date, "h:mmaaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mmaaa (") + TextUtils.formatDecimal(hours, "#.#") + " "
                + getResources().getQuantityString(R.plurals.hour,
                (int)Math.ceil(hours)) + ")");

        dateText.setText(TextUtils.formatDate(date, "EEEE',' MMM d',' yyyy"));
    }
}
