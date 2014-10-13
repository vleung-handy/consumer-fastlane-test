package com.handybook.handybook;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class LoginActivityFragment extends InjectedFragment {
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    private ProgressDialog progressDialog;
    private Toast forgotToast;

    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.forgot_button) Button forgotButton;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.password_text) PasswordInputTextView passwordText;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;
    @Inject UserManager userManager;

    static LoginActivityFragment newInstance() {
        return new LoginActivityFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        forgotToast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        forgotToast.setGravity(Gravity.CENTER, 0, 0);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginButton.setOnClickListener(loginClicked);
        forgotButton.setOnClickListener(forgotClicked);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, passwordText.isHighlighted());
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!emailText.validate()) validate = false;
        if (!passwordText.validate()) validate = false;
        return validate;
    }

    private void disableInputs() {
        loginButton.setClickable(false);
        forgotButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
    }

    private void enableInputs() {
        loginButton.setClickable(true);
        forgotButton.setClickable(true);
    }

    private final View.OnClickListener loginClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                dataManager.authUser(emailText.getText().toString(),
                    passwordText.getText().toString(), new DataManager.Callback<User>() {
                        @Override
                        public void onSuccess(final User user) {
                            userManager.setCurrentUser(user);
                            progressDialog.dismiss();
                            enableInputs();

                            final MenuDrawerActivity activity = (MenuDrawerActivity)getActivity();
                            final MenuDrawer menuDrawer = activity.getMenuDrawer();
                            menuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
                                @Override
                                public void onDrawerStateChange(final int oldState, final int newState) {
                                    if (newState == MenuDrawer.STATE_OPEN) {
                                        activity.navigateToActivity(ServiceCategoriesActivity.class);
                                        menuDrawer.setOnDrawerStateChangeListener(null);
                                    }
                                }

                                @Override
                                public void onDrawerSlide(float openRatio, int offsetPixels) {
                                }
                            });
                            activity.getMenuDrawer().openMenu(true);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            progressDialog.dismiss();
                            enableInputs();

                            final HashMap<String, InputTextField> inputMap = new HashMap<>();
                            inputMap.put("password", passwordText);
                            inputMap.put("email", emailText);
                            dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
                        }
                    });
            }
        }
    };

    private final View.OnClickListener forgotClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (emailText.validate()) {
                disableInputs();
                progressDialog.show();

                dataManager.requestPasswordReset(emailText.getText().toString(), new DataManager.Callback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        progressDialog.dismiss();
                        enableInputs();

                        forgotToast.setText(response);
                        forgotToast.show();
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error) {
                        progressDialog.dismiss();
                        enableInputs();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
            }
        }
    };
}
