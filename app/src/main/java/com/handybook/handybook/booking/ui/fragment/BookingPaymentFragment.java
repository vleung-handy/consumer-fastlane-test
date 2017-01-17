package com.handybook.handybook.booking.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.handybook.handybook.booking.model.BookingCompleteTransaction;
import com.handybook.handybook.booking.model.BookingCoupon;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.event.StripeEvent;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.core.ui.view.BillView;
import com.handybook.handybook.core.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.core.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.core.ui.widget.CreditCardIconImageView;
import com.handybook.handybook.core.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.core.util.WalletUtils;
import com.handybook.handybook.library.ui.view.FreezableInputTextView;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.ValidationUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;
import com.stripe.exception.CardException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;

public class BookingPaymentFragment extends BookingFlowFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String STATE_CARD_NUMBER_HIGHLIGHT = "CARD_NUMBER_HIGHLIGHT";
    private static final String STATE_CARD_EXP_HIGHLIGHT = "CARD_EXP_HIGHLIGHT";
    private static final String STATE_CARD_CVC_HIGHLIGHT = "CARD_CVC_HIGHLIGHT";
    private static final String STATE_USE_EXISTING_CARD = "USE_EXISTING_CARD";

    @Inject
    ServicesManager mServicesManager;

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
    CreditCardIconImageView mCreditCardIcon;
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
    @Bind(R.id.booking_payment_terms_of_use_text)
    TextView mTermsOfUseText;
    @Bind(R.id.scan_card_button)
    TextView mScanCardButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.payment_screen_bill)
    BillView mBillView;

    private boolean mUseExistingCard;
    private boolean mUseAndroidPay;
    private GoogleApiClient mGoogleApiClient;
    private MaskedWallet mMaskedWallet;
    private BookingQuote mCurrentQuote;
    private BookingTransaction mCurrentTransaction;

    @OnClick(R.id.scan_card_button)
    public void onScanCardButtonPressed()
    {
        startCardScanActivity();
    }

    private void startCardScanActivity()
    {
        Intent scanIntent = new Intent(getContext(), CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        startActivityForResult(scanIntent, ActivityResult.SCAN_CREDIT_CARD);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResult.SCAN_CREDIT_CARD)
        {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))
            {
                io.card.payment.CreditCard scannedCardResult = data.getParcelableExtra(
                        CardIOActivity.EXTRA_SCAN_RESULT);
                onScannedCardResult(scannedCardResult);
            }
        }
    }

    public void onScannedCardResult(@NonNull final io.card.payment.CreditCard scannedCardResult)
    {
        mCreditCardText.setText(scannedCardResult.cardNumber);
        if (scannedCardResult.isExpiryValid())
        {
            mExpText.setTextFromMonthYear(
                    scannedCardResult.expiryMonth,
                    scannedCardResult.expiryYear
            );
        }
        mCvcText.setText(scannedCardResult.cvv);
    }

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
            Wallet.Payments.changeMaskedWallet(
                    mGoogleApiClient,
                    mMaskedWallet.getGoogleTransactionId(),
                    null,
                    ActivityResult.LOAD_MASKED_WALLET
            );
        }
        else
        {
            final MaskedWalletRequest maskedWalletRequest = WalletUtils.createMaskedWalletRequest(
                    mCurrentQuote,
                    mCurrentTransaction
            );
            Wallet.Payments.loadMaskedWallet(
                    mGoogleApiClient,
                    maskedWalletRequest,
                    ActivityResult.LOAD_MASKED_WALLET
            );
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
        ((BaseApplication) getActivity().getApplication()).inject(this);
        if (savedInstanceState != null)
        {
            mUseExistingCard = savedInstanceState.getBoolean(STATE_USE_EXISTING_CARD);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletUtils.getEnvironment())
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();
        mGoogleApiClient.connect();
        mCurrentQuote = bookingManager.getCurrentQuote();
        mCurrentTransaction = bookingManager.getCurrentTransaction();

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingPaymentShownLog()));
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
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_booking_payment, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.payment));
        showBookingWarningIfApplicable(mCurrentQuote);
        if (mCurrentQuote.hasCouponWarning())
        {
            showToast(mCurrentQuote.getCoupon().getWarning());
        }
        final User user = userManager.getCurrentUser();
        final User.CreditCard card = user != null ? user.getCreditCard() : null;
        if ((card != null && card.getLast4() != null)
                && (savedInstanceState == null || mUseExistingCard)
                && !user.isUsingAndroidPay())
        {
            mUseExistingCard = true;
            mCreditCardText.setDisabled(true, getString(R.string.formatted_last4, card.getLast4()));
            mInfoPaymentLayout.setVisibility(View.VISIBLE);
            mCreditCardIcon.setCardIcon(card.getBrand());
        }

        mCreditCardText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(
                    final CharSequence charSequence, final int start,
                    final int count, final int after
            )
            { }

            @Override
            public void onTextChanged(
                    final CharSequence charSequence, final int start,
                    final int before, final int count
            )
            { }

            @Override
            public void afterTextChanged(final Editable editable)
            {
                if (!mUseExistingCard)
                {
                    mCreditCardIcon.setCardIcon(mCreditCardText.getCardType());
                }
            }
        });
        mLockIcon.setColorFilter(
                ContextCompat.getColor(getContext(), R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP
        );

        initializePromoCodeView();
        initializeBill();
        initializeTermsOfUseText();

        return view;
    }

    /**
     * Show either "apply promo code" button or the promo code input field
     * based on the applied promo code
     */
    private void initializePromoCodeView()
    {
        String appliedPromoCode = mCurrentTransaction.promoApplied();

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

    private void initializeBill()
    {
        if (mCurrentQuote.getBill() == null)
        {
            mBillView.setVisibility(View.GONE);
            initializeBookingHeader();
        }
        else
        {
            mBillView.setVisibility(View.VISIBLE);
            mBillView.setBill(mCurrentQuote.getBill());
        }
    }

    private void initializeBookingHeader()
    {
        final BookingHeaderFragment headerFragment = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, headerFragment).commit();
    }

    /**
     * set the terms of use text and remove the underlines from the link
     * must do this in code because cannot specify textview to render text as html
     * from the layout xml
     */
    private void initializeTermsOfUseText()
    {
        mTermsOfUseText.setText(Html.fromHtml(
                getString(R.string.booking_payment_terms_of_use_agreement)));
        TextUtils.stripUnderlines(mTermsOfUseText);

        //need to override the click event for the link.
        // TODO: is there a cleaner way to override click events for links?
        //splitting out link into a new text view seems too cumbersome
        //due to additional layout xml complexity
        mTermsOfUseText.setMovementMethod(new LinkMovementMethod()
        {
            @Override
            public boolean onTouchEvent(
                    final TextView widget,
                    final Spannable buffer, final MotionEvent event
            )
            {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                {
                    final int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
                    final int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();
                    final Layout layout = widget.getLayout();
                    final int line = layout.getLineForVertical(y);
                    //get the tap position
                    final int off = layout.getOffsetForHorizontal(line, x);

                    //get the link at the tap position
                    final ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0)
                    {
                        showTermsWebViewModal();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void showTermsWebViewModal()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(NavbarWebViewDialogFragment.FRAGMENT_TAG) == null)
        //only show if there isn't an instance of the fragment showing already
        {
            NavbarWebViewDialogFragment df = NavbarWebViewDialogFragment
                    .newInstance(
                            getString(R.string.handy_terms_of_use_title),
                            getString(R.string.handy_terms_of_use_url)
                    );
            df.show(fragmentManager, NavbarWebViewDialogFragment.FRAGMENT_TAG);
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

    public void handleLoadFullWalletResult(
            final int resultCode,
            final Intent data,
            final int errorCode
    )
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

    public void handleLoadMaskedWalletResult(
            final int resultCode,
            final Intent data,
            final int errorCode
    )
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
        enableInputs();
        mNextButton.setClickable(true);
        progressDialog.dismiss();
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
        mCreditCardIcon.setCardIcon(CreditCard.Type.OTHER);
        mCreditCardText.setText(null);
        mCreditCardText.setDisabled(false, getString(R.string.credit_card_num));
        mCardExtrasLayout.setVisibility(View.VISIBLE);
        mScanCardButton.setVisibility(View.VISIBLE);
        mUseAndroidPay = false;
        mUseExistingCard = false;
    }

    private boolean isAndroidPayPromoApplied()
    {
        String androidPayPromoCode = mCurrentQuote.getAndroidPayCouponCode();
        String promoApplied = mCurrentTransaction.promoApplied();
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
    @VisibleForTesting
    protected void showSelectPaymentLayout()
    {
        updateSelectPaymentPromoText();

        mSelectPaymentLayout.setVisibility(View.VISIBLE);
        mInfoPaymentLayout.setVisibility(View.GONE);

        if (isAndroidPayPromoApplied())
        {
            //Remove the applied promo if it is an Android pay one
            //because we don't want credit card users to be able to use it
            removePromo();
        }

        initializePromoCodeView();
    }

    private boolean hasAndroidPayPromoSavings()
    {
        BookingQuote bookingQuote = mCurrentQuote;
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
            String androidPayCouponValueFormatted =
                    mCurrentQuote.getAndroidPayCouponValueFormatted();

            mSelectPaymentPromoText.setText(getString(
                    R.string.booking_payment_android_pay_promo_savings_formatted,
                    androidPayCouponValueFormatted
            ));
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

    @VisibleForTesting
    protected void showPaymentMethodSelection(final @Nullable BooleanResult result)
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
                && mCurrentQuote.isAndroidPayEnabled())
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
     *
     * @param maskedWallet masked wallet
     */
    @VisibleForTesting
    protected void showMaskedWalletInfo(MaskedWallet maskedWallet)
    {
        showInfoPaymentLayout();
        showAndUpdatePromoCodeInput(); //show the promo code input field
        mCreditCardText.setText(null);
        mCreditCardText.setDisabled(true, maskedWallet.getPaymentDescriptions()[0]);
        mCardExtrasLayout.setVisibility(View.GONE);
        mScanCardButton.setVisibility(View.GONE);
        mUseExistingCard = false;
        mUseAndroidPay = true;
        mMaskedWallet = maskedWallet;
        mCreditCardIcon.setCardIcon(CreditCard.Type.ANDROID_PAY);
        applyPromo(mCurrentQuote.getAndroidPayCouponCode());
    }

    private void finishAndroidPayTransaction(FullWallet fullWallet)
    {
        if (!allowCallbacks) { return; }
        String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
        com.stripe.model.Token token = com.stripe.model.Token.GSON.fromJson(
                tokenJSON,
                com.stripe.model.Token.class
        );
        mCurrentTransaction.setStripeToken(token.getId());
        mCurrentTransaction.setPaymentMethod(User.PAYMENT_METHOD_ANDROID_PAY);
        completeBooking();
    }

    @OnClick(R.id.next_button)
    public void onNextClicked(final View view)
    {
        if (validateFields())
        {
            disableInputs();
            progressDialog.show();

            if (mUseAndroidPay)
            {
                final FullWalletRequest fullWalletRequest = WalletUtils.createFullWalletRequest(
                        mCurrentQuote,
                        mCurrentTransaction,
                        mMaskedWallet
                );
                Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequest,
                                               ActivityResult.LOAD_FULL_WALLET
                );
            }
            else if (!mUseExistingCard)
            {
                final Card card = new Card(
                        mCreditCardText.getCardNumber(),
                        mExpText.getExpMonth(),
                        mExpText.getExpYear(),
                        mCvcText.getCVC()
                );
                final String stripeKey = mCurrentQuote.getStripeKey();
                bus.post(new StripeEvent.RequestCreateToken(card, stripeKey));
            }
            else { completeBooking(); }
        }
    }

    @Subscribe
    public void onReceiveCreateTokenSuccess(StripeEvent.ReceiveCreateTokenSuccess event)
    {
        mCurrentTransaction.setStripeToken(event.getToken().getId());
        completeBooking();
    }

    @Subscribe
    public void onReceiveCreateTokenError(StripeEvent.ReceiveCreateTokenError event)
    {
        enableInputs();
        progressDialog.dismiss();

        if (event.getError() instanceof CardException)
        {
            toast.setText(event.getError().getMessage());
        }
        else { toast.setText(getString(R.string.default_error_string)); }
        toast.show();
    }

    @OnClick(R.id.promo_button)
    public void onPromobButtonClicked()
    {
        final String promoCode = mPromoText.getText().toString();
        final boolean hasPromo = (mCurrentTransaction.promoApplied() != null);

        if (hasPromo || promoCode.length() > 0)
        {
            mPromoProgress.setVisibility(View.VISIBLE);
            mPromoButton.setText(null);
            mPromoButton.setVisibility(View.GONE);

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

    //TODO: this was stripped out of promoClicked and may need to be refactored
    private void removePromo()
    {
        final int bookingId = mCurrentTransaction.getBookingId();
        dataManager.removePromo(bookingId, new FragmentSafeCallback<BookingCoupon>(this)
        {
            @Override
            public void onCallbackSuccess(final BookingCoupon coupon)
            {
                // FIXME: This should give us back the updated quote, not coupon!
                removePromoSuccess(coupon, mCurrentQuote, mCurrentTransaction, null);
                bookingManager.setPromoTabCoupon(null);
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error)
            {
                handlePromoFailure(error);
            }
        });
    }

    //TODO: this was stripped out of promoClicked and may need to be refactored
    private void applyPromo(final String promoCode)
    {
        if (ValidationUtils.isNullOrEmpty(promoCode)) { return; }

        bus.post(new LogEvent.AddLogEvent(
                new BookingFunnelLog.ReferralBookingFunnelCodeEnteredLog(promoCode)));

        final int bookingId = mCurrentTransaction.getBookingId();
        final User user = userManager.getCurrentUser();
        final String userId = user != null ? user.getId() : null;
        final String email = user != null ? user.getEmail() : null;

        dataManager.applyPromo(
                promoCode,
                bookingId,
                userId,
                email,
                new FragmentSafeCallback<BookingQuote>(this)
                {
                    @Override
                    public void onCallbackSuccess(final BookingQuote bookingQuote)
                    {
                        bus.post(
                                new LogEvent.AddLogEvent(
                                        new BookingFunnelLog.ReferralBookingFunnelCodeEnteredLog(
                                                promoCode
                                        )));
                        applyPromoSuccess(bookingQuote, mCurrentTransaction, promoCode);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        handlePromoFailure(error);
                    }
                }
        );
    }

    private void completeBooking()
    {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingRequestSubmittedLog()));
        final Context applicationContext = getActivity().getApplicationContext();
        // The variable referrerToken may be null but that's ok! It just means that the booking
        // flow was not initiated through Button.
        final String referrerToken = com.usebutton.sdk.Button
                .getButton(applicationContext)
                .getReferrerToken();
        mCurrentTransaction.setReferrerToken(referrerToken);
        dataManager.createBooking(
                mCurrentTransaction,
                new FragmentSafeCallback<BookingCompleteTransaction>(this)
                {
                    @Override
                    public void onCallbackSuccess(final BookingCompleteTransaction trans)
                    {
                        bus.post(new LogEvent.AddLogEvent(
                                new BookingFunnelLog.BookingRequestSuccessLog(trans.getId())
                        ));
                        bus.post(new LogEvent.AddLogEvent(
                                new BookingFunnelLog.BookingRequestMadeLog(
                                        bookingManager.getCurrentQuote(),
                                        mCurrentTransaction,
                                        trans,
                                        bookingManager.getExtraHours(mCurrentTransaction),
                                        mServicesManager.getServiceNameByServiceId(
                                                mCurrentTransaction.getServiceId())
                                )));
                        if (!allowCallbacks) { return; }
                        mCurrentTransaction.setBookingId(trans.getId());
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
                        if (user != null)
                        {
                            bus.post(new HandyEvent.RequestUser(
                                             user.getId(),
                                             user.getAuthToken(),
                                             null
                                     )
                            );
                        }
                        bookingManager.setCurrentPostInfo(new BookingPostInfo());
                        bookingManager.setCurrentFinalizeBookingRequestPayload(
                                new FinalizeBookingRequestPayload()
                        );
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingFinalizeActivity.class
                        );
                        intent.putExtra(BookingFinalizeActivity.EXTRA_NEW_USER, isNewUser);
                        intent.putExtra(
                                BookingFinalizeActivity.EXTRA_INSTRUCTIONS,
                                trans.getInstructions()
                        );
                        intent.putExtra(
                                BookingFinalizeActivity.EXTRA_ENTRY_METHODS_INFO,
                                trans.getEntryMethodsInfo()
                        );
                        startActivity(intent);
                        enableInputs();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        bus.post(new LogEvent.AddLogEvent(
                                new BookingFunnelLog.BookingRequestErrorLog(error.getMessage())
                        ));
                        if (!allowCallbacks) { return; }
                        enableInputs();
                        checkAndShowPaymentMethodSelection();
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    /**
     * This is a new method that handles the updated API that returns the whole quote not a subset
     * Api Endpooint: /v3/quotes/{1245}/set_coupon
     */
    private void applyPromoSuccess(
            final BookingQuote newQuote,
            final BookingTransaction transaction,
            final String promo
    )
    {
        if (!allowCallbacks) { return; }
        // FIXME: Here we should eb storing this instead of replacing it's insides....ffs
        mCurrentQuote.setBill(newQuote.getBill());
        initializeBill();
        mCurrentQuote.setPriceTable(newQuote.getPriceTable());
        mCurrentQuote.setSurgePriceTable(newQuote.getSurgePriceTable());
        mCurrentQuote.setCoupon(newQuote.getCoupon());
        mCurrentQuote.setCommitmentPrices(newQuote.getCommitmentPrices());
        mCurrentQuote.setupCommitmentPricingStructure();
        mCurrentQuote.setActiveCommitmentTypes(newQuote.getActiveCommitmentTypes());
        showBookingWarningIfApplicable(mCurrentQuote);
        transaction.setPromoApplied(promo);
        updatePromoUI();
        mPromoText.setText(null);
    }

    private void removePromoSuccess(
            final BookingCoupon coupon,
            final BookingQuote quote,
            final BookingTransaction transaction,
            final String promo
    )
    {
        if (!allowCallbacks) { return; }
        // FIXME: Here we should be toring the new quote in bookingManager
        quote.setPriceTable(coupon.getPriceTable());
        if (coupon.getCommitmentPrices() != null)
        {
            quote.setCommitmentPrices(coupon.getCommitmentPrices());
            quote.setupCommitmentPricingStructure();
        }
        transaction.setPromoApplied(promo);
        updatePromoUI();
        mPromoText.setText(null);
    }

    private void showBookingWarningIfApplicable(BookingQuote quote)
    {
        if (quote.hasCouponWarning())
        {
            showToast(quote.getCoupon().getWarning());
        }
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
        final String promo = mCurrentTransaction.promoApplied();
        final boolean applied = (promo != null);
        mPromoProgress.setVisibility(View.INVISIBLE);
        mPromoButton.setText(applied ? getString(R.string.remove) : getString(R.string.apply));
        mPromoButton.setVisibility(View.VISIBLE);
        String promoCodeDisplayString;
        if (applied)
        {
            if (isAndroidPayPromoApplied()) //show the obfuscated Android pay promo code
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

}
