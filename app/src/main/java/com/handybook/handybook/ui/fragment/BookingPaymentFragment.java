package com.handybook.handybook.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingCoupon;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.MixpanelEvent;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.ui.widget.FreezableInputTextView;
import com.handybook.handybook.util.ValidationUtils;
import com.handybook.handybook.util.WalletUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.CardException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingPaymentFragment extends BookingFlowFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    //TODO: would be nice to have a ViewModel
    private static final String STATE_CARD_NUMBER_HIGHLIGHT = "CARD_NUMBER_HIGHLIGHT";
    private static final String STATE_CARD_EXP_HIGHLIGHT = "CARD_EXP_HIGHLIGHT";
    private static final String STATE_CARD_CVC_HIGHLIGHT = "CARD_CVC_HIGHLIGHT";
    private static final String STATE_USE_EXISTING_CARD = "USE_EXISTING_CARD";

    private boolean mUseExistingCard;
    private boolean mUseAndroidPay;
    private GoogleApiClient mGoogleApiClient;
    private MaskedWallet mMaskedWallet;

    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.promo_button)
    Button mPromoButton;
    @Bind(R.id.credit_card_text)
    CreditCardNumberInputTextView mCreditCardText;
    @Bind(R.id.exp_text)
    CreditCardExpDateInputTextView mExpText;
    @Bind(R.id.cvc_text)
    CreditCardCVCInputTextView mCvcText;
    @Bind(R.id.promo_text)
    FreezableInputTextView mPromoText;
    @Bind(R.id.lock_icon)
    ImageView mLockIcon;
    @Bind(R.id.card_icon)
    ImageView mCreditCardIcon;
    @Bind(R.id.card_extras_layout)
    LinearLayout mCardExtrasLayout;
    @Bind(R.id.promo_progress)
    ProgressBar mPromoProgress;
    @Bind(R.id.promo_layout)
    LinearLayout mPromoLayout;
    @Bind(R.id.select_payment_layout)
    View mSelectPaymentLayout;
    @Bind(R.id.info_payment_layout)
    View mInfoPaymentLayout;
    @Bind(R.id.apply_promo_button)
    View mApplyPromoButton;
    @Bind(R.id.change_button)
    View mChangeButton;
    @Bind(R.id.booking_select_payment_promo_text)
    TextView mSelectPaymentPromoText;

    @OnClick(R.id.enter_credit_card_button)
    public void onEnterCreditCardButtonClicked()
    {
        allowCardInput();
    }

    @OnClick(R.id.android_pay_button)
    public void onBookWithAndroidPayClicked()
    {
        if (mMaskedWallet != null)
        {
            Wallet.Payments.changeMaskedWallet(mGoogleApiClient,
                    mMaskedWallet.getGoogleTransactionId(),
                    null,
                    ActivityResult.LOAD_MASKED_WALLET);
        }
        else
        {
            final MaskedWalletRequest maskedWalletRequest = WalletUtils.createMaskedWalletRequest(
                    bookingManager.getCurrentQuote(),
                    bookingManager.getCurrentTransaction()
            );
            Wallet.Payments.loadMaskedWallet(mGoogleApiClient,
                    maskedWalletRequest,
                    ActivityResult.LOAD_MASKED_WALLET);
        }
    }

    @OnClick(R.id.change_button)
    public void onChangeButtonClicked()
    {
        mUseExistingCard = false;
        checkAndShowPaymentMethodSelection();
    }

    @OnClick(R.id.apply_promo_button)
    public void onApplyPromoButtonClicked()
    {
        showAndUpdatePromoCodeInput();
    }

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
    public void onConnected(Bundle bundle)
    {
        checkAndShowPaymentMethodSelection();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        checkAndShowPaymentMethodSelection();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        mixpanel.trackEventPaymentPage(bookingManager.getCurrentRequest(),
                bookingManager.getCurrentQuote(),
                bookingManager.getCurrentTransaction());
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment, container, false);

        ButterKnife.bind(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        final User.CreditCard card = user != null ? user.getCreditCard() : null;

        if ((card != null && card.getLast4() != null)
                && (savedInstanceState == null || mUseExistingCard)
                && !user.isUsingAndroidPay())
        {
            mUseExistingCard = true;
            mCreditCardText.setDisabled(true, getString(R.string.last4, card.getLast4()));
            mInfoPaymentLayout.setVisibility(View.VISIBLE);
            setCardIcon(card.getBrand());
        }

        mCreditCardText.addTextChangedListener(cardTextWatcher);
        mNextButton.setOnClickListener(nextClicked);

        mLockIcon.setColorFilter(getResources().getColor(R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP);

        //show the apply promo code views
        mPromoButton.setOnClickListener(promoClicked);
        showViewForPromoCodeApplied();

        mGoogleApiClient.connect();

        return view;
    }

    /**
     * Show either "apply promo code" button or the promo code input field
     * based on the applied promo code
     */
    private void showViewForPromoCodeApplied() //TODO: better name?
    {
        String appliedPromoCode = bookingManager.getCurrentTransaction().promoApplied();

        if (ValidationUtils.isNullOrEmpty(appliedPromoCode))
        {
            //no promo code. show the "Apply Promo Code" button
            showApplyPromoCodeButton();
        }
        else //has a promo code. display it
        {
            showAndUpdatePromoCodeInput();
        }
    }

    /**
     * shows and updates the promo code input layout
     * also hides the "apply promo code" button
     */
    private void showAndUpdatePromoCodeInput()
    {
        mApplyPromoButton.setVisibility(View.GONE);
        mPromoLayout.setVisibility(View.VISIBLE);
        updatePromoUI(); //update the promo input UI
    }

    /**
     * shows the "apply promo code" button
     * also hides the promo code input layout
     */
    private void showApplyPromoCodeButton()
    {
        mPromoLayout.setVisibility(View.GONE);
        mApplyPromoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView()
    {
        mGoogleApiClient.disconnect();
        super.onDestroyView();
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_CARD_NUMBER_HIGHLIGHT))
            {
                mCreditCardText.highlight();
            }
            if (savedInstanceState.getBoolean(STATE_CARD_EXP_HIGHLIGHT))
            {
                mExpText.highlight();
            }
            if (savedInstanceState.getBoolean(STATE_CARD_CVC_HIGHLIGHT))
            {
                mCvcText.highlight();
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CARD_NUMBER_HIGHLIGHT, mCreditCardText.isHighlighted());
        outState.putBoolean(STATE_CARD_EXP_HIGHLIGHT, mExpText.isHighlighted());
        outState.putBoolean(STATE_CARD_CVC_HIGHLIGHT, mCvcText.isHighlighted());
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
            mCreditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type)
        {
            case Card.AMERICAN_EXPRESS:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_amex);
                break;

            case Card.DISCOVER:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_discover);
                break;

            case Card.MASTERCARD:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_mc);
                break;

            case Card.VISA:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_visa);
                break;

            default:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_blank);
        }
    }

    private void setCardIcon(final CreditCard.Type type)
    {
        if (type == null)
        {
            mCreditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type)
        {
            case AMEX:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_amex);
                break;

            case DISCOVER:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_discover);
                break;

            case MASTERCARD:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_mc);
                break;

            case VISA:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_visa);
                break;

            case ANDROID_PAY:
                mCreditCardIcon.setImageResource(R.drawable.ic_android_pay);
                break;

            default:
                mCreditCardIcon.setImageResource(R.drawable.ic_card_blank);
        }
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mNextButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCreditCardText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    private boolean validateFields()
    {
        boolean validate = true;

        if (!mUseExistingCard && !mUseAndroidPay)
        {
            if (!mCreditCardText.validate()) { validate = false; }
            if (!mExpText.validate()) { validate = false; }
            if (!mCvcText.validate()) { validate = false; }
        }

        return validate;
    }

    /**
     * User is using a credit card. Show the relevant display
     */
    private void allowCardInput()
    {
        showInfoPaymentLayout();
        showAndUpdatePromoCodeInput(); //show the promo code input field
        setCardIcon(CreditCard.Type.OTHER);
        mCreditCardText.setText(null);
        mCreditCardText.setDisabled(false, getString(R.string.credit_card_num));
        mCardExtrasLayout.setVisibility(View.VISIBLE);
        mUseAndroidPay = false;
        mUseExistingCard = false;
    }

    private boolean isAndroidPayPromoApplied()
    {
        String androidPayPromoCode = bookingManager.getCurrentQuote().getAndroidPayCouponCode();
        String promoApplied = bookingManager.getCurrentTransaction().promoApplied();
        return (!ValidationUtils.isNullOrEmpty(androidPayPromoCode)
                && androidPayPromoCode.equalsIgnoreCase(promoApplied));
    }

    private void showInfoPaymentLayout()
    {
        mSelectPaymentLayout.setVisibility(View.GONE);
        mInfoPaymentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * shows the layout that allows the user to select between Android Pay and credit card
     */
    private void showSelectPaymentLayout()
    {
        updateSelectPaymentPromoText();

        mSelectPaymentLayout.setVisibility(View.VISIBLE);
        mInfoPaymentLayout.setVisibility(View.GONE);

        if(isAndroidPayPromoApplied())
        {
            //Remove the applied promo if it is an Android pay one
            //because we don't want credit card users to be able to use it
            removePromo();
        }

        showViewForPromoCodeApplied();
        bus.post(new MixpanelEvent.TrackPaymentMethodShownEvent(MixpanelEvent.PaymentMethod.ANDROID_PAY));
    }

    private boolean hasAndroidPayPromoSavings()
    {
        BookingQuote bookingQuote = bookingManager.getCurrentQuote();
        String androidPayCoupon = bookingQuote.getAndroidPayCouponCode();
        String androidPayCouponValueFormatted = bookingQuote.getAndroidPayCouponValueFormatted();

        /*
        check to see if either the coupon code or coupon value is null or empty
        checking both just in case we get something like:

        coupon = "COUPONCODE"
        formatted value = ""
         */

        return !ValidationUtils.isNullOrEmpty(androidPayCoupon)
                && !ValidationUtils.isNullOrEmpty(androidPayCouponValueFormatted);
    }

    /**
     * updates the select payment promo text visibility and value,
     * currently based on whether or not the user has Android Pay promo savings
     */
    private void updateSelectPaymentPromoText()
    {
        if (hasAndroidPayPromoSavings())
        {
            String androidPayCouponValueFormatted = bookingManager.getCurrentQuote().getAndroidPayCouponValueFormatted();

            mSelectPaymentPromoText.setText(getString(
                    R.string.booking_payment_android_pay_promo_savings_formatted,
                    androidPayCouponValueFormatted));
            mSelectPaymentPromoText.setVisibility(View.VISIBLE);
        }
        else
        {
            mSelectPaymentPromoText.setVisibility(View.GONE);
        }
    }

    private void checkAndShowPaymentMethodSelection()
    {
        progressDialog.show();
        if (mGoogleApiClient.isConnected())
        {
            Wallet.Payments.isReadyToPay(mGoogleApiClient).setResultCallback(
                    new ResultCallback<BooleanResult>()
                    {
                        public void onResult(@NonNull BooleanResult result)
                        {
                            showPaymentMethodSelection(result);
                            progressDialog.dismiss();
                        }
                    });
        }
        else
        {
            showPaymentMethodSelection(null);
            progressDialog.dismiss();
        }
    }

    private void showPaymentMethodSelection(final @Nullable BooleanResult result)
    {
        if (!mUseExistingCard)
        {
            if (shouldShowAndroidPay(result))
            {
                showSelectPaymentLayout();
            }
            else
            {
                // since Android Pay cannot be used, go ahead and display credit card input fields
                showUnchangeableCardInputFields();
            }
        }
    }

    private void showUnchangeableCardInputFields()
    {
        mChangeButton.setVisibility(View.GONE);
        allowCardInput();
    }

    // Only show Android Pay for new customers and for customers who already used Android Pay
    private boolean shouldShowAndroidPay(final @Nullable BooleanResult result)
    {
        /* TODO: Add condition US only */
        if (result != null && result.getStatus().isSuccess() && result.getValue()
                && bookingManager.getCurrentQuote().isAndroidPayEnabled())
        {
            final User currentUser = userManager.getCurrentUser();
            return currentUser == null || currentUser.isUsingAndroidPay();
        }
        else
        {
            return false;
        }
    }

    /**
     * User is using Android Pay. Show the relevant info
     * @param maskedWallet
     */
    private void showMaskedWalletInfo(MaskedWallet maskedWallet)
    {
        showInfoPaymentLayout();
        showAndUpdatePromoCodeInput(); //show the promo code input field
        mCreditCardText.setText(null);
        mCreditCardText.setDisabled(true, maskedWallet.getPaymentDescriptions()[0]);
        mCardExtrasLayout.setVisibility(View.GONE);
        mUseExistingCard = false;
        mUseAndroidPay = true;
        mMaskedWallet = maskedWallet;
        setCardIcon(CreditCard.Type.ANDROID_PAY);

        applyAndroidPayCoupon();

        bus.post(new MixpanelEvent.TrackPaymentMethodProvidedEvent(MixpanelEvent.PaymentMethod.ANDROID_PAY));
    }

    private void applyAndroidPayCoupon()
    {
        //apply android pay coupon
        String promoCode = bookingManager.getCurrentQuote().getAndroidPayCouponCode();
        applyPromo(promoCode);
    }

    private void finishAndroidPayTransaction(FullWallet fullWallet)
    {
        if (!allowCallbacks) { return; }
        String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
        com.stripe.model.Token token = com.stripe.model.Token.GSON.fromJson(tokenJSON, com.stripe.model.Token.class);
        final BookingTransaction currentTransaction = bookingManager.getCurrentTransaction();
        currentTransaction.setStripeToken(token.getId());
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
                            ActivityResult.LOAD_FULL_WALLET);
                }
                else if (!mUseExistingCard)
                {
                    final Card card = new Card(mCreditCardText.getCardNumber(), mExpText.getExpMonth(),
                            mExpText.getExpYear(), mCvcText.getCVC());

                    //TODO: we should move these to a service
                    final Stripe stripe = new Stripe();
                    stripe.createToken(card, bookingManager.getCurrentQuote().getStripeKey(),
                            new TokenCallback()
                            {
                                @Override
                                public void onError(final Exception e)
                                {
                                    if (!allowCallbacks) { return; }

                                    enableInputs();
                                    progressDialog.dismiss();

                                    if (e instanceof CardException)
                                    {
                                        toast.setText(e.getMessage());
                                    }
                                    else
                                    {
                                        toast.setText(getString(R.string.default_error_string));
                                    }
                                    toast.show();
                                }

                                @Override
                                public void onSuccess(final Token token)
                                {
                                    if (!allowCallbacks) { return; }
                                    bookingManager.getCurrentTransaction().setStripeToken(token.getId());
                                    completeBooking();
                                }
                            });
                }
                else { completeBooking(); }
            }
        }
    };

    private final View.OnClickListener promoClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            final String promoCode = mPromoText.getText().toString();
            final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
            final boolean hasPromo = (bookingTransaction.promoApplied() != null);

            if (hasPromo || promoCode.length() > 0)
            {
                mPromoProgress.setVisibility(View.VISIBLE);
                mPromoButton.setText(null);

                if (hasPromo)
                {
                    removePromo();
                }
                else
                {
                    applyPromo(promoCode);
                }
            }
        }
    };

    //TODO: this was stripped out of promoClicked and may need to be refactored
    private void removePromo()
    {
        final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        final int bookingId = bookingTransaction.getBookingId();
        final BookingQuote quote = bookingManager.getCurrentQuote();
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

    //TODO: this was stripped out of promoClicked and may need to be refactored
    private void applyPromo(final String promoCode)
    {
        if(ValidationUtils.isNullOrEmpty(promoCode)) return;

        final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        final int bookingId = bookingTransaction.getBookingId();
        final BookingQuote quote = bookingManager.getCurrentQuote();

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

    private void completeBooking()
    {
        dataManager.createBooking(bookingManager.getCurrentTransaction(),
                new DataManager.Callback<BookingCompleteTransaction>()
                {
                    @Override
                    public void onSuccess(final BookingCompleteTransaction trans)
                    {

                        //UPGRADE: Should we use this trans or ask the manager for current trans? So much inconsistency....

                        if (mUseAndroidPay)
                        {
                            bus.post(new MixpanelEvent.TrackBookingCompletedWithPaymentMethodEvent(MixpanelEvent.PaymentMethod.ANDROID_PAY));
                        }
                        mixpanel.trackEventSubmitPayment(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());
                        mixpanel.trackEventBookingMade(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());

                        if (!allowCallbacks) { return; }

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
                        if (!allowCallbacks) { return; }
                        enableInputs();
                        checkAndShowPaymentMethodSelection();
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
            if (!mUseExistingCard) { setCardIcon(mCreditCardText.getCardType()); }
        }
    };

    private void handlePromoSuccess(final BookingCoupon coupon, final BookingQuote quote,
                                    final BookingTransaction transaction, final String promo)
    {
        if (!allowCallbacks) { return; }
        quote.setPriceTable(coupon.getPriceTable());
        transaction.setPromoApplied(promo);
        updatePromoUI();
        mPromoText.setText(null);
    }

    private void handlePromoFailure(final DataManager.DataManagerError error)
    {
        if (!allowCallbacks) { return; }
        updatePromoUI();
        mPromoText.setText(null);
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void updatePromoUI()
    {
        final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        final String promo = bookingTransaction.promoApplied();
        final boolean applied = (promo != null);

        mPromoProgress.setVisibility(View.INVISIBLE);
        mPromoButton.setText(applied ? getString(R.string.remove) : getString(R.string.apply));

        String promoCodeDisplayString;
        if(applied)
        {
            if(isAndroidPayPromoApplied()) //show the obfuscated Android pay promo code
            {
                promoCodeDisplayString = getString(R.string.android_pay_obfuscated_promo_code);
            }
            else //show the actual promo code
            {
                promoCodeDisplayString = promo;
            }
        }
        else //show a hint
        {
            promoCodeDisplayString = getString(R.string.promo_code_opt);
        }
        mPromoText.setDisabled(applied, promoCodeDisplayString);
    }

    public void handleError()
    {
        enableInputs();
        mNextButton.setClickable(true);
        progressDialog.dismiss();
    }
}
