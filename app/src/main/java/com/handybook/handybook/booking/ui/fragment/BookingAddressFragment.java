package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.widget.EmailInputTextView;
import com.handybook.handybook.core.ui.widget.FullNameInputTextView;
import com.handybook.handybook.core.ui.widget.PhoneInputTextView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingAddressFragment extends BookingFlowFragment
{
    @Bind(R.id.booking_address_main_container)
    View mMainContainer;
    @Bind(R.id.next_button)
    Button mButtonNext;
    @Bind(R.id.booking_address_fullname)
    FullNameInputTextView mTextFullName;
    @Bind(R.id.booking_address_phone_prefix)
    TextView mTextPhonePrefix;
    @Bind(R.id.booking_address_phone)
    PhoneInputTextView mTextPhone;

    @Bind(R.id.booking_address_email)
    EmailInputTextView mTextEmail;

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
        final View view = getActivity()
                .getLayoutInflater()
                .inflate(
                        R.layout.fragment_booking_address,
                        container,
                        false
                );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.address));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.booking_address_info_header_layout, header).commit();
        ZipValidationResponse.ZipArea filter = null;
        if (bookingManager.getCurrentRequest() != null)
        {
            filter = bookingManager.getCurrentRequest().getZipArea();
        }
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
                mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                        filter,
                        addr.getAddress1(),
                        addr.getAddress2(),
                        mConfigurationManager.getCachedConfiguration()
                );
            }
        }
        else
        {
            //user is not logged in. There will be a stored email only via the new "onboarding"
            //process.
            String email = mDefaultPreferencesManager.getString(PrefsKey.EMAIL, null);
            if (!TextUtils.isEmpty(email))
            {
                //if there is a stored email on the phone, then show that.
                mTextEmail.setText(email);
                mTextEmail.setVisibility(View.VISIBLE);
            }
            final String prefix = bookingManager.getCurrentQuote().getPhonePrefix();
            mTextPhone.setCountryCode(prefix);
            mTextPhonePrefix.setText(prefix);
        }
        if (mAutoCompleteFragment == null)
        {
            mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                    filter,
                    null,
                    null,
                    mConfigurationManager.getCachedConfiguration()
            );
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.booking_address_fragment_container, mAutoCompleteFragment)
                .commitAllowingStateLoss();
        mButtonNext.setOnClickListener(nextClicked);
        return view;
    }

    private boolean validateFields()
    {
        boolean validate = mAutoCompleteFragment.validateFields();

        if (!mTextFullName.validate()) { validate = false; }
        if (!mTextPhone.validate()) { validate = false; }
        if (hasStoredEmail() && !mTextEmail.validate())
        {
            validate = false;
        }
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
                transaction.setAddress1(mAutoCompleteFragment.mStreet.getAddress());
                transaction.setAddress2(mAutoCompleteFragment.mOther.getText().toString());
                transaction.setFirstName(mTextFullName.getFirstName());
                transaction.setLastName(mTextFullName.getLastName());
                transaction.setPhone(mTextPhone.getPhoneNumber());

                showUiBlockers();
                final String email = mTextEmail.getEmail();
                if (!TextUtils.isEmpty(email) && !userManager.isUserLoggedIn())
                {
                    //following the new onboarding process, if the user hasn't logged in by this
                    //point, it is a new user. We still have to check whether it's existing,
                    //because the user could've changed the email on this screen.
                    bookingManager.getCurrentTransaction().setEmail(email);
                    dataManager.getUserExists(
                            email,
                            new FragmentSafeCallback<UserExistsResponse>(BookingAddressFragment.this)
                            {
                                @Override
                                public void onCallbackSuccess(final UserExistsResponse userExistsResponse)
                                {
                                    bookingManager.getCurrentRequest()
                                                  .setEmail(email);
                                    if (userExistsResponse.exists())
                                    {
                                        //existing user, we must make them login.
                                        //we can only get here IFF both of this happens:
                                        //1. During onboarding, user provided email that didn't exist in the system.
                                        //2. On the address page, the user changed the email to something that did exist in the system.
                                        final Intent intent = new Intent(
                                                getActivity(),
                                                LoginActivity.class
                                        );
                                        intent.putExtra(
                                                LoginActivity.EXTRA_BOOKING_EMAIL,
                                                email
                                        );
                                        intent.putExtra(
                                                LoginActivity.EXTRA_BOOKING_USER_NAME,
                                                userExistsResponse.getFirstName()
                                        );
                                        intent.putExtra(
                                                LoginActivity.EXTRA_FROM_ONBOARDING,
                                                true
                                        );
                                        removeUiBlockers();
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        //new user, we can proceed to payment plan.
                                        proceedToPayment();
                                    }
                                }

                                @Override
                                public void onCallbackError(DataManager.DataManagerError error)
                                {
                                    removeUiBlockers();
                                    dataManagerErrorHandler.handleError(
                                            getActivity(),
                                            error
                                    );

                                }
                            }
                    );
                }
                else
                {
                    proceedToPayment();
                }
            }
        }
    };

    private void proceedToPayment() {
        //before we can actually proceed to payment, we have to get an updated quote
        updateQuote();
    }

    private void updateQuote()
    {
        //UI Blockers should still be showing at this point
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        dataManager.updateQuote(
                transaction.getBookingId(),
                transaction,
                new FragmentSafeCallback<BookingQuote>(this)
                {
                    @Override
                    public void onCallbackSuccess(final BookingQuote newQuote)
                    {
                        removeUiBlockers();
                        transaction.setBookingId(newQuote.getBookingId());

                        if (newQuote.getCoupon() != null)
                        {
                            /*
                            TODO for ugly promo code hotfix
                            for issue in which promo code not entered by user or deeplink
                            was visible in the promo text field

                            at this point, we got a non-null coupon from the server.
                            note in the booking transaction that the coupon should be hidden if:
                            - the transaction coupon is null. this means the user or deeplink didn't set it before
                            - or, the transaction notes that the coupon code should be hidden
                            to account for case in which user goes backwards in the flow
                            (it's only set to false on new quote or user manually enters a coupon)
                             */
                            boolean shouldPromoCodeBeHidden = transaction.getPromoCode() == null
                                    || transaction.shouldPromoCodeBeHidden();
                            transaction.setPromoCode(
                                    newQuote.getCoupon().getCode(),
                                    shouldPromoCodeBeHidden
                            );
                        }
                        BookingQuote.updateQuote(
                                bookingManager.getCurrentQuote(),
                                newQuote
                        );
                        startPaymentActivity();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        // Fail silently and proceed to payment screen without bill - Mngmnt.
                        removeUiBlockers();
                        startPaymentActivity();
                    }
                }
        );
    }

    private void startPaymentActivity()
    {
        final Intent intent = new Intent(getActivity(), BookingPaymentActivity.class);
        startActivity(intent);
    }
}
