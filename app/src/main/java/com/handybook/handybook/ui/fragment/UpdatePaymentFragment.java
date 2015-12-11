package com.handybook.handybook.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.StripeEvent;
import com.handybook.handybook.ui.widget.CreditCardCVCInputTextView;
import com.handybook.handybook.ui.widget.CreditCardExpDateInputTextView;
import com.handybook.handybook.ui.widget.CreditCardIconImageView;
import com.handybook.handybook.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.ui.widget.MenuButton;
import com.squareup.otto.Subscribe;
import com.stripe.android.model.Card;
import com.stripe.exception.CardException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePaymentFragment extends InjectedFragment
{
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.credit_card_text)
    CreditCardNumberInputTextView mCreditCardText;
    @Bind(R.id.exp_text)
    CreditCardExpDateInputTextView mExpText;
    @Bind(R.id.cvc_text)
    CreditCardCVCInputTextView mCvcText;
    @Bind(R.id.card_icon)
    CreditCardIconImageView mCreditCardIcon;
    @Bind(R.id.update_button)
    Button mUpdateButton;
    @Bind(R.id.change_button)
    Button mChangeButton;
    @Bind(R.id.card_extras_layout)
    ViewGroup mCardExtrasLayout;
    @Bind(R.id.cancel_button)
    View mCancelButton;
    @Bind(R.id.nav_text)
    TextView mNavText;
    @Bind(R.id.lock_icon)
    ImageView mLockIcon;

    public static UpdatePaymentFragment newInstance()
    {
        return new UpdatePaymentFragment();
    }

    @OnClick(R.id.change_button)
    public void unfreezeCardInput()
    {
        mNavText.setText(R.string.edit_payment);
        mCreditCardText.setDisabled(false, getString(R.string.credit_card_num));
        mCardExtrasLayout.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.VISIBLE);
        mChangeButton.setVisibility(View.GONE);
        mCreditCardIcon.setCardIcon(CreditCard.Type.OTHER);
        mCancelButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_button)
    public void freezeCardInput()
    {
        final User currentUser = userManager.getCurrentUser();
        final User.CreditCard creditCard = currentUser.getCreditCard();
        freezeCardInput(creditCard.getBrand(), creditCard.getLast4());
    }

    private void freezeCardInput(String brand, String last4)
    {
        mCreditCardText.setDisabled(true, getString(R.string.formatted_last4, last4));
        mNavText.setText(R.string.payment);
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
    }

    @OnClick(R.id.update_button)
    public void updatePayment()
    {
        if (areFieldsValid())
        {
            disableInputs();
            progressDialog.show();
            final Card card = new Card(mCreditCardText.getCardNumber(), mExpText.getExpMonth(),
                    mExpText.getExpYear(), mCvcText.getCVC());
            final String stripeKey = userManager.getCurrentUser().getStripeKey();
            bus.post(new StripeEvent.RequestCreateToken(card, stripeKey));
        }
        else
        {
            showToast(R.string.error_payment_info_invalid, Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onReceiveCreateTokenSuccess(StripeEvent.ReceiveCreateTokenSuccess event)
    {
        bus.post(new HandyEvent.RequestUpdatePayment(event.getToken()));
    }

    @Subscribe
    public void onReceiveCreateTokenError(StripeEvent.ReceiveCreateTokenError event)
    {
        enableInputs();
        progressDialog.dismiss();
        if (event.getError() instanceof CardException)
        {
            showToast(event.getError().getMessage());
        }
        else
        {
            showToast(R.string.default_error_string);
        }
    }

    @Subscribe
    public void onReceiveUpdatePaymentSuccess(HandyEvent.ReceiveUpdatePaymentSuccess event)
    {
        progressDialog.dismiss();
        ((BaseApplication) getActivity().getApplication()).updateUser();
        final Card card = new Card(mCreditCardText.getCardNumber(), mExpText.getExpMonth(),
                mExpText.getExpYear(), mCvcText.getCVC());
        freezeCardInput(card.getType(), card.getLast4());
        showToast(R.string.update_successful);
    }

    @Subscribe
    public void onReceiveUpdatePaymentError(HandyEvent.ReceiveUpdatePaymentError event)
    {
        progressDialog.dismiss();
        showToast(R.string.default_error_string);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_update_payment, container, false);
        ButterKnife.bind(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        menuButton.setColor(getResources().getColor(R.color.white));
        mMenuButtonLayout.addView(menuButton);

        final User currentUser = userManager.getCurrentUser();
        final User.CreditCard creditCard = currentUser.getCreditCard();
        if (creditCard != null && creditCard.getLast4() != null && !creditCard.getLast4().isEmpty())
        {
            freezeCardInput(creditCard.getBrand(), creditCard.getLast4());
        }
        else
        {
            unfreezeCardInput();
            mCancelButton.setVisibility(View.GONE);
        }

        mLockIcon.setColorFilter(getResources().getColor(R.color.black_pressed),
                PorterDuff.Mode.SRC_ATOP);

        mCreditCardText.addTextChangedListener(new TextWatcher()
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
                mCreditCardIcon.setCardIcon(mCreditCardText.getCardType());
            }
        });

        return view;
    }

    private boolean areFieldsValid()
    {
        return mCreditCardText.validate() && mExpText.validate() && mCvcText.validate();
    }
}
