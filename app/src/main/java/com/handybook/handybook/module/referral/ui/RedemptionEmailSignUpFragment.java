package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.handybook.handybook.util.UiUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedemptionEmailSignUpFragment extends InjectedFragment
{
    private static final String KEY_REFERRAL_GUID = "referral_guid";

    @Bind(R.id.email)
    EmailInputTextView mEmailInput;
    @Bind(R.id.password)
    PasswordInputTextView mPasswordInput;

    private String mReferralGuid;

    public static RedemptionEmailSignUpFragment newInstance(final String referralGuid)
    {
        final RedemptionEmailSignUpFragment fragment = new RedemptionEmailSignUpFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_REFERRAL_GUID, referralGuid);
        fragment.setArguments(arguments);
        return fragment;
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked()
    {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked()
    {
        UiUtils.dismissKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpButtonClicked()
    {
        if (mEmailInput.validate() && mPasswordInput.validate())
        {
            UiUtils.dismissKeyboard(getActivity());
            showUiBlockers();

            final String email = mEmailInput.getEmail();
            final String password = mPasswordInput.getPassword();
            bus.post(new HandyEvent.RequestCreateUser(email, password, mReferralGuid));
        }
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
        final View view =
                inflater.inflate(R.layout.fragment_redemption_email_sign_up, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UiUtils.toggleKeyboard(getActivity());
        mEmailInput.requestFocus();
    }

    @Subscribe
    public void onReceiveAuthUserError(final HandyEvent.ReceiveAuthUserError event)
    {
        removeUiBlockers();
    }
}
