package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class LoginActivityFragment extends InjectedFragment {
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.password_text) PasswordInputTextView passwordText;

    static LoginActivityFragment newInstance() {
        return new LoginActivityFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    //TODO submit to server
                }
            }
        });

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) emailText.highlight();
            if (savedInstanceState.getBoolean(STATE_PASSWORD_HIGHLIGHT)) passwordText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, passwordText.isHighlighted());
    }

    final boolean validateFields() {
        boolean validate = true;
        if (!emailText.validate()) validate = false;
        if (!passwordText.validate()) validate = false;
        return validate;
    }
}
