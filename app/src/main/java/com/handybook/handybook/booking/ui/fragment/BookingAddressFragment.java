package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.widget.FullNameInputTextView;
import com.handybook.handybook.core.ui.widget.PhoneInputTextView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class BookingAddressFragment extends BookingFlowFragment {

    @BindView(R.id.booking_address_main_container)
    View mMainContainer;
    @BindView(R.id.next_button)
    Button mButtonNext;
    @BindView(R.id.booking_address_fullname)
    FullNameInputTextView mTextFullName;
    @BindView(R.id.booking_address_phone_prefix)
    TextView mTextPhonePrefix;
    @BindView(R.id.booking_address_phone)
    PhoneInputTextView mTextPhone;

    AutoCompleteAddressFragment mAutoCompleteFragment;
    private static final String AC_FRAG_TAG = AutoCompleteAddressFragment.class.getName();

    public static BookingAddressFragment newInstance(@Nullable Bundle extras) {
        BookingAddressFragment fragment = new BookingAddressFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAddressShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_address, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.address));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.booking_address_info_header_layout, header).commit();
        ZipValidationResponse.ZipArea filter = null;
        if (bookingManager.getCurrentRequest() != null) {
            filter = bookingManager.getCurrentRequest().getZipArea();
        }
        final User user = userManager.getCurrentUser();
        mAutoCompleteFragment
                = (AutoCompleteAddressFragment) getChildFragmentManager().findFragmentByTag(
                AC_FRAG_TAG);
        if (user != null) {
            mTextFullName.setText(user.getFirstName() + " " + user.getLastName());
            mTextPhone.setCountryCode(user.getPhonePrefix());
            mTextPhone.setText(user.getPhone());
            mTextPhonePrefix.setText(user.getPhonePrefix());

            final User.Address addr = user.getAddress();
            if (addr != null && mAutoCompleteFragment == null) {
                mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                        filter,
                        addr.getAddress1(),
                        addr.getAddress2(),
                        addr.getCity(),
                        addr.getState(),
                        mConfigurationManager.getCachedConfiguration(),
                        false
                );
            }
        }
        else {
            //user is not logged in.
            final String prefix = bookingManager.getCurrentQuote().getPhonePrefix();
            mTextPhone.setCountryCode(prefix);
            mTextPhonePrefix.setText(prefix);
        }
        if (mAutoCompleteFragment == null) {
            mAutoCompleteFragment = AutoCompleteAddressFragment.newInstance(
                    filter,
                    null,
                    null,
                    null,
                    null,
                    mConfigurationManager.getCachedConfiguration(),
                    false
            );
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.booking_address_fragment_container,
                        mAutoCompleteFragment,
                        AC_FRAG_TAG
                )
                .commitAllowingStateLoss();
        mButtonNext.setOnClickListener(nextClicked);
        return view;
    }

    private boolean validateFields() {
        boolean validate = mAutoCompleteFragment.validateFields();

        if (!mTextFullName.validate()) { validate = false; }
        if (!mTextPhone.validate()) { validate = false; }
        return validate;
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {

                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingAddressSubmittedLog()));
                final BookingTransaction transaction = bookingManager.getCurrentTransaction();
                transaction.setAddress1(mAutoCompleteFragment.getStreet());
                transaction.setAddress2(mAutoCompleteFragment.getApt());
                transaction.setCity(mAutoCompleteFragment.getCity());
                transaction.setState(mAutoCompleteFragment.getState());
                transaction.setFirstName(mTextFullName.getFirstName());
                transaction.setLastName(mTextFullName.getLastName());
                transaction.setPhone(mTextPhone.getPhoneNumber());
                updateQuote();
            }
        }
    };

    private void updateQuote() {
        showProgressSpinner(true);
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        dataManager.updateQuote(
                transaction.getBookingId(),
                transaction,
                new FragmentSafeCallback<BookingQuote>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingQuote newQuote) {
                        hideProgressSpinner();
                        transaction.setBookingId(newQuote.getBookingId());

                        if (newQuote.getCoupon() != null) {
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
                                                              ||
                                                              transaction.shouldPromoCodeBeHidden();
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
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // Fail silently and proceed to payment screen without bill - Mngmnt.
                        hideProgressSpinner();
                        startPaymentActivity();
                    }
                }
        );
    }

    private void startPaymentActivity() {
        final Intent intent = new Intent(getActivity(), BookingPaymentActivity.class);
        intent.putExtras(createProgressBundle());
        startActivity(intent);
    }
}
