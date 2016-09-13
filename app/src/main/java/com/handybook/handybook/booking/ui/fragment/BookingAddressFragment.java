package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.GooglePlacesService;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.module.autocomplete.PlacePrediction;
import com.handybook.handybook.module.autocomplete.PlacesAutoCompleteAdapter;
import com.handybook.handybook.ui.widget.FullNameInputTextView;
import com.handybook.handybook.ui.widget.PhoneInputTextView;
import com.handybook.handybook.ui.widget.StreetAddressInputTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingAddressFragment extends BookingFlowFragment implements GoogleApiClient.OnConnectionFailedListener
{
    @Bind(R.id.main_container)
    View mMainContainer;

    @Bind(R.id.button_next)
    Button mButtonNext;

    @Bind(R.id.text_fullname)
    FullNameInputTextView mTextFullName;

    @Bind(R.id.text_street)
    StreetAddressInputTextView mTextStreet;

    @Bind(R.id.text_city)
    StreetAddressInputTextView mTextCity;

    @Bind(R.id.text_state)
    StreetAddressInputTextView mTextState;

    @Bind(R.id.text_postal)
    StreetAddressInputTextView mTextPostal;

    @Bind(R.id.text_other)
    EditText mTextOther;

    @Bind(R.id.text_phone_prefix)
    TextView mTextPhonePrefix;

    @Bind(R.id.text_phone)
    PhoneInputTextView mTextPhone;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    GooglePlacesService mPlacesService;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    public static BookingAddressFragment newInstance()
    {
        return new BookingAddressFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAddressShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_address, container, false);

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.address));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            mTextFullName.setText(user.getFirstName() + " " + user.getLastName());
            mTextPhone.setCountryCode(user.getPhonePrefix());
            mTextPhone.setText(user.getPhone());
            mTextPhonePrefix.setText(user.getPhonePrefix());

            final User.Address addr = user.getAddress();
            if (addr != null)
            {
                mTextStreet.setText(addr.getAddress1());
                mTextOther.setText(addr.getAddress2());
                mTextCity.setText(addr.getCity());
                mTextState.setText(addr.getState());
                mTextPostal.setText(addr.getZip());
            }
        }
        else
        {
            final String prefix = bookingManager.getCurrentQuote().getPhonePrefix();
            mTextPhone.setCountryCode(prefix);
            mTextPhonePrefix.setText(prefix);
        }

        mButtonNext.setOnClickListener(nextClicked);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_complete_list_item, mPlacesService);
        mTextStreet.setAdapter(mAutoCompleteAdapter);
        mTextStreet.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PlacePrediction prediction = mAutoCompleteAdapter.getPrediction(position);
                mTextStreet.setText(prediction.getAddress());
                mTextCity.setText(prediction.getCity());
                mTextState.setText(prediction.getState());

                mMainContainer.requestFocus();

                hideKeyboard();
            }
        });

        return view;
    }

    private boolean validateFields()
    {
        boolean validate = true;
        if (!mTextFullName.validate()) { validate = false; }
        if (!mTextStreet.validate()) { validate = false; }
        if (!mTextPhone.validate()) { validate = false; }
        if (!mTextCity.validate()) { validate = false; }
        if (!mTextState.validate()) { validate = false; }
        if (!mTextPostal.validate()) { validate = false; }
        return validate;
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (validateFields())
            {
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAddressSubmittedLog()));

                final BookingTransaction transaction = bookingManager.getCurrentTransaction();
                transaction.setFirstName(mTextFullName.getFirstName());
                transaction.setLastName(mTextFullName.getLastName());
                transaction.setAddress1(mTextStreet.getAddress());
                transaction.setAddress2(mTextOther.getText().toString());
                transaction.setPhone(mTextPhone.getPhoneNumber());

                final Intent intent = new Intent(getActivity(), BookingPaymentActivity.class);
                startActivity(intent);
            }
        }
    };


    public void hideKeyboard()
    {
        if (getActivity() != null && getView() != null)
        {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult)
    {
        Crashlytics.logException(new RuntimeException(getString(R.string.error_connect_gps)));
    }
}
