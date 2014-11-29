package com.handybook.handybook;

import android.content.Context;
import android.content.Intent;
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

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.CardException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingPaymentFragment extends BookingFlowFragment {
    private static final String STATE_CARD_NUMBER_HIGHLIGHT = "CARD_NUMBER_HIGHLIGHT";
    private static final String STATE_CARD_EXP_HIGHLIGHT = "CARD_EXP_HIGHLIGHT";
    private static final String STATE_CARD_CVC_HIGHLIGHT = "CARD_CVC_HIGHLIGHT";
    private static final String STATE_USE_EXISTING_CARD = "USE_EXISTING_CARD";

    private boolean useExistingCard;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.change_button) Button changeButton;
    @InjectView(R.id.credit_card_text) CreditCardNumberInputTextView creditCardText;
    @InjectView(R.id.exp_text) CreditCardExpDateInputTextView expText;
    @InjectView(R.id.cvc_text) CreditCardCVCInputTextView cvcText;
    @InjectView(R.id.card_icon) ImageView creditCardIcon;
    @InjectView(R.id.card_extras_layout) LinearLayout cardExtrasLayout;

    static BookingPaymentFragment newInstance() {
        final BookingPaymentFragment fragment = new BookingPaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            useExistingCard = savedInstanceState.getBoolean(STATE_USE_EXISTING_CARD);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        mixpanel.trackEventPaymentPage();
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment,container, false);

        ButterKnife.inject(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        final User.CreditCard card = user != null ? user.getCreditCard() : null;

        if ((card != null && card.getLast4() != null)
                && (savedInstanceState == null || useExistingCard)) {
            useExistingCard = true;
            creditCardText.setDisabled(true, "\u2022\u2022\u2022\u2022 " + card.getLast4());
            setCardIcon(card.getBrand());
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

    private void setCardIcon(final String type) {
        if (type == null) {
            creditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type) {
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

    private void setCardIcon(final CreditCard.Type type) {
        if (type == null) {
            creditCardIcon.setImageResource(R.drawable.ic_card_blank);
            return;
        }

        switch (type) {
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

            default:
                creditCardIcon.setImageResource(R.drawable.ic_card_blank);
        }
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
        setCardIcon(CreditCard.Type.OTHER);
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

                    final Stripe stripe = new Stripe();
                    stripe.createToken(card, bookingManager.getCurrentQuote().getStripeKey(),
                            new TokenCallback() {
                        @Override
                        public void onError(final Exception e) {
                            if (!allowCallbacks) return;

                            enableInputs();
                            progressDialog.dismiss();

                            if (e instanceof CardException) toast.setText(e.getMessage());
                            else toast.setText(getString(R.string.default_error_string));
                            toast.show();
                        }
                        @Override
                        public void onSuccess(final Token token) {
                            if (!allowCallbacks) return;
                            bookingManager.getCurrentTransaction().setStripeToken(token.getId());
                            completeBooking();
                        }
                    });
                } else completeBooking();
            }
        }
    };

    private void completeBooking() {
        dataManager.completeBooking(bookingManager.getCurrentTransaction(),
            new DataManager.Callback<BookingCompleteTransaction>() {
                @Override
                public void onSuccess(final BookingCompleteTransaction trans) {
                    mixpanel.trackEventSubmitPayment();
                    mixpanel.trackEventBookingMade();
                    if (!allowCallbacks) return;

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
            if (!useExistingCard) setCardIcon(creditCardText.getCardType());
        }
    };
}
