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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.history.HistoryFragment;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.UserDataManager;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.view.PriceView;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.AccountLog;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountFragment extends InjectedFragment
{
    @Inject
    UserManager mUserManager;
    @Inject
    UserDataManager mUserDataManager;

    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fragment_account_credits_view)
    PriceView mCreditsView;
    @Bind(R.id.account_active_plans_text)
    TextView mActivePlansText;
    @Bind(R.id.account_active_plans_layout)
    ViewGroup mActivePlansLayout;
    @Bind(R.id.account_history_help_layout)
    ViewGroup mHistoryHelpLayout;
    @Bind(R.id.horizontal_progress_bar)
    ProgressBar mHorizontalProgressBar;

    //This counter is used to remove the horizontal progress bar when counter is 0
    //Whenever show horizontal progress bar is called, this counter is incremented
    private int mHorizontalProgressRequestCounter;

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

        setupToolbar(mToolbar, getString(R.string.account));

        if (mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            mToolbar.setNavigationIcon(null);
            //if bottom nav is enabled, show
            mHistoryHelpLayout.setVisibility(View.VISIBLE);
        }
        else if (getActivity() instanceof MenuDrawerActivity)
        {
            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }
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
        updateCreditsView(mUser);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showHorizontalProgressBar();

        dataManager.getRecurringBookings(new FragmentSafeCallback<RecurringBookingsResponse>(
                this)
        {
            @Override
            public void onCallbackSuccess(final RecurringBookingsResponse response)
            {
                hideHorizontalProgressBarIfReady();
                mPlans = new ArrayList<>(response.getRecurringBookings());
                mActivePlansText.setText(getString(
                        R.string.account_active_plans_formatted, mPlans.size()));
                mActivePlansLayout.setEnabled(mPlans.size() != 0);
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error)
            {
                hideHorizontalProgressBarIfReady();
                mActivePlansLayout.setEnabled(false);
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    private void updateCreditsView(@NonNull User user)
    {
        mCreditsView.setCurrencySymbol(user.getCurrencyChar());
        mCreditsView.setPriceCents(user.getCreditsCents());
    }

    @Override
    public void onStop()
    {
        mHorizontalProgressRequestCounter = 0;
        hideHorizontalProgressBarIfReady();
        super.onStop();
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
        TODO investigate when UserManager's current user is set and retrieved
         */
        showHorizontalProgressBar();
        mUserDataManager.requestAndSetCurrentUser(
                mUser.getId(),
                mUser.getAuthToken(),
                new FragmentSafeCallback<User>(AccountFragment.this)
                {
                    @Override
                    public void onCallbackSuccess(final User response)
                    {
                        mUser = response;
                        updateCreditsView(mUser);
                        hideHorizontalProgressBarIfReady();
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
                        hideHorizontalProgressBarIfReady();
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

    @OnClick(R.id.account_help_layout)
    public void helpClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.HelpTapped()));

        InjectedFragment fragment = null;
        Bundle args = null;
        Configuration config = mConfigurationManager.getPersistentConfiguration();
        String helpCenterUrl = config.getHelpCenterUrl();
        if (config.isNativeHelpCenterEnabled())
        {
            fragment = HelpFragment.newInstance(helpCenterUrl);
        }
        else if (!Strings.isNullOrEmpty(helpCenterUrl))
        {
            args = new Bundle();
            args.putString(BundleKeys.HELP_CENTER_URL, helpCenterUrl);
        }

        //If fragment is not set, then default to HelpWebViewFragment
        if (fragment == null)
        {
            //args can be set or null
            fragment = HelpWebViewFragment.newInstance(args);
        }

        FragmentUtils.switchToFragment(this, fragment, true);
    }

    @OnClick(R.id.account_booking_history_layout)
    public void bookingHistoryClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.BookingHistoryTapped()));
        FragmentUtils.switchToFragment(this, new HistoryFragment(), true);
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

                        //remove user email and zip when they logout. Do this before setting the user
                        //to null, as that action will trigger more downstream action that is dependent
                        //on these shared prefs being removed.
                        mDefaultPreferencesManager.removeValue(PrefsKey.ZIP);
                        mDefaultPreferencesManager.removeValue(PrefsKey.EMAIL);

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

    private void showHorizontalProgressBar() {
        mHorizontalProgressRequestCounter++;
        mHorizontalProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * This method will hide the horizontal progress bar if api call backs are completed.
     * If not, it will decrement the counter
     */
    private void hideHorizontalProgressBarIfReady() {
        //only decrement if greater then 0
        if(mHorizontalProgressRequestCounter > 0)
            --mHorizontalProgressRequestCounter;

        if(mHorizontalProgressRequestCounter == 0) {
           mHorizontalProgressBar.setVisibility(View.GONE);
        }
    }
}
