package com.handybook.handybook.account.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.ui.fragment.UpdatePaymentFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountFragment extends InjectedFragment
{
    @Inject
    ConfigurationManager mConfigurationManager;
    @Inject
    UserManager mUserManager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.credits_text)
    TextView mCreditsText;
    @Bind(R.id.active_plans_text)
    TextView mActivePlansText;

    private User mUser;
    private ArrayList<RecurringBooking> mPlans;

    public static AccountFragment newInstance()
    {
        return new AccountFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mUser = userManager.getCurrentUser();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.account));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        return view;
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    )
    {
        super.onViewCreated(view, savedInstanceState);
        mCreditsText.setText(TextUtils.formatPrice(
                mUser.getCredits(),
                mUser.getCurrencyChar(),
                null
        ));
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showUiBlockers();
        dataManager.getRecurringBookings(new DataManager.Callback<RecurringBookingsResponse>()
        {
            @Override
            public void onSuccess(final RecurringBookingsResponse response)
            {
                removeUiBlockers();
                mPlans = new ArrayList<>(response.getRecurringBookings());
                mActivePlansText.setText(getString(
                        R.string.account_active_plans_formatted, mPlans.size()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                removeUiBlockers();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    @OnClick(R.id.contact_info_layout)
    public void contactClicked()
    {
        FragmentUtils.switchToFragment(this, new ContactFragment(), true);
    }

    @OnClick(R.id.password_layout)
    public void passwordClicked()
    {
        FragmentUtils.switchToFragment(this, ProfilePasswordFragment.newInstance(), true);
    }

    @OnClick(R.id.payment_method_layout)
    public void paymentClicked()
    {
        FragmentUtils.switchToFragment(this, UpdatePaymentFragment.newInstance(), true);
    }

    @OnClick(R.id.active_plans_layout)
    public void activePlansClicked()
    {
//        FragmentUtils.switchToFragment(this, PlansFragment.newInstance(mPlans), true);
    }

    @OnClick(R.id.promo_code_layout)
    public void promoClicked()
    {
        FragmentUtils.switchToFragment(this, PromosFragment.newInstance(), true);
    }

    @OnClick(R.id.sign_out_button)
    public void signOutClicked()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.want_to_log_out)
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mConfigurationManager.invalidateCache();
                        mUserManager.setCurrentUser(null);
                        //log out of Facebook also
                        LoginManager.getInstance().logOut();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //do nothing if it's canceled
                    }
                });

        alertDialog.show();
    }
}
