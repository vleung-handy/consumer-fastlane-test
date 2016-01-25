package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.TextUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedemptionFragment extends InjectedFragment
{
    private static final String FACEBOOK_PERMISSION_EMAIL = "email";

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;

    private String mReferralGuid;
    private CallbackManager mFacebookCallbackManager;

    @OnClick(R.id.facebook_register_button)
    public void onFacebookRegisterButtonClicked()
    {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Lists.newArrayList(FACEBOOK_PERMISSION_EMAIL)
        );
    }

    @OnClick(R.id.email_register_button)
    public void onEmailRegisterButtonClicked()
    {
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked()
    {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

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
            startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
            getActivity().finish();
        }
        else
        {
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            mFacebookCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(
                    mFacebookCallbackManager,
                    mFacebookCallback
            );
        }
    }

    @Override
    public final void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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

        bus.post(new ReferralsEvent.RequestRedemptionDetails(mReferralGuid));

        return view;
    }

    @Subscribe
    public void onReceiveRequestRedemptionDetails(
            final ReferralsEvent.ReceiveRedemptionDetailsSuccess event
    )
    {
        final RedemptionDetails redemptionDetails = event.getRedemptionDetails();
        final String currencySymbol = redemptionDetails.getLocalizationData().getCurrencySymbol();
        final int receiverCouponAmount = redemptionDetails.getReceiverCouponAmount();
        final String senderFirstName = redemptionDetails.getSender().getFirstName();

        final String receiverCouponAmountFormatted =
                TextUtils.formatPrice(receiverCouponAmount, currencySymbol, null);
        mTitle.setText(getString(R.string.redemption_title_formatted, senderFirstName,
                receiverCouponAmountFormatted));
        mSubtitle.setText(getString(R.string.redemption_subtitle_formatted,
                receiverCouponAmountFormatted));
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

        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    private FacebookCallback<LoginResult> mFacebookCallback =
            new FacebookCallback<LoginResult>()
            {
                @Override
                public void onSuccess(final LoginResult loginResult)
                {
                    final AccessToken accessToken = loginResult.getAccessToken();
                    bus.post(new HandyEvent.RequestAuthFacebookUser(accessToken, mReferralGuid));
                }

                @Override
                public void onCancel()
                {

                }

                @Override
                public void onError(final FacebookException error)
                {

                }
            };
}
