package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.module.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.ui.widget.FullNameInputTextView;
import com.handybook.handybook.ui.widget.PhoneInputTextView;

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

    @Bind(R.id.text_phone_prefix)
    TextView mTextPhonePrefix;

    @Bind(R.id.text_phone)
    PhoneInputTextView mTextPhone;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    AutoCompleteAddressFragment mAutoCompleteFragment;


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

        mAutoCompleteFragment = (AutoCompleteAddressFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            mTextFullName.setText(user.getFirstName() + " " + user.getLastName());
            mTextPhone.setCountryCode(user.getPhonePrefix());
            mTextPhone.setText(user.getPhone());
            mTextPhonePrefix.setText(user.getPhonePrefix());

            final User.Address addr = user.getAddress();
            if (addr != null && mAutoCompleteFragment != null)
            {
                //FIXME: JIA: verify that this bindable
                mAutoCompleteFragment.bindAddress(addr);
            }
        }
        else
        {
            final String prefix = bookingManager.getCurrentQuote().getPhonePrefix();
            mTextPhone.setCountryCode(prefix);
            mTextPhonePrefix.setText(prefix);
        }

        mButtonNext.setOnClickListener(nextClicked);

        return view;
    }

    private boolean validateFields()
    {
        boolean validate = mAutoCompleteFragment.validateFields();

        if (!mTextFullName.validate()) { validate = false; }
        if (!mTextPhone.validate()) { validate = false; }

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
                transaction.setPhone(mTextPhone.getPhoneNumber());

                if (mAutoCompleteFragment != null)
                {
                    transaction.setAddress1(mAutoCompleteFragment.textStreet.getAddress());
                    transaction.setAddress2(mAutoCompleteFragment.textOther.getText().toString());
                }

                final Intent intent = new Intent(getActivity(), BookingPaymentActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult)
    {
        Crashlytics.logException(new RuntimeException(getString(R.string.error_connect_gps)));
    }
}
