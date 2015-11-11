package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.InputTextField;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.ui.widget.PasswordInputTextView;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class LoginFragment extends BookingFlowFragment
        implements MenuDrawerActivity.OnDrawerStateChangeListener {
    static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    CallbackManager callbackManager;
    //private UiLifecycleHelper uiHelper;
    private boolean handleFBSessionUpdates = false;
    private boolean findUser;
    private String bookingUserName, bookingUserEmail;
    private BookingRequest bookingRequest;
    private AuthType authType;

    private enum AuthType {EMAIL, FACEBOOK}

    @Bind(R.id.nav_text)
    TextView navText;
    @Bind(R.id.login_button)
    Button loginButton;
    @Bind(R.id.forgot_button)
    Button forgotButton;
    @Bind(R.id.email_text)
    EmailInputTextView emailText;
    @Bind(R.id.password_text)
    PasswordInputTextView passwordText;
    //@Bind(R.id.fb_button)
    //LoginButton fbButton;
    @Bind(R.id.fb_login_button)
    LoginButton fbLoginButton;
    @Bind(R.id.fb_layout)
    View fbLayout;
    @Bind(R.id.or_text)
    TextView orText;
    @Bind(R.id.welcome_text)
    TextView welcomeText;
    @Bind(R.id.menu_button_layout)
    ViewGroup menuButtonLayout;

    public static LoginFragment newInstance(final boolean findUser, final String bookingUserName,
                                     final String bookingUserEmail) {
        final LoginFragment fragment = new LoginFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_FIND_USER, findUser);
        args.putString(EXTRA_BOOKING_USER_NAME, bookingUserName);
        args.putString(EXTRA_BOOKING_EMAIL, bookingUserEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        bookingRequest = bookingManager.getCurrentRequest();
        //uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        //uiHelper.onCreate(savedInstanceState);
        findUser = getArguments().getBoolean(EXTRA_FIND_USER);
        bookingUserName = getArguments().getString(EXTRA_BOOKING_USER_NAME);
        bookingUserEmail = getArguments().getString(EXTRA_BOOKING_EMAIL);

        if (!findUser && bookingUserName == null)
        {
            mixpanel.trackPageLogin();
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_login,container, false);

        ButterKnife.bind(this, view);

        final MenuDrawerActivity activity = (MenuDrawerActivity)getActivity();

        if (findUser) {
            activity.setDrawerDisabled(true);
            navText.setText(getString(R.string.contact));
            passwordText.setVisibility(View.GONE);
            forgotButton.setVisibility(View.GONE);
            loginButton.setText(getString(R.string.next));
            emailText.setText(bookingRequest.getEmail());
            mixpanel.trackEventAppTrackContact();
        }
        else if (bookingUserName != null) {
            activity.setDrawerDisabled(true);
            fbLayout.setVisibility(View.GONE);
            orText.setVisibility(View.GONE);
            emailText.setText(bookingUserEmail);
            welcomeText.setText(String.format(getString(R.string.welcome_back), bookingUserName));
            welcomeText.setVisibility(View.VISIBLE);
            bookingRequest.setEmail(bookingUserEmail);
            mixpanel.trackEventAppTrackLogIn();
        }
        else {
            final MenuButton menuButton = new MenuButton(getActivity(), menuButtonLayout);
            menuButtonLayout.addView(menuButton);
        }

        //fbButton.setFragment(this);
        //fbButton.setReadPermissions("email");

        fbLoginButton.setFragment(this);
        fbLoginButton.setReadPermissions("email");

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(final LoginResult loginResult)
            {
                GraphRequest.newMeRequest( // Request user info from FB through a GraphRequest
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response)
                            {
                                if (response.getError() != null)
                                {
                                    Crashlytics.logException(
                                            new FacebookAuthorizationException(
                                                    response.getError().toString()
                                            )
                                    );
                                    //TODO: Handle error
                                } else
                                {
                                    String fbid = me.optString("id");
                                    String accessToken = loginResult.getAccessToken().getToken();
                                    String email = me.optString("email");
                                    String firstName = me.optString("first_name");
                                    String lastName = me.optString("last_name");
                                    //TODO: Make magic strings -> costants
                                    authType = AuthType.FACEBOOK;
                                    dataManager.authFBUser( // send email and id to your web server
                                            fbid,
                                            accessToken,
                                            email,
                                            firstName,
                                            lastName,
                                            userCallback);
                                }
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel()
            {
                assert true;
                //TODO: Handle case when user cancels Facebook login
            }

            @Override
            public void onError(FacebookException exception)
            {
                Crashlytics.logException(exception);
                progressDialog.dismiss();
                enableInputs();
                toast.setText(R.string.default_error_string);
                toast.show();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginButton.setOnClickListener(loginClicked);
        forgotButton.setOnClickListener(forgotClicked);
    }

    @Override
    public final void onResume() {
        super.onResume();
        //uiHelper.onResume();
        handleFBSessionUpdates = true;
    }

    @Override
    public final void onPause() {
        super.onPause();
        //uiHelper.onPause();
        handleFBSessionUpdates = false;
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        //uiHelper.onDestroy();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        //uiHelper.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, passwordText.isHighlighted());
    }

    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
                                       final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!emailText.validate()) validate = false;
        if (!findUser && !passwordText.validate()) validate = false;
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

                final String email = emailText.getEmail();

                if (findUser) {
                    dataManager.getUser(email, new DataManager.Callback<String>() {
                        @Override
                        public void onSuccess(final String name) {
                            if (!allowCallbacks) return;

                            if (name != null) {
                                final Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra(LoginActivity.EXTRA_BOOKING_USER_NAME, name);
                                intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, email);
                                startActivityForResult(intent, ActivityResult.LOGIN_FINISH);

                                progressDialog.dismiss();
                                enableInputs();
                            }
                            else {
                                bookingRequest.setEmail(email);
                                continueBookingFlow();
                            }
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
                    authType = AuthType.EMAIL;
                    dataManager.authUser(email,
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

                        toast.setText(Html.fromHtml(response).toString());
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

//TODO: Cleanup below
/*
    private final Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            if (!handleFBSessionUpdates || !allowCallbacks) return;

            if (exception instanceof FacebookAuthorizationException) {
                mProgressDialog.dismiss();
                enableInputs();

                mToast.setText(R.string.default_error_string);
                mToast.show();
            }
            else if (session != null && session.isOpened()) {
                disableInputs();
                mProgressDialog.show();

                final Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, final Response response) {
                        if (!allowCallbacks) return;
                        if (response.getError() != null ) {
                            mProgressDialog.dismiss();
                            enableInputs();

                            mToast.setText(R.string.default_error_string);
                            mToast.show();
                            session.closeAndClearTokenInformation();
                        }
                        else if (user != null && user.asMap().get("email") == null) {
                             // TODO if email not given, then deauthorize app
                            mProgressDialog.dismiss();
                            enableInputs();

                            mToast.setText(R.string.default_error_string);
                            mToast.show();
                            session.closeAndClearTokenInformation();
                        }
                        else {
                            authType = AuthType.FACEBOOK;
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
*/

    private final DataManager.Callback<User> userCallback = new DataManager.Callback<User>() {
        @Override
        public void onSuccess(final User user) {
            if (!allowCallbacks) return;

            dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
                @Override
                public void onSuccess(final User user) {
                    if (!allowCallbacks) return;

                    userManager.setCurrentUser(user);

                    if (authType == AuthType.FACEBOOK) {
                        mixpanel.trackEventLoginSuccess(Mixpanel.LoginType.FACEBOOK);
                    } else mixpanel.trackEventLoginSuccess(Mixpanel.LoginType.EMAIL);

/*
                    Session session = Session.getActiveSession();
                    if (session != null) session.closeAndClearTokenInformation();
*/

                    if (bookingUserName != null || authType == AuthType.FACEBOOK && findUser)
                    {
                        continueBookingFlow();
                        return;
                    }

                    progressDialog.dismiss();
                    enableInputs();

                    final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
                    activity.setOnDrawerStateChangedListener(LoginFragment.this);

                    final MenuDrawer menuDrawer = activity.getMenuDrawer();
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

    @Override
    public void onDrawerStateChange(final MenuDrawer menuDrawer, final int oldState,
                                    final int newState) {
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        if (newState == MenuDrawer.STATE_OPEN) {
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
    }

    private void handleUserCallbackError(final DataManager.DataManagerError error) {
        if (authType == AuthType.FACEBOOK) {
            mixpanel.trackEventLoginFailure(Mixpanel.LoginType.FACEBOOK);
        } else mixpanel.trackEventLoginFailure(Mixpanel.LoginType.EMAIL);

        progressDialog.dismiss();
        enableInputs();

/*
        final Session session = Session.getActiveSession();
        if (session != null) session.closeAndClearTokenInformation();
*/

        final HashMap<String, InputTextField> inputMap = new HashMap<>();
        inputMap.put("password", passwordText);
        inputMap.put("email", emailText);
        dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
    }
}
