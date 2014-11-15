package com.handybook.handybook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class LoginFragment extends BookingFlowFragment {
    static final String EXTRA_IS_FOR_BOOKING = "com.handy.handy.EXTRA_IS_FOR_BOOKING";
    static final String EXTRA_USER_NAME = "com.handy.handy.EXTRA_USER_NAME";
    static final String EXTRA_USER_EMAIL = "com.handy.handy.EXTRA_USER_EMAIL";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    private UiLifecycleHelper uiHelper;
    private boolean handleFBSessionUpdates = false;
    private boolean isForBooking;
    private String userName, userEmail;

    @InjectView(R.id.nav_text) TextView navText;
    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.forgot_button) Button forgotButton;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.password_text) PasswordInputTextView passwordText;
    @InjectView(R.id.fb_button) LoginButton fbButton;
    @InjectView(R.id.or_text) TextView orText;
    @InjectView(R.id.welcome_text) TextView welcomeText;

    static LoginFragment newInstance(final boolean isForBooking, final String userName,
                                     final String userEmail) {
        final LoginFragment fragment = new LoginFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_IS_FOR_BOOKING, isForBooking);
        args.putString(EXTRA_USER_NAME, userName);
        args.putString(EXTRA_USER_EMAIL, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        uiHelper.onCreate(savedInstanceState);
        isForBooking = getArguments().getBoolean(EXTRA_IS_FOR_BOOKING);
        userName = getArguments().getString(EXTRA_USER_NAME);
        userEmail = getArguments().getString(EXTRA_USER_EMAIL);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_login,container, false);

        ButterKnife.inject(this, view);

        if (isForBooking) {
            navText.setText(getString(R.string.contact));
            passwordText.setVisibility(View.GONE);
            forgotButton.setVisibility(View.GONE);
            loginButton.setText(getString(R.string.next));
        }
        else if (userName != null) {
            fbButton.setVisibility(View.GONE);
            orText.setVisibility(View.GONE);
            emailText.setText(userEmail);
            welcomeText.setText(String.format(getString(R.string.welcome_back), userName));
            welcomeText.setVisibility(View.VISIBLE);
        }

        fbButton.setFragment(this);
        fbButton.setReadPermissions("email");

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
    public final void onResume() {
        super.onResume();
        uiHelper.onResume();
        handleFBSessionUpdates = true;
    }

    @Override
    public final void onPause() {
        super.onPause();
        uiHelper.onPause();
        handleFBSessionUpdates = false;
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, passwordText.isHighlighted());
    }

    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
                                       final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!emailText.validate()) validate = false;
        if (!isForBooking && !passwordText.validate()) validate = false;
        return validate;
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        loginButton.setClickable(false);
        forgotButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        loginButton.setClickable(true);
        forgotButton.setClickable(true);
    }

    private final View.OnClickListener loginClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                if (isForBooking) {
                    dataManager.getUser(emailText.getEmail(), new DataManager.Callback<String>() {
                        @Override
                        public void onSuccess(final String name) {
                            if (!allowCallbacks) return;

                            if (name != null) {
                                final Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra(LoginActivity.EXTRA_USER_NAME, name);
                                intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, emailText.getEmail());
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else {
                                Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();
                            enableInputs();
                        }

                        @Override
                        public void onError(DataManager.DataManagerError error) {
                            if (!allowCallbacks) return;
                            progressDialog.dismiss();
                            enableInputs();
                            dataManagerErrorHandler.handleError(getActivity(), error);
                        }
                    });
                }
                else {
                    dataManager.authUser(emailText.getEmail(),
                            passwordText.getPassword(), userCallback);
                }
            }
        }
    };

    private final View.OnClickListener forgotClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            passwordText.unHighlight();
            if (emailText.validate()) {
                disableInputs();
                progressDialog.show();

                dataManager.requestPasswordReset(emailText.getText().toString(),
                        new DataManager.Callback<String>() {
                    @Override
                    public void onSuccess(final String response) {
                        if (!allowCallbacks) return;
                        progressDialog.dismiss();
                        enableInputs();

                        toast.setText(response);
                        toast.show();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) return;
                        progressDialog.dismiss();
                        enableInputs();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
            }
        }
    };

    private final Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            disableInputs();
            progressDialog.show();

            if (!handleFBSessionUpdates || !allowCallbacks) return;

            if (exception instanceof FacebookAuthorizationException) {
                progressDialog.dismiss();
                enableInputs();

                toast.setText(R.string.default_error_string);
                toast.show();
            }
            else if (session != null && session.isOpened()) {
                final Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, final Response response) {
                        if (!allowCallbacks) return;
                        if (response.getError() != null ) {
                            progressDialog.dismiss();
                            enableInputs();

                            toast.setText(R.string.default_error_string);
                            toast.show();
                            session.closeAndClearTokenInformation();
                        }
                        else if (user != null && user.asMap().get("email") == null) {
                             // TODO if email not given, then deauthorize app
                            progressDialog.dismiss();
                            enableInputs();

                            toast.setText(R.string.default_error_string);
                            toast.show();
                            session.closeAndClearTokenInformation();
                        }
                        else {
                            dataManager.authFBUser(user.getId(), session.getAccessToken(),
                                    user.asMap().get("email").toString(),  user.getFirstName(),
                                    user.getLastName(), userCallback);
                        }
                    }
                });
                Request.executeBatchAsync(request);
            }
        }
    };

    private final DataManager.Callback<User> userCallback = new DataManager.Callback<User>() {
        @Override
        public void onSuccess(final User user) {
            if (!allowCallbacks) return;
            dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
                @Override
                public void onSuccess(final User user) {
                    if (!allowCallbacks) return;

                    userManager.setCurrentUser(user);
                    Session session = Session.getActiveSession();
                    if (session != null) session.closeAndClearTokenInformation();

                    if (isForBooking || userName != null) {
                        showBookingAddress();
                        return;
                    }

                    progressDialog.dismiss();
                    enableInputs();

                    final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
                    final MenuDrawer menuDrawer = activity.getMenuDrawer();
                    menuDrawer.setOnDrawerStateChangeListener(
                            new MenuDrawer.OnDrawerStateChangeListener() {
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
                    menuDrawer.openMenu(true);
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    handleUserCallbackError(error);
                }
            });
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;
            handleUserCallbackError(error);
        }
    };

    private void handleUserCallbackError(final DataManager.DataManagerError error) {
        progressDialog.dismiss();
        enableInputs();

        final Session session = Session.getActiveSession();
        if (session != null) session.closeAndClearTokenInformation();

        final HashMap<String, InputTextField> inputMap = new HashMap<>();
        inputMap.put("password", passwordText);
        inputMap.put("email", emailText);
        dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
    }
}
