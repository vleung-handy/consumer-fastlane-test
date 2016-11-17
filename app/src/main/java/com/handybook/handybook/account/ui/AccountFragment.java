package com.handybook.handybook.account.ui;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.AccountLog;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

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
    @Inject
    UserDataManager mUserDataManager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.account_credits_text)
    TextView mCreditsText;
    @Bind(R.id.account_active_plans_text)
    TextView mActivePlansText;
    @Bind(R.id.account_active_plans_layout)
    ViewGroup mActivePlansLayout;

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
        /*
        display the credits based on mUser data
        this can be old, but we will request the most recent user data onResume
        and show an error if we are unable to get it
         */
        setCreditsText(mUser);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showUiBlockers();
        dataManager.getRecurringBookings(new FragmentSafeCallback<RecurringBookingsResponse>(
                this)
        {
            @Override
            public void onCallbackSuccess(final RecurringBookingsResponse response)
            {
                removeUiBlockers();
                mPlans = new ArrayList<>(response.getRecurringBookings());
                mActivePlansText.setText(getString(
                        R.string.account_active_plans_formatted, mPlans.size()));
                mActivePlansLayout.setEnabled(mPlans.size() != 0);
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error)
            {
                mActivePlansLayout.setEnabled(false);
                removeUiBlockers();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    private void setCreditsText(@NonNull User user)
    {
        mCreditsText.setText(TextUtils.formatPrice(
                user.getCredits(),
                user.getCurrencyChar(),
                null
        ));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Always log when this page is shown.
        // 1. When you come back from background
        // 2. When you come to fragment first time
        // 3. When you click away from page, but now you hit back and page is shown again
        bus.post(new LogEvent.AddLogEvent(new AccountLog.Shown()));

        /*
        hotfix to get updated user data
        looks like previous logic assumed mUser to be non-null here
        TODO non-blocking loading indicator
        TODO investigate when UserManager's current user is set and retrieved
         */
        mUserDataManager.requestAndSetCurrentUser(
                mUser.getId(),
                mUser.getAuthToken(),
                new FragmentSafeCallback<User>(AccountFragment.this)
                {
                    @Override
                    public void onCallbackSuccess(final User response)
                    {
                        mUser = response;
                        setCreditsText(mUser);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        //this will trigger an error toast
                        dataManagerErrorHandler.handleError(
                                AccountFragment.this.getContext(),
                                error
                        );
                        Crashlytics.logException(new Exception(error.getMessage()));
                    }
                }
        );
    }

    @OnClick(R.id.account_contact_info_layout)
    public void contactClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.EditProfileTapped()));
        FragmentUtils.switchToFragment(this, new ContactFragment(), true);
    }

    @OnClick(R.id.account_password_layout)
    public void passwordClicked()
    {

        bus.post(new LogEvent.AddLogEvent(new AccountLog.EditPasswordTapped()));
        FragmentUtils.switchToFragment(this, ProfilePasswordFragment.newInstance(), true);
    }

    @OnClick(R.id.account_payment_method_layout)
    public void paymentClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.EditPaymentTapped()));
        FragmentUtils.switchToFragment(this, UpdatePaymentFragment.newInstance(), true);
    }

    @OnClick(R.id.account_active_plans_layout)
    public void activePlansClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.PlanManagementTapped(mPlans.size())));
        if (mPlans.size() == 1)
        {
            FragmentUtils.switchToFragment(this, EditPlanFragment.newInstance(mPlans.get(0)), true);
        }
        else
        {
            FragmentUtils.switchToFragment(this, PlansFragment.newInstance(mPlans), true);
        }
    }

    @OnClick(R.id.account_promo_code_layout)
    public void promoClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.ApplyPromoTapped()));
        FragmentUtils.switchToFragment(this, PromosFragment.newInstance(), true);
    }

    @OnClick(R.id.account_sign_out_button)
    public void signOutClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.LogoutTapped()));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.want_to_log_out)
                .setPositiveButton(R.string.account_sign_out, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        bus.post(new LogEvent.AddLogEvent(new AccountLog.LogoutSuccess()));
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
                        bus.post(new LogEvent.AddLogEvent(new AccountLog.LogoutCancelled()));
                        //do nothing if it's canceled
                    }
                });

        alertDialog.show();
    }
}
