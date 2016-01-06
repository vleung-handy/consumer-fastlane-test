package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.widget.MenuButton;

import net.simonvt.menudrawer.MenuDrawer;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class PromosFragment extends BookingFlowFragment
        implements MenuDrawerActivity.OnDrawerStateChangeListener {

    public static final String EXTRA_PROMO_CODE = "EXTRA_PROMO_CODE";

    @Bind(R.id.menu_button_layout)
    ViewGroup menuButtonLayout;
    @Bind(R.id.apply_button)
    Button applyButton;
    @Bind(R.id.promo_text)
    EditText promoText;

    public static PromosFragment newInstance(String extraPromoCode) {
        PromosFragment fragment = new PromosFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PROMO_CODE, extraPromoCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PromosFragment newInstance() {
        return new PromosFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_promos, container, false);

        ButterKnife.bind(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), menuButtonLayout);
        menuButtonLayout.addView(menuButton);

        applyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final String promoCode = promoText.getText().toString();

                if (promoCode.trim().length() > 0)
                {
                    disableInputs();
                    progressDialog.show();

                    dataManager.getPreBookingPromo(promoCode, new DataManager.Callback<PromoCode>()
                    {
                        @Override
                        public void onSuccess(final PromoCode code)
                        {
                            if (!allowCallbacks) return;

                            progressDialog.dismiss();
                            enableInputs();

                            if (code.getType() == PromoCode.Type.VOUCHER)
                            {
                                startBookingFlow(code.getServiceId(), code.getUniq(), code);
                            }
                            else if (code.getType() == PromoCode.Type.COUPON)
                            {
                                bookingManager.setPromoTabCoupon(code.getCode());

                                final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
                                activity.setOnDrawerStateChangedListener(PromosFragment.this);

                                final MenuDrawer menuDrawer = activity.getMenuDrawer();
                                menuDrawer.openMenu(true);
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
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

    /**
     * handles the bundle arguments. currently arguments are only passed from deep links
     *
     * must be called after onCreateView() due to butterknife dependency
     */
    private void handleBundleArguments()
    {
        final Bundle args = getArguments();
        if (args != null)
        {
            String promoCode = args.getString(EXTRA_PROMO_CODE);
            if (promoCode != null)
            {
                args.remove(EXTRA_PROMO_CODE);
                promoText.setText(promoCode);
            }
        }
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleBundleArguments();
        promoText.requestFocus();
        InputMethodManager imm
                = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(promoText, InputMethodManager.SHOW_IMPLICIT);
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

    @Override
    public void onDrawerStateChange(final MenuDrawer menuDrawer, final int oldState,
                                    final int newState) {
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        if (newState == MenuDrawer.STATE_OPEN) {
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
    }
}
