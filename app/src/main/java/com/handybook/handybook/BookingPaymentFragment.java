package com.handybook.handybook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.stripe.android.model.Card;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingPaymentFragment extends InjectedFragment {
    private boolean useExisitnigCard;
    private ProgressDialog progressDialog;

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    @InjectView(R.id.next_button) Button nextButton;
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final User user = userManager.getCurrentUser();
        User.CreditCard card;

        if (user != null && (card = user.getCreditCard()) != null && card.getLast4() != null) {
            useExisitnigCard = true;
            creditCardText.setHint("\u2022\u2022\u2022\u2022 " + card.getLast4());
            creditCardText.setInputType(InputType.TYPE_NULL);
            creditCardText.setEnabled(false);
            setCardIcon(card.getBrand());
        }
        else {
            //TODO handle no user so show card input fields or if user doesnt have card
        }

        //TODO implement change button

        nextButton.setOnClickListener(nextClicked);

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

    private void disableInputs() {
        nextButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(creditCardText.getWindowToken(), 0);
    }

    private void enableInputs() {
        nextButton.setClickable(true);
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!useExisitnigCard && !creditCardText.validate()) validate = false;
        return validate;
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                dataManager.completeBooking(bookingManager.getCurrentTransaction(),
                    new DataManager.Callback<String>() {
                        @Override
                        public void onSuccess(final String resp) {
                            if (!allowCallbacks) return;
                            Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
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
        }
    };
}
