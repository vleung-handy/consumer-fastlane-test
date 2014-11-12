package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingAddressFragment extends InjectedFragment {
    private static final String STATE_FULLNAME_HIGHLIGHT = "FULLNAME_HIGHLIGHT";
    private static final String STATE_ADDR1_HIGHLIGHT = "ADDR1_HIGHLIGHT";
    private static final String STATE_PHONE_HIGHLIGHT = "PHONE_HIGHLIGHT";

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.fullname_text) FullNameInputTextView fullNameText;
    @InjectView(R.id.street_addr_text) StreetAddressInputTextView streetAddrText;
    @InjectView(R.id.other_addr_text) EditText otherAddrText;
    @InjectView(R.id.phone_prefix_text) TextView phonePrefixText;
    @InjectView(R.id.phone_text) PhoneInputTextView phoneText;
    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.time_text) TextView timeText;
    @InjectView(R.id.price_text) TextView priceText;

    static BookingAddressFragment newInstance() {
        final BookingAddressFragment fragment = new BookingAddressFragment();
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_address,container, false);

        ButterKnife.inject(this, view);

        User user = userManager.getCurrentUser();
        if (user != null) {
            fullNameText.setText(user.getFirstName() + " " +user.getLastName());
            phoneText.setCountryCode(user.getPhonePrefix());
            phoneText.setText(user.getPhone());
            phonePrefixText.setText(user.getPhonePrefix());

            final User.Address addr = user.getAddress();
            if (addr != null) {
                streetAddrText.setText(addr.getAddress1());
                otherAddrText.setText(addr.getAddress2());
            }

            //TODO add this pages info to booking transaction
            //TODO refactor header info into fragmetn and class
            //TODO use format util for all date strings and decimal formats
        }
        else {
            //TODO if no user, use booking prefix, currency, phone prefix etc & leave fields blank
        }

        final BookingQuote quote = bookingManager.getCurrentQuote();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();

        dateText.setText(TextUtils.formatDate(transaction.getStartDate(), "EEEE',' MMMM d"));

        timeText.setText(TextUtils.formatDate(transaction.getStartDate(), "h:mm aaa") + " - "
                + TextUtils.formatDecimal(transaction.getHours(), "#.#")
                + " " + getString(R.string.hours));

        priceText.setText(TextUtils.formatPrice(quote.getPriceTableMap()
                        .get(transaction.getHours()).getPrice(),
                quote.getCurrencyChar(), quote.getCurrencySuffix()));

        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_FULLNAME_HIGHLIGHT)) fullNameText.highlight();
            if (savedInstanceState.getBoolean(STATE_ADDR1_HIGHLIGHT)) streetAddrText.highlight();
            if (savedInstanceState.getBoolean(STATE_PHONE_HIGHLIGHT)) phoneText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_FULLNAME_HIGHLIGHT, fullNameText.isHighlighted());
        outState.putBoolean(STATE_ADDR1_HIGHLIGHT, streetAddrText.isHighlighted());
        outState.putBoolean(STATE_PHONE_HIGHLIGHT, phoneText.isHighlighted());
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!fullNameText.validate()) validate = false;
        if (!streetAddrText.validate()) validate = false;
        if (!phoneText.validate()) validate = false;
        return validate;
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                Toast.makeText(getActivity(), "SHOW PAYMENT VIEW", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
