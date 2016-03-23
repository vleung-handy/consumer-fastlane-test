package com.handybook.handybook.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.handybook.handybook.R;
import com.handybook.handybook.analytics.Mixpanel;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.model.response.UserExistsResponse;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.handybook.handybook.util.ValidationUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class LoginFragment extends BookingFlowFragment
{
    static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    CallbackManager callbackManager;
    //private UiLifecycleHelper mUiHelper;
    private boolean mHandleFBSessionUpdates = false;
    private boolean mFindUser;
    private String mBookingUserName, mBookingUserEmail;
    private BookingRequest mBookingRequest;

    Class<? extends Activity> mDestinationClass;

    @Bind(R.id.nav_text)
    TextView mNavText;
    @Bind(R.id.login_button)
    Button mLoginButton;
    @Bind(R.id.forgot_button)
    Button mForgotButton;
    @Bind(R.id.email_text)
    EmailInputTextView mEmailText;
    @Bind(R.id.password_text)
    PasswordInputTextView mPasswordText;
    //@Bind(R.id.fb_button)
    //LoginButton fbButton;
    @Bind(R.id.fb_login_button)
    LoginButton mFbLoginButton;
    @Bind(R.id.fb_layout)
    View mFbLayout;
    @Bind(R.id.or_text)
    TextView mOrText;
    @Bind(R.id.welcome_text)
    TextView mWelcomeText;
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;

    public static LoginFragment newInstance(
            final boolean findUser, final String bookingUserName,
            final String bookingUserEmail
    )
    {
        final LoginFragment fragment = new LoginFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_FIND_USER, findUser);
        args.putString(EXTRA_BOOKING_USER_NAME, bookingUserName);
        args.putString(EXTRA_BOOKING_EMAIL, bookingUserEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        mBookingRequest = bookingManager.getCurrentRequest();
        //mUiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        //mUiHelper.onCreate(savedInstanceState);
        mFindUser = getArguments().getBoolean(EXTRA_FIND_USER);
        mBookingUserName = getArguments().getString(EXTRA_BOOKING_USER_NAME);
        mBookingUserEmail = getArguments().getString(EXTRA_BOOKING_EMAIL);

        if (!mFindUser && mBookingUserName == null)
        {
            mixpanel.trackPageLogin();
        }

        String mDestinationActivity = getActivity().getIntent().getStringExtra(BundleKeys.ACTIVITY);
        if (!TextUtils.isEmpty(mDestinationActivity))
        {
            try
            {
                mDestinationClass = (Class<? extends Activity>) Class.forName(mDestinationActivity);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();

        if (mFindUser)
        {
            activity.setDrawerDisabled(true);
            mNavText.setText(getString(R.string.contact));
            mPasswordText.setVisibility(View.GONE);
            mForgotButton.setVisibility(View.GONE);
            mLoginButton.setText(getString(R.string.next));
            mEmailText.setText(mBookingRequest.getEmail());
            mixpanel.trackEventAppTrackContact();
        }
        else if (mBookingUserEmail != null)
        {
            activity.setDrawerDisabled(true);
            mFbLayout.setVisibility(View.GONE);
            mOrText.setVisibility(View.GONE);
            mEmailText.setText(mBookingUserEmail);
            if (ValidationUtils.isNullOrEmpty(mBookingUserName))
            {
                mWelcomeText.setVisibility(View.GONE);
            }
            else
            {
                mWelcomeText.setText(String.format(getString(R.string.welcome_back), mBookingUserName));
                mWelcomeText.setVisibility(View.VISIBLE);
            }
            mBookingRequest.setEmail(mBookingUserEmail);
            mixpanel.trackEventAppTrackLogIn();
        }
        else
        {
            final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
            menuButton.setColor(getResources().getColor(R.color.white));
            mMenuButtonLayout.addView(menuButton);
        }

        mFbLoginButton.setFragment(this);
        mFbLoginButton.setReadPermissions("email");

        // Callback registration
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(final LoginResult loginResult)
            {
                final AccessToken accessToken = loginResult.getAccessToken();
                bus.post(new HandyEvent.RequestAuthFacebookUser(accessToken, null));
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
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) { mEmailText.highlight(); }
            if (savedInstanceState.getBoolean(STATE_PASSWORD_HIGHLIGHT))
            {
                mPasswordText.highlight();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mLoginButton.setOnClickListener(loginClicked);
        mForgotButton.setOnClickListener(forgotClicked);
    }

    @Override
    public final void onResume()
    {
        super.onResume();
        //mUiHelper.onResume();
        mHandleFBSessionUpdates = true;
    }

    @Override
    public final void onPause()
    {
        super.onPause();
        //mUiHelper.onPause();
        mHandleFBSessionUpdates = false;
    }

    @Override
    public final void onDestroy()
    {
        super.onDestroy();
        //mUiHelper.onDestroy();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //mUiHelper.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, mEmailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, mPasswordText.isHighlighted());
    }

    @Override
    public final void onActivityResult(
            final int requestCode, final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //mUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateFields()
    {
        if (!mEmailText.validate()) { return false; }
        if (!mFindUser && !mPasswordText.validate()) { return false; }
        return true;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mLoginButton.setClickable(false);
        mForgotButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mLoginButton.setClickable(true);
        mForgotButton.setClickable(true);
    }

    private final View.OnClickListener loginClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (validateFields())
            {
                disableInputs();
                progressDialog.show();

                final String email = mEmailText.getEmail();

                if (mFindUser)
                {
                    dataManager.getUserExists(email, new DataManager.Callback<UserExistsResponse>()
                    {
                        @Override
                        public void onSuccess(final UserExistsResponse userExistsResponse)
                        {
                            if (!allowCallbacks)
                            { return; }

                            if (userExistsResponse.exists())
                            {
                                final Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, email);
                                intent.putExtra(
                                        LoginActivity.EXTRA_BOOKING_USER_NAME,
                                        userExistsResponse.getFirstName()
                                );
                                startActivityForResult(intent, ActivityResult.LOGIN_FINISH);

                                progressDialog.dismiss();
                                enableInputs();
                            }
                            else
                            {
                                mBookingRequest.setEmail(email);
                                continueBookingFlow();
                            }
                        }

                        @Override
                        public void onError(DataManager.DataManagerError error)
                        {
                            if (!allowCallbacks)
                            {
                                return;
                            }
                            dataManagerErrorHandler.handleError(getActivity(), error);
                            enableInputs();
                            progressDialog.dismiss();

                        }
                    });
                }
                else
                {
                    bus.post(new HandyEvent.RequestAuthUser(email, mPasswordText.getPassword()));
                }
            }
        }
    };

    private final View.OnClickListener forgotClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            mEmailText.unHighlight();
            mPasswordText.unHighlight();
            if (mEmailText.validate())
            {
                disableInputs();
                progressDialog.show();

                dataManager.requestPasswordReset(mEmailText.getText().toString(),
                        new DataManager.Callback<String>()
                        {
                            @Override
                            public void onSuccess(final String response)
                            {
                                if (!allowCallbacks) { return; }
                                progressDialog.dismiss();
                                enableInputs();

                                toast.setText(Html.fromHtml(response).toString());
                                toast.show();
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error)
                            {
                                if (!allowCallbacks) { return; }
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
            if (!mHandleFBSessionUpdates || !allowCallbacks) return;

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
                            mAuthType = AuthType.FACEBOOK;
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

    @Subscribe
    public void onReceiveAuthUserSuccess(final HandyEvent.ReceiveAuthUserSuccess event)
    {
        final User user = event.getUser();
        final UserDataManager.AuthType authType = event.getAuthType();
        bus.post(new HandyEvent.RequestUser(user.getId(), user.getAuthToken(), authType));
    }

    @Subscribe
    public void onReceiveAuthUserError(final HandyEvent.ReceiveAuthUserError event)
    {
        handleUserCallbackError(event.error, event.getAuthType());
    }

    @Subscribe
    public void onReceiveUserSuccess(final HandyEvent.ReceiveUserSuccess event)
    {
        final UserDataManager.AuthType authType = event.getAuthType();
        if (authType == UserDataManager.AuthType.FACEBOOK)
        {
            mixpanel.trackEventLoginSuccess(Mixpanel.LoginType.FACEBOOK);
        }
        else
        {
            mixpanel.trackEventLoginSuccess(Mixpanel.LoginType.EMAIL);
        }

        if (mBookingUserName != null ||
                authType == UserDataManager.AuthType.FACEBOOK && mFindUser)
        {
            continueBookingFlow();
            return;
        }

        progressDialog.dismiss();
        enableInputs();

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();

        if (mDestinationClass != null)
        {
            activity.navigateToActivity(mDestinationClass, getActivity().getIntent().getExtras());
        }
        else
        {
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
    }

    @Subscribe
    public void onReceiveUserError(final HandyEvent.ReceiveUserError event)
    {
        handleUserCallbackError(event.error, event.getAuthType());
    }

    private void handleUserCallbackError(
            final DataManager.DataManagerError error,
            final UserDataManager.AuthType authType
    )
    {
        if (authType == UserDataManager.AuthType.FACEBOOK)
        {
            mixpanel.trackEventLoginFailure(Mixpanel.LoginType.FACEBOOK);
        }
        else { mixpanel.trackEventLoginFailure(Mixpanel.LoginType.EMAIL); }

        progressDialog.dismiss();
        enableInputs();

/*
        final Session session = Session.getActiveSession();
        if (session != null) session.closeAndClearTokenInformation();
*/

        dataManagerErrorHandler.handleError(getActivity(), error, null);
    }
}
