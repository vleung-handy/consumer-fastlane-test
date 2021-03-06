package com.handybook.handybook.account.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.event.StripeEvent;
import com.handybook.handybook.core.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.core.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.core.ui.widget.CreditCardIconImageView;
import com.handybook.handybook.core.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.AccountLog;
import com.stripe.android.model.Card;
import com.stripe.exception.CardException;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;

public class UpdatePaymentFragment extends ProgressSpinnerFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.credit_card_text)
    CreditCardNumberInputTextView mCreditCardText;
    @BindView(R.id.exp_text)
    CreditCardExpDateInputTextView mExpText;
    @BindView(R.id.cvc_text)
    CreditCardCVCInputTextView mCvcText;
    @BindView(R.id.card_icon)
    CreditCardIconImageView mCreditCardIcon;
    @BindView(R.id.update_button)
    Button mUpdateButton;
    @BindView(R.id.change_button)
    Button mChangeButton;
    @BindView(R.id.card_extras_layout)
    ViewGroup mCardExtrasLayout;
    @BindView(R.id.cancel_button)
    View mCancelButton;
    @BindView(R.id.lock_icon)
    ImageView mLockIcon;
    @BindView(R.id.scan_card_button)
    TextView mScanCardButton;

    //TODO: this fragment duplicates a lots of logic in BookingPaymentFragment; we should consolidate
    //as a consequence of not doing the above, we have lots of repeated code between
    //this fragment and BookingPaymentFragment

    /*
    NOTE: duplicating the credit card scanning code from BookingPaymentFragment for now.
    we are not going to put it in a shared class because:
     1) we are assuming we will consolidate this fragment with BookingPaymentFragment soon
     2) we require the activity lifecycle callback onActivityResult() which we cannot register a listener to
     3) cannot extend the same class as BookingPaymentFragment because that needs to be a BookingFlowFragment
     4) because of the above, we can't cleanly do it, so will leave it like this for clarity
     */

    @OnClick(R.id.scan_card_button)
    public void onScanCardButtonPressed() {
        startCardScanActivity();
    }

    private void startCardScanActivity() {
        Intent scanIntent = new Intent(getContext(), CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        startActivityForResult(scanIntent, ActivityResult.SCAN_CREDIT_CARD);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResult.SCAN_CREDIT_CARD) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                io.card.payment.CreditCard scannedCardResult = data.getParcelableExtra(
                        CardIOActivity.EXTRA_SCAN_RESULT);
                onScannedCardResult(scannedCardResult);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new LogEvent.AddLogEvent(new AccountLog.PaymentMethodShown()));

    }

    public static UpdatePaymentFragment newInstance() {
        return new UpdatePaymentFragment();
    }

    @OnClick(R.id.change_button)
    public void unfreezeCardInput() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.edit_payment);
        mCreditCardText.setDisabled(false, getString(R.string.credit_card_num));
        mCardExtrasLayout.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.VISIBLE);
        mChangeButton.setVisibility(View.GONE);
        mCreditCardIcon.setCardIcon(CreditCard.Type.OTHER);
        mCancelButton.setVisibility(View.VISIBLE);
        mScanCardButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_button)
    public void freezeCardInput() {
        final User currentUser = userManager.getCurrentUser();
        final User.CreditCard creditCard = currentUser.getCreditCard();
        freezeCardInput(creditCard.getBrand(), creditCard.getLast4());
    }

    private void freezeCardInput(String brand, String last4) {
        mCreditCardText.setDisabled(true, getString(R.string.formatted_last4, last4));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.payment);
        mCreditCardText.setText(R.string.blank_string);
        mExpText.setText(R.string.blank_string);
        mCvcText.setText(R.string.blank_string);
        mCreditCardText.unHighlight();
        mExpText.unHighlight();
        mCvcText.unHighlight();
        mCardExtrasLayout.setVisibility(View.GONE);
        mUpdateButton.setVisibility(View.GONE);
        mChangeButton.setVisibility(View.VISIBLE);
        mCreditCardIcon.setCardIcon(brand);
        mCancelButton.setVisibility(View.GONE);
        mScanCardButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.update_button)
    public void updatePayment() {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.PaymentMethodUpdateTapped()));

        if (areFieldsValid()) {
            disableInputs();
            showProgressSpinner(true);
            final Card card = new Card(mCreditCardText.getCardNumber(), mExpText.getExpMonth(),
                                       mExpText.getExpYear(), mCvcText.getCVC()
            );
            final String stripeKey = userManager.getCurrentUser().getStripeKey();
            bus.post(new StripeEvent.RequestCreateToken(card, stripeKey));
        }
        else {
            showToast(R.string.error_payment_info_invalid, Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onReceiveCreateTokenSuccess(StripeEvent.ReceiveCreateTokenSuccess event) {
        bus.post(new HandyEvent.RequestUpdatePayment(event.getToken()));
    }

    @Subscribe
    public void onReceiveCreateTokenError(StripeEvent.ReceiveCreateTokenError event) {
        enableInputs();
        hideProgressSpinner();
        if (event.getError() instanceof CardException) {
            showToast(event.getError().getMessage());
        }
        else {
            showToast(R.string.default_error_string);
        }
    }

    @Subscribe
    public void onReceiveUpdatePaymentSuccess(HandyEvent.ReceiveUpdatePaymentSuccess event) {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.PaymentMethodUpdateSuccess()));

        hideProgressSpinner();
        ((BaseApplication) getActivity().getApplication()).updateUser();
        final Card card = new Card(mCreditCardText.getCardNumber(), mExpText.getExpMonth(),
                                   mExpText.getExpYear(), mCvcText.getCVC()
        );
        freezeCardInput(card.getType(), card.getLast4());
        Utils.hideSoftKeyboard(getActivity(), getView());
        showToast(R.string.update_successful);
    }

    @Subscribe
    public void onReceiveUpdatePaymentError(HandyEvent.ReceiveUpdatePaymentError event) {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.PaymentMethodUpdateError()));

        hideProgressSpinner();
        showToast(R.string.default_error_string);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_update_payment, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.payment));

        final User currentUser = userManager.getCurrentUser();
        final User.CreditCard creditCard = currentUser.getCreditCard();
        if (creditCard != null && creditCard.getLast4() != null &&
            !creditCard.getLast4().isEmpty()) {
            freezeCardInput(creditCard.getBrand(), creditCard.getLast4());
        }
        else {
            unfreezeCardInput();
            mCancelButton.setVisibility(View.GONE);
        }

        mLockIcon.setColorFilter(
                ContextCompat.getColor(getContext(), R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP
        );

        mCreditCardText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    final CharSequence charSequence, final int start,
                    final int count, final int after
            ) {
            }

            @Override
            public void onTextChanged(
                    final CharSequence charSequence, final int start,
                    final int before, final int count
            ) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                mCreditCardIcon.setCardIcon(mCreditCardText.getCardType());
            }
        });

        return view;
    }

    private boolean areFieldsValid() {
        return mCreditCardText.validate() && mExpText.validate() && mCvcText.validate();
    }

    public void onScannedCardResult(@NonNull final io.card.payment.CreditCard scannedCardResult) {
        mCreditCardText.setText(scannedCardResult.cardNumber);
        if (scannedCardResult.isExpiryValid()) {
            mExpText.setTextFromMonthYear(
                    scannedCardResult.expiryMonth,
                    scannedCardResult.expiryYear
            );
        }
        mCvcText.setText(scannedCardResult.cvv);
    }
}
