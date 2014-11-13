package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stripe.android.model.Card;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingPaymentFragment extends InjectedFragment {
    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;

    @InjectView(R.id.credit_card_text) CreditCardInputTextView creditCardText;
    @InjectView(R.id.card_icon) ImageView creditCardIcon;

    static BookingPaymentFragment newInstance() {
        final BookingPaymentFragment fragment = new BookingPaymentFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_payment,container, false);

        ButterKnife.inject(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        User.CreditCard card;

        if (user != null && (card = user.getCreditCard()) != null && card.getLast4() != null) {
            creditCardText.setHint("\u2022\u2022\u2022\u2022 " + card.getLast4());
            creditCardText.setInputType(InputType.TYPE_NULL);
            creditCardText.setEnabled(false);
            setCardIcon(card.getBrand());
        }
        else {
            //TODO handle no user so show card input fields or if user doesnt have card
        }

        //TODO implement change button

        return view;
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
}
