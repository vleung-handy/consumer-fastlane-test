package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PromosFragment extends BookingFlowFragment {

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;
    @InjectView(R.id.apply_button) Button applyButton;
    @InjectView(R.id.promo_text) EditText promoText;

    static PromosFragment newInstance() {
        return new PromosFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_promos,container, false);

        ButterKnife.inject(this, view);

        final MenuButton menuButton = new MenuButton(getActivity());
        menuButton.setColor(getResources().getColor(R.color.black_pressed));
        Utils.extendHitArea(menuButton, menuButtonLayout, Utils.toDP(32, getActivity()));
        menuButtonLayout.addView(menuButton);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String promoCode = promoText.getText().toString();

                if (promoCode.trim().length() > 0) {
                    disableInputs();
                    progressDialog.show();

                    dataManager.getPreBookingPromo(promoCode, new DataManager.Callback<PromoCode>() {
                        @Override
                        public void onSuccess(final PromoCode code) {
                            if (!allowCallbacks) return;

                            progressDialog.dismiss();
                            enableInputs();

                            if (code.getType() == PromoCode.Type.VOUCHER) {
                                startBookingFlow(code.getServiceId(), code.getUniq(), code.getCode());
                            }
                            else {
                                toast.setText(getString(R.string.coupon_on_payment_screen));
                                toast.show();
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            if (!allowCallbacks) return;
                            progressDialog.dismiss();
                            enableInputs();
                            dataManagerErrorHandler.handleError(getActivity(), error);
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        applyButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        applyButton.setClickable(true);
    }
}
