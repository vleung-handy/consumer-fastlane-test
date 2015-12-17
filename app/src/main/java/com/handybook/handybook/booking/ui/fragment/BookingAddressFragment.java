package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.core.User;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.ui.widget.FullNameInputTextView;
import com.handybook.handybook.ui.widget.PhoneInputTextView;
import com.handybook.handybook.ui.widget.StreetAddressInputTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingAddressFragment extends BookingFlowFragment {
    private static final String STATE_FULLNAME_HIGHLIGHT = "FULLNAME_HIGHLIGHT";
    private static final String STATE_ADDR1_HIGHLIGHT = "ADDR1_HIGHLIGHT";
    private static final String STATE_PHONE_HIGHLIGHT = "PHONE_HIGHLIGHT";

    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.fullname_text)
    FullNameInputTextView fullNameText;
    @Bind(R.id.street_addr_text)
    StreetAddressInputTextView streetAddrText;
    @Bind(R.id.other_addr_text)
    EditText otherAddrText;
    @Bind(R.id.phone_prefix_text)
    TextView phonePrefixText;
    @Bind(R.id.phone_text)
    PhoneInputTextView phoneText;

    public static BookingAddressFragment newInstance() {
        final BookingAddressFragment fragment = new BookingAddressFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackAddress();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_address,container, false);

        ButterKnife.bind(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
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
        }
        else {
            final String prefix = bookingManager.getCurrentQuote().getPhonePrefix();
            phoneText.setCountryCode(prefix);
            phonePrefixText.setText(prefix);
        }

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
                final BookingTransaction transaction = bookingManager.getCurrentTransaction();
                transaction.setFirstName(fullNameText.getFirstName());
                transaction.setLastName(fullNameText.getLastName());
                transaction.setAddress1(streetAddrText.getAddress());
                transaction.setAddress2(otherAddrText.getText().toString());
                transaction.setPhone(phoneText.getPhoneNumber());

                final Intent intent = new Intent(getActivity(), BookingPaymentActivity.class);
                startActivity(intent);
            }
        }
    };
}
