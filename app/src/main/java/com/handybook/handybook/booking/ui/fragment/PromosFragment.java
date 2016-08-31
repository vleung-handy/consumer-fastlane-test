package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class PromosFragment extends BookingFlowFragment
{

    public static final String EXTRA_PROMO_CODE = "EXTRA_PROMO_CODE";

    @Bind(R.id.promotions_apply_button)
    Button mApplyButton;
    @Bind(R.id.promotions_coupon_text)
    EditText mPromoText;
    @Bind(R.id.promotions_coupon_text_clear)
    View mPromoTextClearImage;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String mPromoCoupon;

    public static PromosFragment newInstance(String extraPromoCode)
    {
        PromosFragment fragment = new PromosFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PROMO_CODE, extraPromoCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PromosFragment newInstance()
    {
        return new PromosFragment();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity()
                .getLayoutInflater().inflate(R.layout.fragment_promos, container, false);
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.promotions));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

        mPromoCoupon = bookingManager.getPromoTabCoupon();
        mPromoText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(
                    final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            )
            {
            }

            @Override
            public void onTextChanged(
                    final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            )
            {
            }

            @Override
            public void afterTextChanged(final Editable s)
            {
                mPromoTextClearImage.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        if (mPromoCoupon != null)
        {
            mPromoText.setText(mPromoCoupon);
        }
        return view;
    }


    /**
     * handles the bundle arguments. currently arguments are only passed from deep links
     * <p/>
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
                mPromoText.setText(promoCode);
            }
        }
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        handleBundleArguments();
        mPromoText.requestFocus();
        InputMethodManager imm
                = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mPromoText, InputMethodManager.SHOW_IMPLICIT);
    }

    @OnClick(R.id.promotions_apply_button)
    public void applyChanges()
    {
        final String promoCode = mPromoText.getText().toString();

        if (promoCode.trim().length() > 0)
        {
            disableInputs();
            progressDialog.show();

            dataManager.getPreBookingPromo(promoCode, new DataManager.Callback<PromoCode>()
            {
                @Override
                public void onSuccess(final PromoCode code)
                {
                    if (!allowCallbacks) { return; }
                    progressDialog.dismiss();
                    enableInputs();
                    if (code.getType() == PromoCode.Type.VOUCHER)
                    {
                        startBookingFlow(code.getServiceId(), code.getUniq(), code);
                    }
                    else if (code.getType() == PromoCode.Type.COUPON)
                    {
                        bookingManager.setPromoTabCoupon(code.getCode());
                        ((MenuDrawerActivity) getActivity()).navigateToActivity(
                                ServiceCategoriesActivity.class, R.id.nav_menu_home);
                    }
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    if (!allowCallbacks) { return; }
                    progressDialog.dismiss();
                    enableInputs();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
            });
        }
        else if (mPromoCoupon != null)
        {
            // The user wants to delete the promo code
            bookingManager.setPromoTabCoupon(null);
            showSnackbar();
        }
    }

    private void showSnackbar()
    {
        final Snackbar snackbar = Snackbar.make(
                getView(),
                R.string.snackbar_promo_code_deleted,
                Snackbar.LENGTH_LONG
        );
        final TextView snackText = (TextView) snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        snackText.setTextColor(Color.WHITE);
        snackbar.setAction(R.string.undo, new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                bookingManager.setPromoTabCoupon(mPromoCoupon);
                mPromoText.setText(mPromoCoupon);
                Snackbar undoSnackbar = Snackbar.make(
                        getView(),
                        R.string.snackbar_promo_code_restored,
                        Snackbar.LENGTH_SHORT);
                final TextView undoSnackText = (TextView) undoSnackbar.getView()
                        .findViewById(android.support.design.R.id.snackbar_text);
                undoSnackText.setTextColor(Color.WHITE);
                undoSnackbar.show();
            }
        });
        snackbar.setActionTextColor(
                getResources().getColor(R.color.handy_blue)
        );
        snackbar.show();
    }


    @OnClick(R.id.promotions_coupon_text_clear)
    public void clearPromoCode()
    {
        mPromoText.setText("");
        if (mPromoCoupon != null)
        { // If the user had a coupon and clears the field, apply changes
            applyChanges();
        }
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mApplyButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mApplyButton.setClickable(true);
    }
}
