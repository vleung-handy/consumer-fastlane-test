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
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.LeftIconButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedemptionSignUpFragment extends InjectedFragment
{
    private static final String FACEBOOK_PERMISSION_EMAIL = "email";
    private static final String KEY_RECEIVER_COUPON_AMOUNT = "receiver_coupon_amount";
    private static final String KEY_SENDER_NAME = "sender_name";
    private static final String KEY_REFERRAL_GUID = "referral_guid";

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;
    @Bind(R.id.facebook_register_button)
    LeftIconButton mFacebookRegisterButton;
    @Bind(R.id.email_register_button)
    LeftIconButton mEmailRegisterButton;

    private String mReferralGuid;
    private CallbackManager mFacebookCallbackManager;
    private String mSenderName;
    private String mReceiverCouponAmount;

    public static RedemptionSignUpFragment newInstance(
            final String referralGuid,
            final String senderName,
            final String receiverCouponAmount
    )
    {
        final RedemptionSignUpFragment fragment = new RedemptionSignUpFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_SENDER_NAME, senderName);
        arguments.putString(KEY_RECEIVER_COUPON_AMOUNT, receiverCouponAmount);
        arguments.putString(KEY_REFERRAL_GUID, referralGuid);
        fragment.setArguments(arguments);
        return fragment;
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked()
    {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

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
        final RedemptionEmailSignUpFragment fragment =
                RedemptionEmailSignUpFragment.newInstance(mReferralGuid);
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.child_fragment_container, fragment)
                .commit();
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

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSenderName = getArguments().getString(KEY_SENDER_NAME);
        mReceiverCouponAmount = getArguments().getString(KEY_RECEIVER_COUPON_AMOUNT);
        mReferralGuid = getArguments().getString(KEY_REFERRAL_GUID);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                mFacebookCallbackManager,
                mFacebookCallback
        );

    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_redemption_sign_up, container, false);
        ButterKnife.bind(this, view);

        mTitle.setText(getString(R.string.redemption_title_formatted, mSenderName,
                mReceiverCouponAmount));
        mSubtitle.setText(getString(R.string.redemption_subtitle_formatted, mReceiverCouponAmount));

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // The following init calls are here due to an issue where both views are displaying the
        // same text after going back to this fragment. This will trigger re-initialization of
        // the views before the fragment goes visible.
        mFacebookRegisterButton.init(R.string.sign_up_with_facebook, R.drawable.ic_facebook_white,
                R.drawable.facebook_pressed_round_left);
        mEmailRegisterButton.init(R.string.sign_up_with_email, R.drawable.ic_email_white,
                R.drawable.handy_blue_pressed_round_left);
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
