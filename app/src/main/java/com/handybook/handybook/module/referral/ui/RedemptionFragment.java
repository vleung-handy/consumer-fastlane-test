package com.handybook.handybook.module.referral.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.ValidationUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

public class RedemptionFragment extends InjectedFragment
{
    private String mReferralGuid;

    public static RedemptionFragment newInstance()
    {
        return new RedemptionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReferralGuid = ReferralIntentUtil.getReferralGuidFromIntent(getActivity().getIntent());
        if (mReferralGuid == null || userManager.isUserLoggedIn()) // new users only
        {
            navigateToHomeScreen();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_redemption, container, false);
        ButterKnife.bind(this, view);

        requestRedemptionDetails();

        return view;
    }

    private void requestRedemptionDetails()
    {
        showUiBlockers();
        bus.post(new ReferralsEvent.RequestRedemptionDetails(mReferralGuid));
    }

    @Subscribe
    public void onReceiveRedemptionDetailsSuccess(
            final ReferralsEvent.ReceiveRedemptionDetailsSuccess event
    )
    {
        removeUiBlockers();
        final RedemptionDetails redemptionDetails = event.getRedemptionDetails();
        final String currencySymbol = redemptionDetails.getLocalizationData().getCurrencySymbol();
        final int receiverCouponAmount = redemptionDetails.getReceiverCouponAmount();
        final String senderFirstName = redemptionDetails.getSender().getFirstName();
        final String receiverCouponAmountFormatted =
                TextUtils.formatPrice(receiverCouponAmount, currencySymbol, null);

        final RedemptionSignUpFragment redemptionSignUpFragment =
                RedemptionSignUpFragment.newInstance(
                        mReferralGuid, senderFirstName, receiverCouponAmountFormatted);

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.child_fragment_container, redemptionSignUpFragment)
                .commit();
    }

    @Subscribe
    public void onReceiveRedemptionDetailsError(
            final ReferralsEvent.ReceiveRedemptionDetailsError event
    )
    {
        removeUiBlockers();
        String displayMessage = event.error.getMessage();
        if (ValidationUtils.isNullOrEmpty(displayMessage))
        {
            displayMessage = getString(R.string.an_error_has_occurred);
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(displayMessage)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        dialog.dismiss();
                        navigateToHomeScreen();
                    }
                })
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        dialog.dismiss();
                        requestRedemptionDetails();
                    }
                })
                .create()
                .show();
    }

    @Subscribe
    public void onReceiveAuthUserSuccess(final HandyEvent.ReceiveAuthUserSuccess event)
    {
        dataManager.getUser(
                event.getUser().getId(),
                event.getUser().getAuthToken(),
                new DataManager.Callback<User>()
                {
                    @Override
                    public void onSuccess(final User user)
                    {
                        userManager.setCurrentUser(user);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                    }
                });
        navigateToHomeScreen();
    }

    @Subscribe
    public void onReceiveAuthUserError(final HandyEvent.ReceiveAuthUserError event)
    {
        removeUiBlockers();
        String displayMessage = event.error.getMessage();
        if (ValidationUtils.isNullOrEmpty(displayMessage))
        {
            displayMessage = getString(R.string.an_error_has_occurred);
        }
        showToast(displayMessage);
    }

    private void navigateToHomeScreen()
    {
        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }
}
