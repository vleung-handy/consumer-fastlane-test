package com.handybook.handybook.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.widget.EmailInputTextView;
import com.handybook.handybook.core.ui.widget.PasswordInputTextView;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.UiUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedemptionEmailSignUpFragment extends ProgressSpinnerFragment {

    private static final String KEY_REFERRAL_GUID = "referral_guid";

    @BindView(R.id.email)
    EmailInputTextView mEmailInput;
    @BindView(R.id.password)
    PasswordInputTextView mPasswordInput;

    private String mReferralGuid;

    public static RedemptionEmailSignUpFragment newInstance(final String referralGuid) {
        final RedemptionEmailSignUpFragment fragment = new RedemptionEmailSignUpFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_REFERRAL_GUID, referralGuid);
        fragment.setArguments(arguments);
        return fragment;
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        UiUtils.dismissKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpButtonClicked() {
        if (mEmailInput.validate() && mPasswordInput.validate()) {
            UiUtils.dismissKeyboard(getActivity());
            showProgressSpinner(true);

            final String email = mEmailInput.getEmail();
            final String password = mPasswordInput.getPassword();
            bus.post(new HandyEvent.RequestCreateUser(email, password, mReferralGuid));
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReferralGuid = getArguments().getString(KEY_REFERRAL_GUID);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(
                R.layout.fragment_redemption_email_sign_up,
                container,
                false
        ));

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UiUtils.toggleKeyboard(getActivity());
        mEmailInput.requestFocus();
    }

    @Subscribe
    public void onReceiveAuthUserSuccess(final HandyEvent.ReceiveAuthUserSuccess event) {
        // RedemptionFragment handles post authentication procedures
        hideProgressSpinner();
    }

    @Subscribe
    public void onReceiveAuthUserError(final HandyEvent.ReceiveAuthUserError event) {
        hideProgressSpinner();
    }
}
