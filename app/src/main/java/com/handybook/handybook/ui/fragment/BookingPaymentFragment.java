package com.handybook.handybook.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingCoupon;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.ui.widget.FreezableInputTextView;
import com.handybook.handybook.util.WalletUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.CardException;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingPaymentFragment extends BookingFlowFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private static final String STATE_CARD_NUMBER_HIGHLIGHT = "CARD_NUMBER_HIGHLIGHT";
    private static final String STATE_CARD_EXP_HIGHLIGHT = "CARD_EXP_HIGHLIGHT";
    private static final String STATE_CARD_CVC_HIGHLIGHT = "CARD_CVC_HIGHLIGHT";
    private static final String STATE_USE_EXISTING_CARD = "USE_EXISTING_CARD";

    private boolean mUseExistingCard;
    private boolean mUseAndroidPay;
    private GoogleApiClient mGoogleApiClient;
    private MaskedWallet mMaskedWallet;

    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.change_button)
    Button changeButton;
    @Bind(R.id.promo_button)
    Button promoButton;
    @Bind(R.id.credit_card_text)
    CreditCardNumberInputTextView creditCardText;
    @Bind(R.id.exp_text)
    CreditCardExpDateInputTextView expText;
    @Bind(R.id.cvc_text)
    CreditCardCVCInputTextView cvcText;
    @Bind(R.id.promo_text)
    FreezableInputTextView promoText;
    @Bind(R.id.lock_icon)
    ImageView lockIcon;
    @Bind(R.id.card_icon)
    ImageView creditCardIcon;
    @Bind(R.id.card_extras_layout)
    LinearLayout cardExtrasLayout;
    @Bind(R.id.promo_progress)
    ProgressBar promoProgress;
    @Bind(R.id.promo_layout)
    LinearLayout promoLayout;
    @Bind(R.id.android_pay_button_layout)
    View mAndroidPayButtonLayout;

    public static BookingPaymentFragment newInstance()
    {
        return new BookingPaymentFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            mUseExistingCard = savedInstanceState.getBoolean(STATE_USE_EXISTING_CARD);
        }
        mixpanel.trackEventAppTrackPayment();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletUtils.getEnvironment())
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        checkIsReadyToPayWithAndroidPay();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {

        mixpanel.trackEventPaymentPage(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment, container, false);

        ButterKnife.bind(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        final User.CreditCard card = user != null ? user.getCreditCard() : null;

        if ((card != null && card.getLast4() != null)
                && (savedInstanceState == null || mUseExistingCard))
        {
            mUseExistingCard = true;
            creditCardText.setDisabled(true, "\u2022\u2022\u2022\u2022 " + card.getLast4());
            if (user.isUsingAndroidPay())
            {
                setCardIcon(CreditCard.Type.ANDROID_PAY);
            }
            else
            {
                setCardIcon(card.getBrand());
            }
        }
        else
        {
            allowCardInput();
        }

        creditCardText.addTextChangedListener(cardTextWatcher);
        nextButton.setOnClickListener(nextClicked);
        changeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                allowCardInput();
            }
        });

        lockIcon.setColorFilter(getResources().getColor(R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP);

        final BookingRequest request = bookingManager.getCurrentRequest();

        if (request.getPromoCode() != null) promoLayout.setVisibility(View.GONE);
        else
        {
            promoButton.setOnClickListener(promoClicked);
            updatePromoUI();
        }

        createAndAddWalletFragment(bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());

        return view;
    }

    private void createAndAddWalletFragment(BookingQuote quote, BookingTransaction transaction)
    {
        WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                .setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
                .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
                .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);

        WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                .setEnvironment(WalletUtils.getEnvironment())
                .setFragmentStyle(walletFragmentStyle)
                .setTheme(WalletConstants.THEME_LIGHT)
                .setMode(WalletFragmentMode.BUY_BUTTON)
                .build();
        SupportWalletFragment walletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

        // Now initialize the Wallet Fragment

        final MaskedWalletRequest maskedWalletRequest = WalletUtils.createMaskedWalletRequest(quote, transaction);
        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(WalletUtils.REQUEST_CODE_LOAD_MASKED_WALLET);
        walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.android_pay_button_layout, walletFragment)
                .commit();
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_CARD_NUMBER_HIGHLIGHT))
                creditCardText.highlight();
            if (savedInstanceState.getBoolean(STATE_CARD_EXP_HIGHLIGHT)) expText.highlight();
            if (savedInstanceState.getBoolean(STATE_CARD_CVC_HIGHLIGHT)) cvcText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CARD_NUMBER_HIGHLIGHT, creditCardText.isHighlighted());
        outState.putBoolean(STATE_CARD_EXP_HIGHLIGHT, expText.isHighlighted());
        outState.putBoolean(STATE_CARD_CVC_HIGHLIGHT, cvcText.isHighlighted());
        outState.putBoolean(STATE_USE_EXISTING_CARD, mUseExistingCard);
    }

    public void handleLoadFullWalletResult(final int resultCode, final Intent data, final int errorCode)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (data != null && data.hasExtra(WalletConstants.EXTRA_FULL_WALLET))
                {
                    FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                    finishAndroidPayTransaction(fullWallet);
                }
                else
                {
                    handleWalletError(errorCode);
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            default:
                handleWalletError(errorCode);
                break;
        }
    }

    public void handleLoadMaskedWalletResult(final int resultCode, final Intent data, final int errorCode)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (data != null && data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET))
                {
                    MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                    showMaskedWalletInfo(maskedWallet);
                }
                else
                {
                    handleWalletError(errorCode);
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            default:
                handleWalletError(errorCode);
                break;
        }
    }

    private void handleWalletError(int errorCode)
    {
        switch (errorCode)
        {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                showToast(R.string.spending_limit_exceeded);
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                showToast(R.string.android_pay_unavailable);
                break;
        }
        handleError();
    }

    private void setCardIcon(final String type)
    {
        if (type == null)
        {
            creditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type)
        {
            case Card.AMERICAN_EXPRESS:
                creditCardIcon.setImageResource(R.drawable.ic_card_amex);
                break;

            case Card.DISCOVER:
                creditCardIcon.setImageResource(R.drawable.ic_card_discover);
                break;

            case Card.MASTERCARD:
                creditCardIcon.setImageResource(R.drawable.ic_card_mc);
                break;

            case Card.VISA:
                creditCardIcon.setImageResource(R.drawable.ic_card_visa);
                break;

            default:
                creditCardIcon.setImageResource(R.drawable.ic_card_blank);
        }
    }

    private void setCardIcon(final CreditCard.Type type)
    {
        if (type == null)
        {
            creditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type)
        {
            case AMEX:
                creditCardIcon.setImageResource(R.drawable.ic_card_amex);
                break;

            case DISCOVER:
                creditCardIcon.setImageResource(R.drawable.ic_card_discover);
                break;

            case MASTERCARD:
                creditCardIcon.setImageResource(R.drawable.ic_card_mc);
                break;

            case VISA:
                creditCardIcon.setImageResource(R.drawable.ic_card_visa);
                break;

            case ANDROID_PAY:
                creditCardIcon.setImageResource(R.drawable.ic_android_pay);
                break;

            default:
                creditCardIcon.setImageResource(R.drawable.ic_card_blank);
        }
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(creditCardText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private boolean validateFields()
    {
        boolean validate = true;

        if (!mUseExistingCard && !mUseAndroidPay)
        {
            if (!creditCardText.validate()) validate = false;
            if (!expText.validate()) validate = false;
            if (!cvcText.validate()) validate = false;
        }

        return validate;
    }

    private void allowCardInput()
    {
        setCardIcon(CreditCard.Type.OTHER);
        creditCardText.setText(null);
        creditCardText.setDisabled(false, getString(R.string.credit_card_num));
        changeButton.setVisibility(View.GONE);
        cardExtrasLayout.setVisibility(View.VISIBLE);
        mUseAndroidPay = false;
        mUseExistingCard = false;
        checkIsReadyToPayWithAndroidPay();
    }

    private void checkIsReadyToPayWithAndroidPay()
    {
        if (mGoogleApiClient.isConnected())
        {
            Wallet.Payments.isReadyToPay(mGoogleApiClient).setResultCallback(
                    new ResultCallback<BooleanResult>()
                    {
                        public void onResult(@NonNull BooleanResult result)
                        {
                            if (!mUseExistingCard && !mUseAndroidPay && result.getStatus().isSuccess() && result.getValue()/* && TODO: Add condition US only */)
                            {
                                mAndroidPayButtonLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    private void showMaskedWalletInfo(MaskedWallet maskedWallet)
    {
        creditCardText.setText(null);
        creditCardText.setDisabled(true, maskedWallet.getPaymentDescriptions()[0]);
        changeButton.setVisibility(View.VISIBLE);
        cardExtrasLayout.setVisibility(View.GONE);
        mAndroidPayButtonLayout.setVisibility(View.GONE);
        mUseExistingCard = false;
        mUseAndroidPay = true;
        mMaskedWallet = maskedWallet;
        setCardIcon(CreditCard.Type.ANDROID_PAY);
    }

    private void finishAndroidPayTransaction(FullWallet fullWallet)
    {
        if (!allowCallbacks) return;
        final PaymentMethodToken paymentMethodToken = fullWallet.getPaymentMethodToken();
        final BookingTransaction currentTransaction = bookingManager.getCurrentTransaction();
        currentTransaction.setStripeToken(paymentMethodToken.getToken());
        currentTransaction.setPaymentMethod(User.PAYMENT_METHOD_ANDROID_PAY);
        completeBooking();
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (validateFields())
            {
                disableInputs();
                progressDialog.show();

                if (mUseAndroidPay)
                {
                    final FullWalletRequest fullWalletRequest = WalletUtils.createFullWalletRequest(
                            bookingManager.getCurrentQuote(),
                            bookingManager.getCurrentTransaction(),
                            mMaskedWallet
                    );
                    Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequest,
                            WalletUtils.REQUEST_CODE_LOAD_FULL_WALLET);
                }
                else if (!mUseExistingCard)
                {
                    final Card card = new Card(creditCardText.getCardNumber(), expText.getExpMonth(),
                            expText.getExpYear(), cvcText.getCVC());

                    final Stripe stripe = new Stripe();
                    stripe.createToken(card, bookingManager.getCurrentQuote().getStripeKey(),
                            new TokenCallback()
                            {
                                @Override
                                public void onError(final Exception e)
                                {
                                    if (!allowCallbacks) return;

                                    enableInputs();
                                    progressDialog.dismiss();

                                    if (e instanceof CardException) toast.setText(e.getMessage());
                                    else toast.setText(getString(R.string.default_error_string));
                                    toast.show();
                                }

                                @Override
                                public void onSuccess(final Token token)
                                {
                                    if (!allowCallbacks) return;
                                    bookingManager.getCurrentTransaction().setStripeToken(token.getId());
                                    completeBooking();
                                }
                            });
                }
                else completeBooking();
            }
        }
    };

    private final View.OnClickListener promoClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            final String promoCode = promoText.getText().toString();
            final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
            final boolean hasPromo = (bookingTransaction.promoApplied() != null);

            if (hasPromo || promoCode.length() > 0)
            {
                promoProgress.setVisibility(View.VISIBLE);
                promoButton.setText(null);

                final BookingQuote quote = bookingManager.getCurrentQuote();
                final int bookingId = bookingTransaction.getBookingId();

                if (hasPromo)
                {
                    dataManager.removePromo(bookingId, new DataManager.Callback<BookingCoupon>()
                    {
                        @Override
                        public void onSuccess(final BookingCoupon coupon)
                        {
                            handlePromoSuccess(coupon, quote, bookingTransaction, null);
                            bookingManager.setPromoTabCoupon(null);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            handlePromoFailure(error);
                        }
                    });
                }
                else
                {
                    final User user = userManager.getCurrentUser();
                    final String userId = user != null ? user.getId() : null;
                    final String email = user != null ? user.getEmail() : null;
                    final String authToken = user != null ? user.getAuthToken() : null;

                    dataManager.applyPromo(promoCode, bookingId, userId, email, authToken,
                            new DataManager.Callback<BookingCoupon>()
                            {
                                @Override
                                public void onSuccess(final BookingCoupon coupon)
                                {
                                    handlePromoSuccess(coupon, quote, bookingTransaction, promoCode);
                                }

                                @Override
                                public void onError(final DataManager.DataManagerError error)
                                {
                                    handlePromoFailure(error);
                                }
                            });
                }
            }
        }
    };

    private void completeBooking()
    {
        dataManager.createBooking(bookingManager.getCurrentTransaction(),
                new DataManager.Callback<BookingCompleteTransaction>()
                {
                    @Override
                    public void onSuccess(final BookingCompleteTransaction trans)
                    {

                        //UPGRADE: Should we use this trans or ask the manager for current trans? So much inconsistency....

                        mixpanel.trackEventSubmitPayment(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());
                        mixpanel.trackEventBookingMade(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());

                        if (!allowCallbacks) return;

                        bookingManager.getCurrentTransaction().setBookingId(trans.getId());

                        boolean isNewUser = false;

                        if (userManager.getCurrentUser() == null)
                        {
                            isNewUser = true;

                            final BookingCompleteTransaction.User transUser = trans.getUser();
                            final User user = new User();

                            user.setAuthToken(transUser.getAuthToken());
                            user.setId(transUser.getId());
                            userManager.setCurrentUser(user);
                        }

                        final User user = userManager.getCurrentUser();
                        dataManager.getUser(user.getId(), user.getAuthToken(),
                                new DataManager.Callback<User>()
                                {
                                    @Override
                                    public void onSuccess(final User updatedUser)
                                    {
                                        userManager.setCurrentUser(updatedUser);
                                    }

                                    @Override
                                    public void onError(final DataManager.DataManagerError error)
                                    {
                                    }
                                });

                        bookingManager.setCurrentPostInfo(new BookingPostInfo());

                        final Intent intent = new Intent(getActivity(),
                                BookingConfirmationActivity.class);

                        intent.putExtra(BookingConfirmationActivity.EXTRA_NEW_USER, isNewUser);
                        startActivity(intent);

                        enableInputs();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        if (!allowCallbacks) return;

                        enableInputs();
                        if (mUseAndroidPay)
                        {
                            allowCardInput();
                        }
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
    }

    private final TextWatcher cardTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                                      final int count, final int after)
        {
        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                                  final int before, final int count)
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            if (!mUseExistingCard) setCardIcon(creditCardText.getCardType());
        }
    };

    private void handlePromoSuccess(final BookingCoupon coupon, final BookingQuote quote,
                                    final BookingTransaction transaction, final String promo)
    {
        if (!allowCallbacks) return;
        quote.setPriceTable(coupon.getPriceTable());
        transaction.setPromoApplied(promo);
        updatePromoUI();
        promoText.setText(null);
    }

    private void handlePromoFailure(final DataManager.DataManagerError error)
    {
        if (!allowCallbacks) return;
        updatePromoUI();
        promoText.setText(null);
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void updatePromoUI()
    {
        final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        final String promo = bookingTransaction.promoApplied();
        final boolean applied = (promo != null);

        promoProgress.setVisibility(View.INVISIBLE);
        promoButton.setText(applied ? getString(R.string.remove) : getString(R.string.apply));
        promoText.setDisabled(applied, applied ? promo : getString(R.string.promo_code_opt));
    }

    public void handleError()
    {
        enableInputs();
        nextButton.setClickable(true);
        progressDialog.dismiss();
    }
}
