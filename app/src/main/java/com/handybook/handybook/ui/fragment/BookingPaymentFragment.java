package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.handybook.handybook.event.StripeEvent;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.ui.widget.CreditCardIconImageView;
import com.handybook.handybook.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.ui.widget.FreezableInputTextView;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;
import com.stripe.exception.CardException;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingPaymentFragment extends BookingFlowFragment {
    private static final String STATE_CARD_NUMBER_HIGHLIGHT = "CARD_NUMBER_HIGHLIGHT";
    private static final String STATE_CARD_EXP_HIGHLIGHT = "CARD_EXP_HIGHLIGHT";
    private static final String STATE_CARD_CVC_HIGHLIGHT = "CARD_CVC_HIGHLIGHT";
    private static final String STATE_USE_EXISTING_CARD = "USE_EXISTING_CARD";

    private boolean useExistingCard;

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
    CreditCardIconImageView creditCardIcon;
    @Bind(R.id.card_extras_layout)
    LinearLayout cardExtrasLayout;
    @Bind(R.id.promo_progress)
    ProgressBar promoProgress;
    @Bind(R.id.promo_layout)
    LinearLayout promoLayout;

    public static BookingPaymentFragment newInstance() {
        return new BookingPaymentFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            useExistingCard = savedInstanceState.getBoolean(STATE_USE_EXISTING_CARD);
        }
        mixpanel.trackEventAppTrackPayment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {

        mixpanel.trackEventPaymentPage(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment,container, false);

        ButterKnife.bind(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        final User.CreditCard card = user != null ? user.getCreditCard() : null;

        if ((card != null && card.getLast4() != null)
                && (savedInstanceState == null || useExistingCard)) {
            useExistingCard = true;
            creditCardText.setDisabled(true, "\u2022\u2022\u2022\u2022 " + card.getLast4());
            creditCardIcon.setCardIcon(card.getBrand());
        }
        else allowCardInput();

        creditCardText.addTextChangedListener(cardTextWatcher);
        nextButton.setOnClickListener(nextClicked);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                allowCardInput();
            }
        });

        lockIcon.setColorFilter(getResources().getColor(R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP);

        final BookingRequest request = bookingManager.getCurrentRequest();

        if (request.getPromoCode() != null) promoLayout.setVisibility(View.GONE);
        else {
            promoButton.setOnClickListener(promoClicked);
            updatePromoUI();
        }

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_CARD_NUMBER_HIGHLIGHT)) creditCardText.highlight();
            if (savedInstanceState.getBoolean(STATE_CARD_EXP_HIGHLIGHT)) expText.highlight();
            if (savedInstanceState.getBoolean(STATE_CARD_CVC_HIGHLIGHT)) cvcText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CARD_NUMBER_HIGHLIGHT, creditCardText.isHighlighted());
        outState.putBoolean(STATE_CARD_EXP_HIGHLIGHT, expText.isHighlighted());
        outState.putBoolean(STATE_CARD_CVC_HIGHLIGHT, cvcText.isHighlighted());
        outState.putBoolean(STATE_USE_EXISTING_CARD, useExistingCard);
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(creditCardText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private boolean validateFields() {
        boolean validate = true;

        if (!useExistingCard) {
            if (!creditCardText.validate()) validate = false;
            if (!expText.validate()) validate = false;
            if (!cvcText.validate()) validate = false;
        }

        return validate;
    }

    private void allowCardInput() {
        creditCardIcon.setCardIcon(CreditCard.Type.OTHER);
        creditCardText.setText(null);
        creditCardText.setDisabled(false, getString(R.string.credit_card_num));
        changeButton.setVisibility(View.GONE);
        cardExtrasLayout.setVisibility(View.VISIBLE);
        useExistingCard = false;
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                if (!useExistingCard) {
                    final Card card = new Card(creditCardText.getCardNumber(), expText.getExpMonth(),
                            expText.getExpYear(), cvcText.getCVC());
                    bus.post(new StripeEvent.RequestCreateToken(card));
                } else completeBooking();
            }
        }
    };

    @Subscribe
    public void onReceiveCreateTokenSuccess(StripeEvent.ReceiveCreateTokenSuccess event)
    {
        bookingManager.getCurrentTransaction().setStripeToken(event.token.getId());
        completeBooking();
    }

    @Subscribe
    public void onReceiveCreateTokenError(StripeEvent.ReceiveCreateTokenError event)
    {
        enableInputs();
        progressDialog.dismiss();

        if (event.error instanceof CardException) toast.setText(event.error.getMessage());
        else toast.setText(getString(R.string.default_error_string));
        toast.show();
    }


    private final View.OnClickListener promoClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final String promoCode = promoText.getText().toString();
            final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
            final boolean hasPromo = (bookingTransaction.promoApplied() != null);

            if (hasPromo || promoCode.length() > 0) {
                promoProgress.setVisibility(View.VISIBLE);
                promoButton.setText(null);

                final BookingQuote quote = bookingManager.getCurrentQuote();
                final int bookingId = bookingTransaction.getBookingId();

                if (hasPromo) {
                    dataManager.removePromo(bookingId, new DataManager.Callback<BookingCoupon>() {
                            @Override
                            public void onSuccess(final BookingCoupon coupon) {
                                handlePromoSuccess(coupon, quote, bookingTransaction, null);
                                bookingManager.setPromoTabCoupon(null);
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error) {
                                handlePromoFailure(error);
                            }
                    });
                }
                else {
                    final User user = userManager.getCurrentUser();
                    final String userId = user != null ? user.getId() : null;
                    final String email = user != null ? user.getEmail() : null;
                    final String authToken = user != null ? user.getAuthToken() : null;

                    dataManager.applyPromo(promoCode, bookingId, userId, email, authToken,
                            new DataManager.Callback<BookingCoupon>() {
                            @Override
                            public void onSuccess(final BookingCoupon coupon) {
                                handlePromoSuccess(coupon, quote, bookingTransaction, promoCode);
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error) {
                                handlePromoFailure(error);
                            }
                    });
                }
            }
        }
    };

    private void completeBooking() {
        dataManager.createBooking(bookingManager.getCurrentTransaction(),
            new DataManager.Callback<BookingCompleteTransaction>() {
                @Override
                public void onSuccess(final BookingCompleteTransaction trans) {

                    //UPGRADE: Should we use this trans or ask the manager for current trans? So much inconsistency....

                    mixpanel.trackEventSubmitPayment(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());
                    mixpanel.trackEventBookingMade(bookingManager.getCurrentRequest(), bookingManager.getCurrentQuote(), bookingManager.getCurrentTransaction());

                    if (!allowCallbacks) return;

                    bookingManager.getCurrentTransaction().setBookingId(trans.getId());

                    boolean isNewUser = false;

                    if (userManager.getCurrentUser() == null) {
                        isNewUser = true;

                        final BookingCompleteTransaction.User transUser = trans.getUser();
                        final User user = new User();

                        user.setAuthToken(transUser.getAuthToken());
                        user.setId(transUser.getId());
                        userManager.setCurrentUser(user);
                    }

                    final User user = userManager.getCurrentUser();
                    dataManager.getUser(user.getId(), user.getAuthToken(),
                            new DataManager.Callback<User>() {
                        @Override
                        public void onSuccess(final User updatedUser) {
                            userManager.setCurrentUser(updatedUser);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {}
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
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;

                    enableInputs();
                    progressDialog.dismiss();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
        });
    }

    private final TextWatcher cardTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                                      final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                                  final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (!useExistingCard) creditCardIcon.setCardIcon(creditCardText.getCardType());
        }
    };

    private void handlePromoSuccess(final BookingCoupon coupon, final BookingQuote quote,
                                    final BookingTransaction transaction, final String promo) {
        if (!allowCallbacks) return;
        quote.setPriceTable(coupon.getPriceTable());
        transaction.setPromoApplied(promo);
        updatePromoUI();
        promoText.setText(null);
    }

    private void handlePromoFailure(final DataManager.DataManagerError error) {
        if (!allowCallbacks) return;
        updatePromoUI();
        promoText.setText(null);
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void updatePromoUI() {
        final BookingTransaction bookingTransaction = bookingManager.getCurrentTransaction();
        final String promo = bookingTransaction.promoApplied();
        final boolean applied = (promo != null);

        promoProgress.setVisibility(View.INVISIBLE);
        promoButton.setText(applied ? getString(R.string.remove) : getString(R.string.apply));
        promoText.setDisabled(applied, applied ? promo : getString(R.string.promo_code_opt));
    }
}
