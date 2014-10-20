package com.handybook.handybook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ProfileFragment extends InjectedFragment {
    private static final String STATE_FULLNAME_HIGHLIGHT = "FULLNAME_HIGHLIGHT";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PHONE_HIGHLIGHT = "PHONE_HIGHLIGHT";
    private static final String STATE_LOADED_USER = "LOADED_USER";

    private User user;
    private ProgressDialog progressDialog;
    private boolean loadedUserInfo;

    @InjectView(R.id.credits_text) TextView creditsText;
    @InjectView(R.id.update_button) Button updateButton;
    @InjectView(R.id.fullname_text) FullNameInputTextView fullNameText;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.phone_prefix_text) TextView phonePrefixText;
    @InjectView(R.id.phone_text) PhoneInputTextView phoneText;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = userManager.getCurrentUser();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_FULLNAME_HIGHLIGHT)) fullNameText.highlight();
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) emailText.highlight();
            if (savedInstanceState.getBoolean(STATE_PHONE_HIGHLIGHT)) phoneText.highlight();
            loadedUserInfo = savedInstanceState.getBoolean(STATE_LOADED_USER);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateButton.setOnClickListener(updateClicked);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!loadedUserInfo) loadUserInfo();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_FULLNAME_HIGHLIGHT, fullNameText.isHighlighted());
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PHONE_HIGHLIGHT, phoneText.isHighlighted());
        outState.putBoolean(STATE_LOADED_USER, loadedUserInfo);
    }

    private void updateUserInfo() {
        final DecimalFormat df = new DecimalFormat("#.##");
        creditsText.setText(getString(R.string.you_have_credits)
                .replace("#", (user.getCurrencyChar() != null ? user.getCurrencyChar() : "")
                        + df.format(user.getCredits())
                        + (user.getCurrencySuffix() != null ? user.getCurrencySuffix() : "")));

        fullNameText.unHighlight();
        fullNameText.setText(user.getFirstName() + " " + user.getLastName());
        fullNameText.setSelection(fullNameText.getText().length());

        emailText.unHighlight();
        emailText.setText(user.getEmail());
        emailText.setSelection(emailText.getText().length());

        phonePrefixText.setText(user.getPhonePrefix());

        phoneText.unHighlight();
        phoneText.setText(user.getPhone());
        phoneText.setSelection(phoneText.getText().length());
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!fullNameText.validate()) validate = false;
        if (!emailText.validate()) validate = false;
        if (!phoneText.validate()) validate = false;
        return validate;
    }

    private void disableInputs() {
        fullNameText.setClickable(false);
        emailText.setClickable(false);
        phoneText.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullNameText.getWindowToken(), 0);
    }

    private void enableInputs() {
        fullNameText.setClickable(true);
        emailText.setClickable(true);
        phoneText.setClickable(true);
    }

    private void loadUserInfo() {
        updateUserInfo();
        disableInputs();
        progressDialog.show();

        dataManager.getUserInfo(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
            @Override
            public void onSuccess(final User user) {
                loadedUserInfo = true;
                progressDialog.dismiss();
                enableInputs();
                userManager.setCurrentUser(user);
                updateUserInfo();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                progressDialog.dismiss();
                enableInputs();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    private final View.OnClickListener updateClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();
                progressDialog.dismiss();
                enableInputs();
                //TODO call update api
                //TODO load user data on first login
            }
        }
    };
}
