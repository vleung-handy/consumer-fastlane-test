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
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.ValidationUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

public class RedemptionFragment extends InjectedFragment
{
    private static final String KEY_REFERRAL_GUID = "referral_guid";
    private String mReferralGuid;
    private User mUser;

    public static RedemptionFragment newInstance(final String referralGuid)
    {
        final RedemptionFragment fragment = new RedemptionFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_REFERRAL_GUID, referralGuid);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReferralGuid = getArguments().getString(KEY_REFERRAL_GUID);
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

        bus.post(new ReferralsEvent.RedemptionScreenShown());
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
        showErrorDialog(event.error.getMessage(), new DialogCallback()
        {
            @Override
            public void onRetry()
            {
                requestRedemptionDetails();
            }

            @Override
            public void onCancel()
            {
                navigateToHomeScreen();
            }
        });
    }

    @Subscribe
    public void onReceiveAuthUserSuccess(final HandyEvent.ReceiveAuthUserSuccess event)
    {
        mUser = event.getUser();
        requestUser();
    }

    private void requestUser()
    {
        showUiBlockers();
        bus.post(new HandyEvent.RequestUser(mUser.getId(), mUser.getAuthToken(), null));
    }

    @Subscribe
    public void onReceiveUserSuccess(final HandyEvent.ReceiveUserSuccess event)
    {
        navigateToHomeScreen();
    }

    @Subscribe
    public void onReceiveUserError(final HandyEvent.ReceiveUserError event)
    {
        showErrorDialog(event.error.getMessage(), new DialogCallback()
        {
            @Override
            public void onRetry()
            {
                requestUser();
            }

            @Override
            public void onCancel()
            {
            }
        });
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
