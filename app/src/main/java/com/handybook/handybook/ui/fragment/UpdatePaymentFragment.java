package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.core.CreditCard;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.CreditCardIconImageView;
import com.handybook.handybook.ui.widget.CreditCardNumberInputTextView;
import com.handybook.handybook.ui.widget.MenuButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePaymentFragment extends InjectedFragment
{
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.credit_card_text)
    CreditCardNumberInputTextView mCreditCardText;
    @Bind(R.id.card_icon)
    CreditCardIconImageView mCreditCardIcon;
    @Bind(R.id.update_button)
    Button mUpdateButton;
    @Bind(R.id.change_button)
    Button mChangeButton;
    @Bind(R.id.card_extras_layout)
    ViewGroup mCardExtrasLayout;

    public static Fragment newInstance()
    {
        return new UpdatePaymentFragment();
    }

    @OnClick(R.id.change_button)
    public void allowCardInput()
    {
        mCreditCardText.setDisabled(false, getString(R.string.credit_card_num));
        mCardExtrasLayout.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.VISIBLE);
        mChangeButton.setVisibility(View.GONE);
        mCreditCardIcon.setCardIcon(CreditCard.Type.OTHER);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_update_payment, container, false);
        ButterKnife.bind(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        mMenuButtonLayout.addView(menuButton);

        final User currentUser = userManager.getCurrentUser();
        final User.CreditCard creditCard = currentUser.getCreditCard();
        if (creditCard != null && creditCard.getLast4() != null && !creditCard.getLast4().isEmpty())
        {
            mCreditCardText.setDisabled(true, "\u2022\u2022\u2022\u2022 " + creditCard.getLast4());
            mCreditCardIcon.setCardIcon(creditCard.getBrand());
        }
        else
        {
            allowCardInput();
        }

        mCreditCardText.addTextChangedListener(cardTextWatcher);

        return view;
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
            mCreditCardIcon.setCardIcon(mCreditCardText.getCardType());
        }
    };
}
